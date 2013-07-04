package org.gaixie.jibu.utils;

import org.gaixie.jibu.cache.Cache;
import org.gaixie.jibu.cache.DefaultCache;

/**
 * Cache 工具类，以懒加载方式得到需要的 Cache。
 * <p>
 *
 */
public final class CacheUtils {

    static class AuthCacheHolder {
        static Cache instance = new DefaultCache("cache.authCache",
                                                 256 * 1024l,
                                                 -1l);
    }

    static class UserCacheHolder {
        static Cache instance = new DefaultCache("cache.userCache",
                                                 128 * 1024l,
                                                 1000 * 60 * 30l);
    }

    /**
     * 得到一个保存有 Authority 信息的 Cache。
     * <p>
     * Auth Cache 最大为 256k, 不过期。
     *
     * @return 实例化的 Cache
     */
    public static Cache getAuthCache() {
        return AuthCacheHolder.instance;
    }

    /**
     * 得到一个保存有 User 信息的 Cache。
     * <p>
     * User Cache 最大为 128k, 有效期为 30 分钟，即一个用户信息，
     * 在此Cache中存在的最长时间为 30 分钟。
     *
     * @return 实例化 Cache
     */
    public static Cache getUserCache() {
        return UserCacheHolder.instance;
    }

}
