package org.codelogger.homepage.bean;

/**
 * Created by defei on 2/5/17.
 */
public class Cover {

    private Long id;

    private String name;

    private String description;

    private CoverClientType clientType = CoverClientType.ALL;

    private AppPlatform clientPlatform = AppPlatform.ALL;

    private VersionCompareStrategy versionCompareStrategy = VersionCompareStrategy.ALL;

    private Integer index = 0;

    private CommonStatus status = CommonStatus.ENABLE;

    private String appVersion;

    private String template;

    private String url;

    /**
     * {@linkplain Cover#id}
     */
    public Long getId() {
        return id;
    }

    /**
     * {@linkplain Cover#id}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@linkplain Cover#description}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@linkplain Cover#description}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@linkplain Cover#clientType}
     */
    public CoverClientType getClientType() {
        return clientType;
    }

    /**
     * {@linkplain Cover#clientType}
     */
    public void setClientType(CoverClientType clientType) {
        this.clientType = clientType;
    }

    /**
     * {@linkplain Cover#name}
     */
    public String getName() {
        return name;
    }

    /**
     * {@linkplain Cover#name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@linkplain Cover#index}
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * {@linkplain Cover#index}
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * {@linkplain Cover#status}
     */
    public CommonStatus getStatus() {
        return status;
    }

    /**
     * {@linkplain Cover#status}
     */
    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public AppPlatform getClientPlatform() {
        return clientPlatform;
    }

    public void setClientPlatform(AppPlatform clientPlatform) {
        this.clientPlatform = clientPlatform;
    }

    /**
     * {@linkplain Cover#versionCompareStrategy}
     */
    public VersionCompareStrategy getVersionCompareStrategy() {
        return versionCompareStrategy;
    }

    /**
     * {@linkplain Cover#versionCompareStrategy}
     */
    public void setVersionCompareStrategy(VersionCompareStrategy versionCompareStrategy) {
        this.versionCompareStrategy = versionCompareStrategy;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
