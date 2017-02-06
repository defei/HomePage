package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/7/17.
 */
public class HistoryShowcases extends CommonHtmlTemplateData {

    private Showcase mainShowcase;

    private List<Showcase> secondaryShowcases;

    /**
     * {@linkplain HistoryShowcases#mainShowcase}
     */
    public Showcase getMainShowcase() {
        return mainShowcase;
    }

    /**
     * {@linkplain HistoryShowcases#mainShowcase}
     */
    public void setMainShowcase(Showcase mainShowcase) {
        this.mainShowcase = mainShowcase;
    }

    /**
     * {@linkplain HistoryShowcases#secondaryShowcases}
     */
    public List<Showcase> getSecondaryShowcases() {
        return secondaryShowcases;
    }

    /**
     * {@linkplain HistoryShowcases#secondaryShowcases}
     */
    public void setSecondaryShowcases(List<Showcase> secondaryShowcases) {
        this.secondaryShowcases = secondaryShowcases;
    }
}
