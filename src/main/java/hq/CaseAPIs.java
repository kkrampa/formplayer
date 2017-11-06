package hq;

import api.process.FormRecordProcessorHelper;
import beans.CaseBean;
import engine.FormplayerTransactionParserFactory;
import exceptions.SQLiteRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commcare.cases.model.Case;
import org.commcare.core.parse.ParseUtils;
import org.commcare.modern.database.TableBuilder;
import org.javarosa.core.api.ClassNameHasher;
import org.javarosa.core.model.User;
import org.javarosa.core.services.storage.IStorageIterator;
import org.javarosa.core.util.externalizable.PrototypeFactory;
import org.javarosa.xml.util.InvalidStructureException;
import org.javarosa.xml.util.UnfullfilledRequirementsException;
import org.sqlite.SQLiteException;
import org.xmlpull.v1.XmlPullParserException;
import sandbox.SqliteIndexedStorageUtility;
import sandbox.UserSqlSandbox;
import services.RestoreFactory;
import util.SimpleTimer;
import util.UserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by willpride on 1/7/16.
 */
public class CaseAPIs {

    private static final Log log = LogFactory.getLog(CaseAPIs.class);

    public static class TimedSyncResult {
        private UserSqlSandbox sandbox;
        private SimpleTimer purgeCasesTimer;

        private TimedSyncResult(UserSqlSandbox sandbox, SimpleTimer purgeCasesTimer) {
            this.sandbox = sandbox;
            this.purgeCasesTimer = purgeCasesTimer;
        }

        public UserSqlSandbox getSandbox() {
            return sandbox;
        }

        public SimpleTimer getPurgeCasesTimer() {
            return purgeCasesTimer;
        }
    }

    public static UserSqlSandbox performSync(RestoreFactory restoreFactory) throws Exception {
        return performTimedSync(restoreFactory).getSandbox();
    }

    // This function will only wipe user DBs when they have expired, otherwise will incremental sync
    public static TimedSyncResult performTimedSync(RestoreFactory restoreFactory) throws Exception {
        // Create parent dirs if needed
        if(restoreFactory.getSqlSandbox().getLoggedInUser() != null){
            restoreFactory.getSQLiteDB().createDatabaseFolder();
        }
        UserSqlSandbox sandbox = restoreUser(restoreFactory);
        SimpleTimer timer = new SimpleTimer();
        timer.start();
        FormRecordProcessorHelper.purgeCases(sandbox);
        timer.end();
        return new TimedSyncResult(sandbox, timer);
    }

    // This function will attempt to get the user DBs without syncing if they exist, sync if not
    public static UserSqlSandbox getSandbox(RestoreFactory restoreFactory) throws Exception {
        if(restoreFactory.getSqlSandbox().getLoggedInUser() != null
                && !restoreFactory.isRestoreXmlExpired()){
            return restoreFactory.getSqlSandbox();
        } else {
            restoreFactory.getSQLiteDB().createDatabaseFolder();
            return restoreUser(restoreFactory);
        }
    }

    public static CaseBean getFullCase(String caseId, SqliteIndexedStorageUtility<Case> caseStorage){
        Case cCase = caseStorage.getRecordForValue("case-id", caseId);
        return new CaseBean(cCase);
    }

    private static UserSqlSandbox restoreUser(RestoreFactory restoreFactory) throws
            UnfullfilledRequirementsException, InvalidStructureException, IOException, XmlPullParserException {
        PrototypeFactory.setStaticHasher(new ClassNameHasher());
        int maxRetries = 2;
        int counter = 0;
        while (true) {
            try {
                UserSqlSandbox sandbox = restoreFactory.getSqlSandbox();
                FormplayerTransactionParserFactory factory = new FormplayerTransactionParserFactory(sandbox, true);
                restoreFactory.setAutoCommit(false);
                ParseUtils.parseIntoSandbox(restoreFactory.getRestoreXml(), factory, true, true);
                restoreFactory.commit();
                restoreFactory.setAutoCommit(true);
                sandbox.writeSyncToken();
                return sandbox;
            } catch (InvalidStructureException | SQLiteRuntimeException e) {
                // Rollback changes
                restoreFactory.rollback();
                restoreFactory.setAutoCommit(true);
                restoreFactory.getSQLiteDB().deleteDatabaseFile();
                restoreFactory.getSQLiteDB().createDatabaseFolder();
                if (e instanceof InvalidStructureException || ++counter >= maxRetries) {
                    // If restore payload is invalid or we've exceeded retries, throw exception
                    throw e;
                } else {
                    log.info(String.format("Retrying restore for user %s after receiving exception.",
                            restoreFactory.getEffectiveUsername()),
                            e);
                }
            }
        }
    }
}
