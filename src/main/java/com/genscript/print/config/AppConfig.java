package com.genscript.print.config;

/**
 * 应用配置类
 * 管理应用的基本配置信息
 * 
 * @author Magic_yuan
 * @version 2.0.0
 */
public class AppConfig {

    public static final String APP_NAME = "Print Tool";

    public static final String APP_VERSION = "v2.0.0";

    public static final String APP_AUTHOR = "Magic_yuan";

    public static final String APP_DESCRIPTION = "SCM打印控件";

    public static final String APP_FUNCTION = "跨浏览器打印解决方案";

    public static final String COPYRIGHT = "© 2025 Magic_yuan. All rights reserved.";

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