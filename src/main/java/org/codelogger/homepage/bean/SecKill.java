package org.codelogger.homepage.bean;

import java.util.List;

/**
 * Created by defei on 2/5/17.
 */
public class SecKill extends CommonHtmlTemplateData {

    private Long remainingSeconds;

    private List<Showcase> showcases;

    /**
     * {@linkplain SecKill#remainingSeconds}
     */
    public Long getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * {@linkplain SecKill#remainingSeconds}
     */
    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    /**
     * {@linkplain SecKill#showcases}
     */
    public List<Showcase> getShowcases() {
        return showcases;
    }

    /**
     * {@linkplain SecKill#showcases}
     */
    public void setShowcases(List<Showcase> showcases) {
        this.showcases = showcases;
    }

}
