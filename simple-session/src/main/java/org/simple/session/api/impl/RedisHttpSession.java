package org.simple.session.api.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.simple.session.api.SessionManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * HttpSession
 *
 * @author clx 2018/4/3.
 */
public class RedisHttpSession implements HttpSession {
	/**
	 * session id
	 */
	private final String id;

	private final long createdAt;

	private volatile long lastAccessedAt;

	private int maxInactiveInterval;

	private final HttpServletRequest request;

	private final SessionManager sessionManager;

	/**
	 * new attributes
	 */
	private final Map<String, Object> newAttributes = Maps.newHashMapWithExpectedSize(5);

	/**
	 * deleted attributes
	 */
	private final Set<String> deleteAttribute = Sets.newHashSetWithExpectedSize(5);

	/**
	 * store attributes
	 */
	private final Map<String, Object> sessionStore;

	/**
	 * true if delete session physically
	 */
	private volatile boolean invalid;

	/**
	 * true if flush session store
	 */
	private volatile boolean dirty;

	public RedisHttpSession(SessionManager sessionManager, HttpServletRequest request, String id) {
		this.request = request;
		this.sessionManager = sessionManager;
		this.id = id;
		this.sessionStore = loadSession();
		this.createdAt = System.currentTimeMillis();
		this.lastAccessedAt = createdAt;
	}

	/**
	 * load session
	 *
	 * @return session
	 */
	private Map<String, Object> loadSession() {
		return this.sessionManager.loadById(this.id);
	}

	@Override
	public long getCreationTime() {
		return createdAt;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedAt;
	}

	@Override
	public ServletContext getServletContext() {
		return request.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		checkValid();
		if (newAttributes.containsKey(name)) {
			return newAttributes.get(name);
		} else if (deleteAttribute.contains(name)) {
			return null;
		}
		return sessionStore.get(name);
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkValid();
		Set<String> names = Sets.newHashSet(sessionStore.keySet());
		names.addAll(newAttributes.keySet());
		names.removeAll(deleteAttribute);
		return Collections.enumeration(names);
	}

	@Override
	public String[] getValueNames() {
		checkValid();
		Set<String> names = Sets.newHashSet(sessionStore.keySet());
		names.addAll(newAttributes.keySet());
		names.removeAll(deleteAttribute);
		return names.toArray(new String[0]);
	}

	@Override
	public void setAttribute(String name, Object value) {
		checkValid();
		if (value != null) {
			newAttributes.put(name, value);
			deleteAttribute.remove(name);
		} else {
			deleteAttribute.add(name);
			newAttributes.remove(name);
		}
		dirty = true;
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		checkValid();
		deleteAttribute.add(name);
		newAttributes.remove(name);
		dirty = true;
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
		dirty = true;
	}

	@Override
	public void invalidate() {
		invalid = true;
		dirty = true;
		sessionManager.deleteById(this.getId());
	}

	@Override
	public boolean isNew() {
		return Boolean.TRUE;
	}

	protected void checkValid() throws IllegalStateException {
		Preconditions.checkState(!invalid);
	}

	public boolean isDirty() {
		return dirty;
	}

	/**
	 * session attributes' snapshot
	 *
	 * @return session attributes' map object
	 */
	public Map<String, Object> snapshot() {
		Map<String, Object> snap = Maps.newHashMap();
		snap.putAll(sessionStore);
		snap.putAll(newAttributes);
		for (String name : deleteAttribute) {
			snap.remove(name);
		}
		return snap;
	}

	public boolean isValid() {
		return !invalid;
	}
}
