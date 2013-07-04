package org.gaixie.jibu.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResouceBundle 工具类。
 * <p>
 */
public class BundleHandler {
    private static final Logger logger = LoggerFactory.getLogger(BundleHandler.class);
    ResourceBundle bundle;

    public BundleHandler(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n/JibuResources",locale);
    }

    /**
     * 和 ResouceBundle.getString() 方法一样，不过当 key 不存在时，直接返回 key。
     * <p>
     */
    public String get(String key) {
        try { 
            return bundle.getString(key); 
        }catch(MissingResourceException e) { 
            logger.error("Missing resource.", e);
            return key; 
        }
    }
}