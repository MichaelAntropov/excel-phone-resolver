package com.hizencode.excelphoneresolver.ui.theme;

public enum Theme {
    LIGHT("NORD_LIGHT", "theme.nord.light", "/css/nord-light.css"),
    DARK("NORD_DARK", "theme.nord.dark", "/css/nord-dark.css");

    public static final Theme DEFAULT_THEME = Theme.LIGHT;

    private final String name;

    private final String i18nProperty;

    private final String cssPath;

    Theme(String name, String i18nProperty, String cssPath) {
        this.name = name;
        this.i18nProperty = i18nProperty;
        this.cssPath = cssPath;
    }

    public String getName() {
        return name;
    }

    public String getI18nProperty() {
        return i18nProperty;
    }

    public String getCssPath() {
        return cssPath;
    }
}
