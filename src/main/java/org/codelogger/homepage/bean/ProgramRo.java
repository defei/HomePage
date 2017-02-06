package org.codelogger.homepage.bean;

import org.apache.commons.collections.ComparatorUtils;

import java.util.Comparator;

/**
 * 区域首页里的功能模块
 * Created by defei on 5/13/15.
 */
public class ProgramRo implements Comparable<ProgramRo>, HtmlTemplateSelectable {

    private static final long serialVersionUID = 1106021018539020040L;

    private String id;

    private String sid;

    private String coverId;

    private ProgramDataType type;

    private String html;

    private String dataIds;

    private String dataJson;

    private Boolean hasMore;

    private String moreLink;

    private Integer idx;

    private CommonStatus status = CommonStatus.ENABLE;

    private String description;

    private String title;

    private String template;

    @SuppressWarnings("unchecked")
    public int compareTo(ProgramRo o) {
        if (o == null){
            return 1;
        } else {
            Comparator nullLowComparator = ComparatorUtils.nullLowComparator(null);
            int compare = nullLowComparator.compare(idx, o.idx);
            return compare == 0 ? nullLowComparator.compare(o.type, type) : compare;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@linkplain ProgramRo#sid}
     */
    public String getSid() {
        return sid;
    }

    /**
     * {@linkplain ProgramRo#sid}
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    /**
     * {@linkplain ProgramRo#type}
     */
    public ProgramDataType getType() {
        return type;
    }

    /**
     * {@linkplain ProgramRo#type}
     */
    public void setType(ProgramDataType type) {
        this.type = type;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getDataIds() {
        return dataIds;
    }

    public void setDataIds(String dataIds) {
        this.dataIds = dataIds;
    }

    /**
     * {@linkplain ProgramRo#dataJson}
     */
    public String getDataJson() {
        return dataJson;
    }

    /**
     * {@linkplain ProgramRo#dataJson}
     */
    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getMoreLink() {
        return moreLink;
    }

    public void setMoreLink(String moreLink) {
        this.moreLink = moreLink;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    /**
     * {@linkplain ProgramRo#status}
     */
    public CommonStatus getStatus() {
        return status;
    }

    /**
     * {@linkplain ProgramRo#status}
     */
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
