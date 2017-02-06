package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/5/17.
 */
public class NavigationComponent implements HtmlTemplateSelectable {

    private String sid;

    private List<Navigation> navigations;

    public NavigationComponent() {
    }

    public NavigationComponent(String sid) {
        this.sid = sid;
    }

    /**
     * {@linkplain NavigationComponent#sid}
     */
    public String getSid() {
        return sid;
    }

    /**
     * {@linkplain NavigationComponent#sid}
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * {@linkplain NavigationComponent#navigations}
     */
    public List<Navigation> getNavigations() {
        return navigations;
    }

    /**
     * {@linkplain NavigationComponent#navigations}
     */
    public void setNavigations(List<Navigation> navigations) {
        this.navigations = navigations;
    }
}
