package org.codelogger.homepage.service;

import org.codelogger.core.utils.JsonUtils;
import org.codelogger.homepage.bean.*;
import org.codelogger.homepage.vo.CoverTemplateData;
import org.codelogger.utils.MathUtils;
import org.codelogger.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by defei on 2/8/17.
 */
@Service
public class CoverDataProvider {

    private static final Map<Long, CoverTemplateData> coverTemplateDataCache = newHashMap();

    public static void push(Long coverId, String sourceKey, CoverTemplateData sourceData){

        CoverTemplateData coverTemplateData = getCoverTemplateDataFromCache(coverId);
        coverTemplateData.add(sourceKey, sourceData);
    }

    private static CoverTemplateData getCoverTemplateDataFromCache(Long coverId) {
        CoverTemplateData coverTemplateData = coverTemplateDataCache.get(coverId);
        if (coverTemplateData == null) {
            synchronized (CoverDataProvider.class){
                coverTemplateData = coverTemplateDataCache.get(coverId);
                if(coverTemplateData == null){
                    coverTemplateData = new CoverTemplateData();
                    coverTemplateDataCache.put(coverId, coverTemplateData);
                }
            }
        }
        return coverTemplateData;
    }

    public CoverTemplateData getCoverTemplateData(Long id) {
        return getCoverTemplateDataFromCache(id);
    }
}
