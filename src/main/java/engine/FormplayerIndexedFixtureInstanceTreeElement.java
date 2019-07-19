package engine;

import org.commcare.cases.instance.IndexedFixtureInstanceTreeElement;
import org.commcare.cases.model.StorageIndexedTreeElementModel;
import org.commcare.core.interfaces.UserSandbox;
import org.javarosa.core.model.IndexedFixtureIdentifier;
import org.javarosa.core.model.instance.AbstractTreeElement;
import org.javarosa.core.model.instance.InstanceBase;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.services.storage.IStorageUtilityIndexed;
import org.javarosa.core.util.externalizable.DeserializationException;

import util.SerializationUtil;

public class FormplayerIndexedFixtureInstanceTreeElement extends IndexedFixtureInstanceTreeElement {

    private TreeElement attributes;

    private FormplayerIndexedFixtureInstanceTreeElement(AbstractTreeElement instanceRoot,
                                                        IStorageUtilityIndexed<StorageIndexedTreeElementModel> storage,
                                                        IndexedFixtureIdentifier indexedFixtureIdentifier) {
        super(instanceRoot, storage, indexedFixtureIdentifier);
    }

    public static IndexedFixtureInstanceTreeElement get(UserSandbox sandbox,
                                                        String instanceName,
                                                        InstanceBase instanceBase) {
        IndexedFixtureIdentifier indexedFixtureIdentifier = sandbox.getIndexedFixtureIdentifier(instanceName);
        if (indexedFixtureIdentifier == null) {
            return null;
        } else {
            IStorageUtilityIndexed<StorageIndexedTreeElementModel> storage =
                    sandbox.getIndexedFixtureStorage(instanceName);
            return new FormplayerIndexedFixtureInstanceTreeElement(instanceBase, storage, indexedFixtureIdentifier);
        }
    }

    protected synchronized TreeElement loadAttributes() {
        if (attributes == null) {
            try {
                attributes = SerializationUtil.deserialize(attrHolder, TreeElement.class);
            } catch (Exception e) {
                if (e.getCause() instanceof DeserializationException) {
                    String newMessage = "Deserialization failed for indexed fixture root atrribute wrapper: " + e.getMessage();
                    throw new RuntimeException(newMessage);
                }
                throw e;
            }
        }
        return attributes;
    }
}
