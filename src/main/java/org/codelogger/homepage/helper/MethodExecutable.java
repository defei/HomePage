package org.codelogger.homepage.helper;

import org.codelogger.homepage.vo.CoverTemplateData;

/**
 * Created by defei on 2/11/17.
 */
public interface MethodExecutable {

    CoverTemplateData execute(MethodExecutorMethodParam param);
}
