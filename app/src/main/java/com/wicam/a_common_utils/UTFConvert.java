package com.wicam.a_common_utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Hyeonmin on 2015-07-17.
 */
public class UTFConvert {

    // 안드로이드 스튜디오가 UTF_8을 빨간줄 처리하므로, 전체 프로젝트에서 이곳에서만 뜨게 하기 위해 클래스화

    public String convert(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str.replace("'", ""), "UTF_8");
    }
}
