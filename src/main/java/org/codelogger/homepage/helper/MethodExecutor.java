package org.codelogger.homepage.helper;

import org.codelogger.homepage.vo.CoverTemplateData;

import java.lang.reflect.Method;

import static org.codelogger.utils.ExceptionUtils.iae;

/**
 * Created by defei on 2/10/17.
 */
public class MethodExecutor {

    public static final String EXECUTE_METHOD_NAME = "execute";

    private Object bean;

    private Method method;

    private MethodExecutorMethodParam param;

    public MethodExecutor(String bean, MethodExecutorMethodParam executorMethodParam) {
        iae.throwIfNull(bean, "Argument bean is null.");
        MethodExecutable beanEntity = BeanDiscoverer.getBean(bean);
        try {
            Class<?> beanClass = beanEntity.getClass();
            method = beanClass.getMethod(EXECUTE_METHOD_NAME, MethodExecutorMethodParam.class);
        } catch (Throwable e) {
            throw new RuntimeException("Method not found.", e);
        }
        this.bean = beanEntity;
        this.param = executorMethodParam;
    }

    public CoverTemplateData execute() {

        try {
            return (CoverTemplateData) method.invoke(bean, param);
        } catch (Throwable e) {
            return null;
        }
    }
}
