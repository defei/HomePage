package org.codelogger.homepage.helper;

import org.codelogger.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.codelogger.utils.ExceptionUtils.iae;

/**
 * Created by defei on 2/10/17.
 */
public class BeanDiscoverer {

    public static void put(String key, MethodExecutable bean){

        iae.throwIfTrue(StringUtils.isBlank(key), "Argument key is blank.");
        iae.throwIfNull(bean, "Argument bean is blank.");
        if(keyToBean.containsKey(key)){
            logger.warn("Bean:[{}] for key:[{}] is duplicate.", bean.getClass(), key);
        }
        keyToBean.put(key, bean);
    }

    public static MethodExecutable getBean(String key){
        MethodExecutable bean = keyToBean.get(key);
        if(bean == null){
            logger.warn("Bean not found for key:[{}] is duplicate.", key);
        }
        return bean;
    }

    private static final Map<String, MethodExecutable> keyToBean = newHashMap();

    private static final Logger logger = LoggerFactory.getLogger(BeanDiscoverer.class);
}
