package org.codelogger.homepage.bean;

/**
 * Created by defei on 2/6/17.
 */
public class CommonHtmlTemplateData implements HtmlTemplateSelectable, HtmlImageAndLink, HtmlTextAndLink {

    /**
     * 选择器id
     */
    private String sid;

    /**
     * 文字
     */
    private String text;

    /**
     * 次要文字
     */
    private String secondaryText;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 链接
     */
    private String link;

    /**
     * css样式
     */
    private String styleClass;

    /**
     * 排序
     */
    private Integer orderIndex;

    /**
     * 状态
     */
    private CommonStatus commonStatus;

    public CommonHtmlTemplateData() {
    }

    public CommonHtmlTemplateData(String sid) {
        this.sid = sid;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#sid}
     */
    public String getSid() {
        return sid;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#sid}
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#text}
     */
    public String getText() {
        return text;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#text}
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#secondaryText}
     */
    public String getSecondaryText() {
        return secondaryText;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#secondaryText}
     */
    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#imgUrl}
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#imgUrl}
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#link}
     */
    public String getLink() {
        return link;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#link}
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#styleClass}
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#styleClass}
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#orderIndex}
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#orderIndex}
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#commonStatus}
     */
    public CommonStatus getCommonStatus() {
        return commonStatus;
    }

    /**
     * {@linkplain CommonHtmlTemplateData#commonStatus}
     */
    public void setCommonStatus(CommonStatus commonStatus) {
        this.commonStatus = commonStatus;
    }

}
