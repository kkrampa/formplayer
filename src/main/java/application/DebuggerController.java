package application;

import annotations.UserLock;
import auth.DjangoAuth;
import beans.*;
import beans.debugger.DebuggerFormattedQuestionsResponseBean;
import beans.debugger.XPathQueryItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import objects.SerializableFormSession;
import org.javarosa.xpath.expr.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import qa.QATestRunner;
import repo.SerializableMenuSession;
import services.FormattedQuestionsService;
import session.FormSession;
import util.Constants;

import javax.annotation.Resource;
import java.util.List;

/**
 * Controller class for all routes pertaining to the CloudCare Debugger
 */
@Api(value = "Debugger Controller", description = "Operations involving the CloudCare Debugger")
@RestController
@EnableAutoConfiguration
public class DebuggerController extends AbstractBaseController {

    private int MAX_RECENT = 5;

    @Autowired
    private FormattedQuestionsService formattedQuestionsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource(name="redisTemplate")
    private ListOperations<String, XPathQueryItem> listOperations;

    @ApiOperation(value = "Get formatted questions and instance xml")
    @RequestMapping(value = Constants.URL_DEBUGGER_FORMATTED_QUESTIONS, method = RequestMethod.POST)
    public DebuggerFormattedQuestionsResponseBean getFormattedQuesitons(
            @RequestBody SessionRequestBean debuggerRequest,
            @CookieValue(Constants.POSTGRES_DJANGO_SESSION_ID) String authToken) throws Exception {

        SerializableFormSession serializableFormSession = formSessionRepo.findOneWrapped(debuggerRequest.getSessionId());
        FormSession formSession = new FormSession(serializableFormSession);
        SerializableMenuSession serializableMenuSession = menuSessionRepo.findOne(serializableFormSession.getMenuSessionId());
        FormattedQuestionsService.QuestionResponse response = formattedQuestionsService.getFormattedQuestions(
                debuggerRequest.getDomain(),
                serializableMenuSession.getAppId(),
                formSession.getXmlns(),
                formSession.getInstanceXml(),
                new DjangoAuth(authToken)
        );
        return new DebuggerFormattedQuestionsResponseBean(
                serializableMenuSession.getAppId(),
                formSession.getXmlns(),
                formSession.getInstanceXml(),
                response.getFormattedQuestions(),
                response.getQuestionList(),
                FunctionUtils.xPathFuncList(),
                fetchRecentXPathQueries(debuggerRequest.getDomain(), debuggerRequest.getUsername())
        );
    }

    @ApiOperation(value = "Evaluate the given XPath under the current context")
    @RequestMapping(value = Constants.URL_EVALUATE_XPATH, method = RequestMethod.POST)
    @ResponseBody
    @UserLock
    public EvaluateXPathResponseBean evaluateXpath(@RequestBody EvaluateXPathRequestBean evaluateXPathRequestBean) throws Exception {
        SerializableFormSession serializableFormSession = formSessionRepo.findOneWrapped(evaluateXPathRequestBean.getSessionId());
        FormSession formEntrySession = new FormSession(serializableFormSession);
        EvaluateXPathResponseBean evaluateXPathResponseBean =
                new EvaluateXPathResponseBean(formEntrySession, evaluateXPathRequestBean.getXpath());

        cacheXPathQuery(
                evaluateXPathRequestBean.getDomain(),
                evaluateXPathRequestBean.getUsername(),
                evaluateXPathRequestBean.getXpath(),
                evaluateXPathResponseBean.getOutput(),
                evaluateXPathResponseBean.getStatus()
        );

        return evaluateXPathResponseBean;
    }

    @ApiOperation(value = "Run the given QA plan")
    @RequestMapping(value = Constants.URL_DEBUGGER_RUN_QA, method = RequestMethod.POST)
    @ResponseBody
    public RunQAResponseBean runQAPlan(@RequestBody RunQARequestBean qaRequestBean) throws Exception {
        QATestRunner qaTestRunner = new QATestRunner(qaRequestBean.getQaPlan(), qaRequestBean.getAppId(), qaRequestBean.getDomain(),
                qaRequestBean.getUsername(), qaRequestBean.getPassword());
        return new RunQAResponseBean(qaTestRunner);
    }

    private void cacheXPathQuery(String domain, String username, String xpath, String output, String status) {
        XPathQueryItem queryItem = new XPathQueryItem(xpath, output, status);

        listOperations.leftPush(
                redisXPathKey(domain, username),
                queryItem
        );
    }

    private List<XPathQueryItem> fetchRecentXPathQueries(String domain, String username) {
        listOperations.trim(redisXPathKey(domain, username), 0, MAX_RECENT);
        return listOperations.range(redisXPathKey(domain, username), 0, MAX_RECENT);
    }

    private String redisXPathKey(String domain, String username) {
        return "debugger:xpath:" + domain + ":" + username;
    }
}
