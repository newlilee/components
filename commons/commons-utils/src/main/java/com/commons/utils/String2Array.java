package com.commons.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chenlixin at 2016年5月10日 下午3:49:41
 */
public class String2Array {

    public static List<String> split(String v, char split) {
        String[] ss = StringUtils.split(v, split);
        if (ss == null || ss.length == 0) {
            return Collections.emptyList();
        }

        List<String> retVal = new ArrayList<String>();
        for (String s : ss) {
            s = StringUtils.trimToNull(s);
            if (s != null) {
                retVal.add(s);
            }
        }

        if (retVal.isEmpty()) {
            return Collections.emptyList();
        }
        return retVal;
    }
}
