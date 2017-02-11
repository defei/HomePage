package org.codelogger.homepage.web.controller;

import com.google.common.base.Stopwatch;
import org.codelogger.homepage.bean.CoverClientType;
import org.codelogger.homepage.service.CoverService;
import org.codelogger.homepage.vo.CoverReqVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by defei on 2/5/17.
 */
@Controller public class HomepageController {

    @RequestMapping(value = {"/", "welcome"})
    public void forwardToHomePage(HttpServletRequest httpRequest, HttpServletResponse response) {

        CoverReqVo coverReqVo = new CoverReqVo();
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            String homepageContent = coverService
                .getHomePageHtmlContentByCityIdAndShopTypeId(coverReqVo, CoverClientType.WEB);
            logger.debug("Get homepage from soa used {} milliseconds.",
                stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (isNotBlank(homepageContent)) {
                try {
                    response.setContentType("text/html");
                    response.setCharacterEncoding("utf-8");
                    if (logger.isTraceEnabled()) {
                        logger.trace(homepageContent);
                    }
                    response.getWriter().write(homepageContent);
                } catch (IOException e) {
                    logger.info("can not render homepage.", e);
                }
            }
        } catch (Exception e) {
            logger.info("render cover homepage failed.", e);
        }
        logger.debug("Cover homepage is blank, will to response list by category.");
    }

    @Autowired private CoverService coverService;

    private static final Logger logger = LoggerFactory.getLogger(HomepageController.class);
}
