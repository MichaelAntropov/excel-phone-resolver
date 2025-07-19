package com.hizencode.excelphoneresolver.ui.theme;

import java.util.Objects;

public enum Theme {
    NORD_LIGHT("NORD_LIGHT", "theme.nord.light", "/css/nord-light.css"),
    NORD_DARK("NORD_DARK", "theme.nord.dark", "/css/nord-dark.css");

    public static final Theme DEFAULT_THEME = Theme.NORD_LIGHT;

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

    public static Theme fromString(String themeName) {
        for (var theme : values()) {
            if (Objects.equals(theme.getName(), themeName)) {
                return theme;
            }
        }
        return DEFAULT_THEME;
    }
}
