package org.codelogger.homepage.helper;

import java.util.List;

/**
 * Created by defei on 2/11/17.
 */
public class MethodExecutorMethodParam {

    private String identity;

    private List<String> dataIds;

    public MethodExecutorMethodParam() {
    }

    public MethodExecutorMethodParam(String identity, List<String> dataIds) {
        this.identity = identity;
        this.dataIds = dataIds;
    }

    /**
     * {@linkplain MethodExecutorMethodParam#identity}
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * {@linkplain MethodExecutorMethodParam#identity}
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * {@linkplain MethodExecutorMethodParam#dataIds}
     */
    public List<String> getDataIds() {
        return dataIds;
    }

    /**
     * {@linkplain MethodExecutorMethodParam#dataIds}
     */
    public void setDataIds(List<String> dataIds) {
        this.dataIds = dataIds;
    }
}
