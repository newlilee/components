package com.components.id.util;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @author clx at 2017年8月23日 下午4:38:18
 */
public class EncryptorUtils {

	private static final String PASSWD = "clx";
	private static BasicTextEncryptor encryptor = new BasicTextEncryptor();

	static {
		encryptor.setPassword(PASSWD);
	}

	/**
	 * encrypt
	 *
	 * @param message
	 * @return
	 */
	public static String getEncryptionString(String message) {
		if (StringUtils.isBlank(message)) {
			return "";
		}
		try {
			String encryptString = encryptor.encrypt(message);
			return StringUtils.defaultString(encryptString, "");
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * decrypt
	 *
	 * @param encryptedMessage
	 * @return
	 */
	public static String getDecryptionString(String encryptedMessage) {
		if (StringUtils.isBlank(encryptedMessage)) {
			return "";
		}
		try {
			String decryptString = encryptor.decrypt(encryptedMessage);
			return StringUtils.defaultString(decryptString, "");
		} catch (Exception ex) {
			return "";
		}
	}

	public static void main(String[] args) {
		String message = "clx";
		String encryptedStr = "GyzYFy1CtPVafS3FWzBhvw==";

		String result = getEncryptionString(message);
		String decryptStr = getDecryptionString(encryptedStr);

		System.out.println(result);
		System.out.println(decryptStr);
	}
}
