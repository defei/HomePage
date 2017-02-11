package org.codelogger.homepage.service;

import org.codelogger.core.utils.JsonUtils;
import org.codelogger.homepage.bean.ProgramRo;
import org.codelogger.homepage.vo.CoverTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
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

    private CoverTemplateData getCoverTemplateDataFromCache(Long coverId) {
        CoverTemplateData coverTemplateData = coverTemplateDataCache.get(coverId);
        if (coverTemplateData == null) {
            synchronized (CoverDataProvider.class){
                coverTemplateData = coverTemplateDataCache.get(coverId);
                if(coverTemplateData == null){
                    //TODO 从数据库查询
                    coverTemplateData = new CoverTemplateData();
                    List<ProgramRo> programsOfCover = programService.getProgramsByCoverId(coverId);
                    for (ProgramRo programRo : programsOfCover) {
                        String dataJson = programRo.getDataJson();
                        coverTemplateData.add(programRo.getSourceId(), JsonUtils.fromJson(dataJson, CoverTemplateData.class));
                    }
                    coverTemplateDataCache.put(coverId, coverTemplateData);
                }
            }
        }
        return coverTemplateData;
    }

    public CoverTemplateData getCoverTemplateData(Long coverId) {
        return getCoverTemplateDataFromCache(coverId);
    }

    private static final Map<Long, CoverTemplateData> coverTemplateDataCache = newHashMap();

    @Autowired
    private ProgramService programService;
}
