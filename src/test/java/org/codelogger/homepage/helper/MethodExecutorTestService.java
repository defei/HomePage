package org.codelogger.homepage.helper;

import org.codelogger.homepage.vo.CoverTemplateData;

import java.io.Serializable;
import java.util.List;

import static org.codelogger.utils.PrintUtils.println;

/**
 * Created by defei on 2/10/17.
 */
public class MethodExecutorTestService implements MethodExecutable {

    public CoverTemplateData execute(MethodExecutorMethodParam param) {
        println("----- just do it !!! ----");
        println(param.getIdentity());
        List<? extends Serializable> dataIds = param.getDataIds();
        println(dataIds);
        return new CoverTemplateData();
    }
}
