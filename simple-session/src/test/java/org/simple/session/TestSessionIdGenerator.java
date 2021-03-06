package org.simple.session;

import java.util.UUID;

/**
 * @author clx 2018/5/3.
 */
public class TestSessionIdGenerator {

	public static void main(String[] args) {
		String uuid = UUID.randomUUID().toString();
		System.out.println(uuid);
		System.out.println(uuid.substring(0, 4));
		String hexTime = Long.toHexString(System.currentTimeMillis());
		System.out.println(System.currentTimeMillis());
		System.out.println(hexTime);
		System.out.println(hexTime.length());
	}
}
