package com.communication.server.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.communication.server.constant.Constant;

public class ServerSessionManager implements ISessionManager {
	private HashMap<Integer, CSession> mSessions = new HashMap<Integer, CSession>();

	private AtomicInteger mConnectionsCounter = new AtomicInteger(0);
	
	private volatile static ServerSessionManager instance;

	public static ServerSessionManager getInstance() {
		if (instance == null) {
			synchronized (ServerSessionManager.class) {
				if (instance == null) {
					instance = new ServerSessionManager();
				}
			}
		}
		return instance;
	}

	public void addSession(int port, CSession session) {
		if (session != null) {
			session.setAttribute(Constant.SESSION_KEY, port);
			mSessions.put(port, session);
			mConnectionsCounter.incrementAndGet();
		}
	}

	public void closeSession(int port) {
		if (null != mSessions.get(port)) {
			mSessions.get(port).close(true);
			mSessions.remove(port);
		}
	}
	
	public void closeAllSession() {
		for (Map.Entry<Integer, CSession> entry : mSessions.entrySet()) {
			closeSession(entry.getKey());
		}
	}
	
	public CSession getSession(int port) {
		return mSessions.get(port);
	}

	public Collection<CSession> getSessions() {
		return mSessions.values();
	}

	public void removeSession(CSession session) {
		mSessions.remove(session.getAttribute(Constant.SESSION_KEY));
	}

	public void removeSession(int port) {
		mSessions.remove(port);
	}

	public boolean containsCIMSession(int port) {
		return mSessions.containsKey(port);
	}

	public Integer getPort(CSession ios) {
		if (ios.getAttribute(Constant.SESSION_KEY) == null) {
			for (Integer key : mSessions.keySet()) {
				if (mSessions.get(key).equals(ios) || mSessions.get(key).getGid() == ios.getGid()) {
					return key;
				}
			}
		} else {
			return (Integer) ios.getAttribute(Constant.SESSION_KEY);
		}
		return null;
	}

	@Override
	public void updateSession(CSession session) {
		mSessions.put(session.getPort(), session);
	}

	@Override
	public void setInvalid(int port) {
		mSessions.get(port).setStatus(CSession.STATUS_DISENABLE);
	}
	
	public boolean isConnected() {
		return mSessions.size() != 0;
	}
}
