package org.simple.session;

import org.simple.session.util.PropertiesReaderUtils;

import java.util.Properties;

/**
 * @author clx 2018/5/3.
 */
public class TestPropertiesReaderUtils {

	public static void main(String[] args) {
		String path = "test.properties";
		Properties properties = PropertiesReaderUtils.read(path);
		System.out.println("value:" + properties.getProperty("test"));
	}
}
