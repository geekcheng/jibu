package org.gaixie.jibu.security.service.impl;

import org.gaixie.jibu.utils.ConnectionUtils;
import org.gaixie.jibu.security.service.MonitorService;

/**
 * 系统监控服务接口的默认实现。
 * <p>
 */
public class MonitorServiceImpl implements MonitorService {

    public int getMaxActive() {
        return ConnectionUtils.getMaxActive();
    }

    public int getNumActive() {
        return ConnectionUtils.getNumActive();
    }

    public int getNumIdle() {
        return ConnectionUtils.getNumIdle();
    }
}