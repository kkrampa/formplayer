package beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by willpride on 1/27/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionBean {
    private String caption_audio;
    private String caption;
    private String binding;
    private String caption_image;
    private String question;
    private String required;
    private String relevant;
    private String help;
    private Object answer;
    private String datatype;
    private StyleBean style;
    private String caption_video;
    private String type;
    private String caption_markdown;
    private String ix;
    private String[] choices;
    private QuestionBean[] children;

    public String getCaption_audio() {
        return caption_audio;
    }

    public void setCaption_audio(String caption_audio) {
        this.caption_audio = caption_audio;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getCaption_image() {
        return caption_image;
    }

    public void setCaption_image(String caption_image) {
        this.caption_image = caption_image;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getRelevant() {
        return relevant;
    }

    public void setRelevant(String relevant) {
        this.relevant = relevant;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public StyleBean getStyle() {
        return style;
    }

    public void setStyle(StyleBean style) {
        this.style = style;
    }

    public String getCaption_video() {
        return caption_video;
    }

    public void setCaption_video(String caption_video) {
        this.caption_video = caption_video;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption_markdown() {
        return caption_markdown;
    }

    public void setCaption_markdown(String caption_markdown) {
        this.caption_markdown = caption_markdown;
    }

    public String getIx() {
        return ix;
    }

    public void setIx(String ix) {
        this.ix = ix;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    @Override
    public String toString(){
        return "QuestionBean bind: " + this.getBinding() + " answer: " + this.getAnswer() + ", ix: "+ ix +  ", type: "
                + this.getType() + " children: " +
                Arrays.toString(getChildren());
    }

    public QuestionBean[] getChildren() {
        return children;
    }

    public void setChildren(QuestionBean[] children) {
        this.children = children;
    }
}