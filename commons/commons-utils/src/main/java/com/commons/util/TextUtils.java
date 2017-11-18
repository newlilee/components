package com.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author clx 2017/11/18 23:30
 */
public class TextUtils {

	public static String[] appendSenSeparator(String sentence, String separator) {
		if (StringUtils.isBlank(sentence) || StringUtils.isBlank
				(separator)) {
			return null;
		}
		List<String> separatorList = new ArrayList<>(16);
		for (int idx = 0; idx < separator.length(); idx++) {
			separatorList.add(String.valueOf(separator.charAt(idx)));
		}

		Map<Integer, String> separatorMap = new HashMap<>(16);
		for (String sep : separatorList) {
			int startIdx = StringUtils.indexOf(sentence, sep);
			while (startIdx != -1) {
				separatorMap.put(startIdx, sep);
				startIdx = StringUtils.indexOf(sentence, sep, startIdx + 1);
			}
		}

		String retStr = null;
		int lastSenLength = 0;
		List<String> retSen = new ArrayList<>(16);
		String[] sentences = StringUtils.split(sentence, separator);
		for (String sen : sentences) {
			int keyIdx = lastSenLength + sen.length();
			if (StringUtils.isNotBlank(separatorMap.get(keyIdx))) {
				retStr = sen + separatorMap.get(keyIdx);
			} else {
				retStr = sen;
			}
			retSen.add(retStr);
			lastSenLength += sen.length() + 1;
		}
		if (retSen.size() > 0) {
			return retSen.toArray(new String[sentences.length]);
		}
		return null;
	}
}
