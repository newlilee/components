package org.simple.session.api.impl;

import com.google.common.hash.Hashing;
import org.simple.session.api.SessionIdGenerator;
import org.simple.session.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Default SessionId Generator
 *
 * @author clx 2018/4/3.
 */
public class DefaultSessionIdGenerator implements SessionIdGenerator {

	public static final Character SEPARATOR = 'S';

	private final String hostIpMd5;

	public DefaultSessionIdGenerator() {
		String hostIp;
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			hostIp = UUID.randomUUID().toString();
		}
		hostIpMd5 = Hashing.sha256().hashString(hostIp, StandardCharsets.UTF_8).toString().substring(0, 8);
	}

	/**
	 * generate session id
	 */
	@Override
	public String generate(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder(36);
		String remoteIpMd5 = Hashing.sha256().hashString(WebUtils.getClientIpAddr(request), StandardCharsets.UTF_8).toString()
				.substring(0, 8);
		builder.append(remoteIpMd5).append(SEPARATOR).append(hostIpMd5).append(SEPARATOR)
				.append(Long.toHexString(System.currentTimeMillis())).append(SEPARATOR)
				.append(UUID.randomUUID().toString().substring(0, 4));
		return builder.toString();
	}
}
