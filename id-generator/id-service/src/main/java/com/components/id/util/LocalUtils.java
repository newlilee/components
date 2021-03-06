package com.components.id.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author clx at 2017年7月11日 下午6:29:59
 */
public class LocalUtils {

	/**
	 * return local ips
	 *
	 * @return
	 */
	public static List<String> getLocalIPs() {
		try {
			List<String> localHost = new ArrayList<String>();
			String ip = InetAddress.getLocalHost().getHostAddress();
			localHost.add(ip);

			Enumeration<NetworkInterface> inets = NetworkInterface
					.getNetworkInterfaces();
			while (inets.hasMoreElements()) {
				Enumeration<InetAddress> addresses = inets.nextElement()
						.getInetAddresses();
				while (addresses.hasMoreElements()) {
					String hostAddress = addresses.nextElement()
							.getHostAddress();
					if (!hostAddress.equals(ip) && hostAddress.indexOf(":") < 0
							&& !hostAddress.equals("127.0.0.1")) {
						localHost.add(hostAddress);
					}
				}
			}
			if (!localHost.isEmpty()) {
				Collections.sort(localHost);
			}
			return localHost;
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	public static void main(String[] args) {
		System.out.println(LocalUtils.getLocalIPs());
	}
}
