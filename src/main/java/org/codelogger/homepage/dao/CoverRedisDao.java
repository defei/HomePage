package org.codelogger.homepage.dao;

import org.codelogger.homepage.bean.Cover;
import org.codelogger.homepage.bean.CoverClientType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by defei on 2/5/17.
 */
@Component
public class CoverRedisDao {

    public List<Cover> getAllCovers(CoverClientType coverClientType) {
        ArrayList<Cover> covers = newArrayList();
        Cover cover = new Cover();
        cover.setUrl("http://1919.codelogger.org/home/homewine.html");
        covers.add(cover);
        return covers;
    }

    public List<Cover> getAllCovers() {
        return newArrayList();
    }
}
