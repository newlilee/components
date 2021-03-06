package com.components.id.utils.test;

import com.components.id.util.String2Array;

/**
 * @author chenlixin at 2016年5月10日 下午4:03:51
 */
public class String2ArrayTest {

	public static void main(String[] args) {
		char character = ',';
		String v = "a,b,c,d,e";
		System.out.println(String2Array.split(v, character));
	}
}
