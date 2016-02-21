package beans;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * Created by willpride on 1/12/16.
 */
public class CaseFilterFullResponseBean {
    CaseBean[] cases; // comma separated case list

    public CaseFilterFullResponseBean(){

    }
    public CaseFilterFullResponseBean(CaseBean[] caseBeans) throws Exception{
        cases = caseBeans;
    }

    public CaseBean[] getCases() {
        return cases;
    }
}
