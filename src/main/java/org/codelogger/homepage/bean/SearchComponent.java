package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/5/17.
 */
public class SearchComponent implements HtmlTemplateSelectable {

    private String sid;

    private String searchPlaceHolder;

    private List<Navigation> hotKeys;

    public SearchComponent() {
    }

    public SearchComponent(String sId) {
        this.sid = sId;
    }

    /**
     * {@linkplain SearchComponent#searchPlaceHolder}
     */
    public String getSearchPlaceHolder() {
        return searchPlaceHolder;
    }

    /**
     * {@linkplain SearchComponent#searchPlaceHolder}
     */
    public void setSearchPlaceHolder(String searchPlaceHolder) {
        this.searchPlaceHolder = searchPlaceHolder;
    }

    /**
     * {@linkplain SearchComponent#hotKeys}
     */
    public List<Navigation> getHotKeys() {
        return hotKeys;
    }

    /**
     * {@linkplain SearchComponent#hotKeys}
     */
    public void setHotKeys(List<Navigation> hotKeys) {
        this.hotKeys = hotKeys;
    }

    public String getSid() {
        return sid;
    }

    /**
     * {@linkplain SearchComponent#sid}
     */
    public void setSid(String sid) {
        this.sid = sid;
    }
}
