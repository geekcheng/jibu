package org.gaixie.jibu.security.servlet;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * 统计当前活动的 Session 数目。
 * <p>
 */
public class SessionCounter implements HttpSessionListener {

    private static int activeSessions = 0;

    public void sessionCreated(HttpSessionEvent se) {
        activeSessions++;
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        // 如果容器有session恢复功能，可能会导致activeSessions <0，如tomcat 的 saveOnRestart
        if(activeSessions>0) {
            activeSessions--;
        }
    }

    public static int getActiveSessions() {
        return activeSessions;
    }
}