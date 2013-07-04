package org.gaixie.jibu.security.service;

/**
 * 系统监控服务接口。
 * <p>
 */
public interface MonitorService {

    /**
     * 得到数据库连接池的最大 Active 连接数。
     * <p>
     *
     */
    public int getMaxActive();

    /**
     * 得到数据库连接池的当前 Active 连接数。
     * <p>
     *
     */
    public int getNumActive();

    /**
     * 得到数据库连接池的当前 Idle 连接数。
     * <p>
     *
     */
    public int getNumIdle();
}