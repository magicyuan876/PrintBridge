package com.genscript.print.config;

import com.genscript.print.i18n.I18nManager;

/**
 * 应用配置类
 * 管理应用的基本配置信息
 * 
 * @author Magic_yuan
 * @version 2.1.0
 */
public class AppConfig {

    // 静态常量
    public static final String APP_NAME = "PrintBridge";
    public static final String APP_VERSION = "v2.1.0";
    public static final String APP_AUTHOR = "Magic_yuan";

    // 国际化字符串(通过方法获取,支持动态切换语言)
    public static String getAppDescription() {
        return I18nManager.getString("app.description");
    }

    public static String getAppFunction() {
        return I18nManager.getString("app.function");
    }

    public static String getCopyright() {
        return I18nManager.getString("app.copyright");
    }

    // 为了兼容性,保留常量形式(但使用方法更好)
    @Deprecated
    public static final String APP_DESCRIPTION = "PrintBridge - Connecting Web Apps to Local Printers";
    
    @Deprecated
    public static final String APP_FUNCTION = "Cross-browser Print Middleware";
    
    @Deprecated
    public static final String COPYRIGHT = "© 2020-2025 Magic_yuan. All rights reserved.";

    // UI配置
    public static final int WINDOW_WIDTH = 800;

    public static final int WINDOW_HEIGHT = 600;

    public static final int WINDOW_X = 200;

    public static final int WINDOW_Y = 200;

    // 颜色配置
    public static final class Colors {

        public static final java.awt.Color BACKGROUND = new java.awt.Color(248, 249, 250);

        public static final java.awt.Color WHITE = java.awt.Color.WHITE;

        public static final java.awt.Color BORDER = new java.awt.Color(229, 231, 235);

        public static final java.awt.Color TEXT_PRIMARY = new java.awt.Color(31, 41, 55);

        public static final java.awt.Color TEXT_SECONDARY = new java.awt.Color(107, 114, 128);

        public static final java.awt.Color BUTTON_PRIMARY = new java.awt.Color(59, 130, 246);

        public static final java.awt.Color STATUS_STARTING = new java.awt.Color(249, 115, 22);

        public static final java.awt.Color STATUS_SUCCESS = new java.awt.Color(34, 197, 94);

        public static final java.awt.Color STATUS_ERROR = new java.awt.Color(239, 68, 68);

        public static final java.awt.Color LIST_BACKGROUND = new java.awt.Color(249, 250, 251);
    }

    // 字体配置
    public static final class Fonts {

        public static final String FONT_FAMILY = "Microsoft YaHei";

        public static final String FONT_FAMILY_SANS = "SansSerif";

        public static final int TITLE_SIZE = 18;

        public static final int SUBTITLE_SIZE = 16;

        public static final int NORMAL_SIZE = 13;

        public static final int SMALL_SIZE = 12;
    }
}