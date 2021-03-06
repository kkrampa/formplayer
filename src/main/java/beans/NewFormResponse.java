package beans;

import beans.menus.EntityDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import session.FormSession;

import java.io.IOException;

/**
 * Created by willpride on 1/12/16.
 */
public class NewFormResponse extends SessionResponseBean {
    private QuestionBean[] tree;
    private String[] langs;
    private String[] breadcrumbs;
    private EntityDetailResponse persistentCaseTile;
    private QuestionBean event;
    public NewFormResponse(){}

    public NewFormResponse(FormSession fes) throws IOException {
        this.tree = new ObjectMapper().readValue(fes.getFormTree().toString(), QuestionBean[].class);
        this.langs = fes.getLanguages();
        this.title = fes.getTitle();
        this.sessionId = fes.getSessionId();
        this.sequenceId = fes.getSequenceId();
        this.instanceXml = new InstanceXmlBean(fes);
    }

    public QuestionBean[] getTree(){
        return tree;
    }

    public String[] getLangs(){
        return langs;
    }

    public String getSession_id(){return sessionId;}

    public String toString(){
        return "NewFormResponse [sessionId=" + sessionId + ", title=" + title + "]";
    }

    public String[] getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(String[] breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public EntityDetailResponse getPersistentCaseTile() {
        return persistentCaseTile;
    }

    public void setPersistentCaseTile(EntityDetailResponse persistentCaseTile) {
        this.persistentCaseTile = persistentCaseTile;
    }

    public QuestionBean getEvent() {
        return event;
    }

    public void setEvent(QuestionBean event) {
        this.event = event;
    }

    public void setTree(QuestionBean[] tree) {
        this.tree = tree;
    }
}
