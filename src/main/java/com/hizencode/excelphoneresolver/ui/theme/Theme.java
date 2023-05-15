package com.hizencode.excelphoneresolver.ui.theme;

public enum Theme {
    LIGHT("NORD_LIGHT", "/css/nord-light.css"),
    DARK("NORD_DARK","/css/nord-dark.css");

    public static final Theme DEFAULT_THEME = Theme.LIGHT;

    private final String name;

    private final String cssPath;

    Theme(String name, String cssPath) {
        this.name = name;
        this.cssPath = cssPath;
    }

    public String getName() {
        return name;
    }

    public String getCssPath() {
        return cssPath;
    }
}
