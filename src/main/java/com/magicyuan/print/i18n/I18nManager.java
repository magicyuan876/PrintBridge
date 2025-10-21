package com.magicyuan.print.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 国际化管理器
 * 负责加载和管理多语言资源
 * 
 * @author Magic_yuan
 * @version 2.1.0
 */
public class I18nManager {

    private static final Logger logger = LoggerFactory.getLogger(I18nManager.class);
    
    private static final String BUNDLE_NAME = "i18n.messages";
    private static ResourceBundle resourceBundle;
    private static Locale currentLocale;

    static {
        // 初始化时自动加载系统语言
        initializeWithSystemLocale();
    }

    /**
     * 使用系统默认语言初始化
     */
    private static void initializeWithSystemLocale() {
        Locale systemLocale = Locale.getDefault();
        String language = systemLocale.getLanguage();
        
        // 根据系统语言选择合适的Locale
        if ("zh".equals(language)) {
            currentLocale = Locale.SIMPLIFIED_CHINESE;
            logger.info("检测到中文系统,使用中文界面");
        } else {
            currentLocale = Locale.US;
            logger.info("Detected non-Chinese system, using English interface");
        }
        
        loadResourceBundle();
    }

    /**
     * 加载资源文件
     */
    private static void loadResourceBundle() {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
            logger.info("成功加载语言资源: {}", currentLocale);
        } catch (MissingResourceException e) {
            logger.error("无法加载语言资源文件: {}", e.getMessage());
            // 降级到默认Locale
            try {
                resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.US);
                logger.warn("降级使用英文资源文件");
            } catch (MissingResourceException ex) {
                logger.error("严重错误: 无法加载任何语言资源文件");
            }
        }
    }

    /**
     * 设置语言
     * 
     * @param locale 目标语言
     */
    public static void setLocale(Locale locale) {
        if (locale == null) {
            logger.warn("尝试设置null Locale,忽略");
            return;
        }
        
        currentLocale = locale;
        loadResourceBundle();
        logger.info("语言已切换为: {}", locale);
    }

    /**
     * 获取当前语言
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * 获取国际化字符串
     * 
     * @param key 资源键
     * @return 国际化后的字符串
     */
    public static String getString(String key) {
        try {
            if (resourceBundle == null) {
                logger.warn("ResourceBundle未初始化,返回key: {}", key);
                return key;
            }
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            logger.warn("找不到资源key: {}", key);
            return key; // 返回key本身作为降级
        }
    }

    /**
     * 获取格式化的国际化字符串
     * 
     * @param key 资源键
     * @param args 格式化参数
     * @return 格式化后的字符串
     */
    public static String getString(String key, Object... args) {
        try {
            String pattern = getString(key);
            return String.format(pattern, args);
        } catch (Exception e) {
            logger.warn("格式化字符串失败,key: {}", key, e);
            return key;
        }
    }

    /**
     * 检查是否为中文环境
     */
    public static boolean isChinese() {
        return currentLocale != null && 
               "zh".equals(currentLocale.getLanguage());
    }

    /**
     * 检查是否为英文环境
     */
    public static boolean isEnglish() {
        return currentLocale != null && 
               "en".equals(currentLocale.getLanguage());
    }
}

