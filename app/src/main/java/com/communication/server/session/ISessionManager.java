package com.communication.server.session;

import java.util.Collection;

/**
 * 客户端的 session管理接口 可自行实现此接口管理session
 */

public interface ISessionManager {

	/**
	 * 添加新的session
	 */
	public void addSession(int port, CSession session);

	/**
	 * 添加新的session
	 */
	public void updateSession(CSession session);

	/**
	 * 
	 * @param port
	 *            客户端session的 key 一般可用port来对应session
	 * @return
	 */
	CSession getSession(int port);

	/**
	 * 获取所有session
	 * 
	 * @return
	 */
	public Collection<CSession> getSessions();

	/**
	 * 删除session
	 * 
	 * @param session
	 */
	public void removeSession(int port);

	/**
	 * 删除session
	 * 
	 * @param session
	 */
	public void setInvalid(int port);

	/**
	 * session是否存在
	 * 
	 * @param session
	 */
	public boolean containsCIMSession(int port);
}