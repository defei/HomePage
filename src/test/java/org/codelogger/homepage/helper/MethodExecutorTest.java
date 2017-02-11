package org.codelogger.homepage.helper;

import org.codelogger.homepage.vo.CoverTemplateData;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

/**
 * Created by defei on 2/10/17.
 */
public class MethodExecutorTest {

    MethodExecutor methodExecutor;

    @Before
    public void setup(){
        String methodExecutorTestService = "methodExecutorTestService";
        BeanDiscoverer.put(methodExecutorTestService, new MethodExecutorTestService());
        MethodExecutorMethodParam executorMethodParam = new MethodExecutorMethodParam("testIdentity", newArrayList("1", "2", "3"));
        methodExecutor = new MethodExecutor(methodExecutorTestService, executorMethodParam);
    }

    @Test
    public void justDoIt(){

        CoverTemplateData coverTemplateData = methodExecutor.execute();
        assertNotNull(coverTemplateData);
    }

}