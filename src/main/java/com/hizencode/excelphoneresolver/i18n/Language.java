package com.hizencode.excelphoneresolver.i18n;

import java.util.Locale;

public enum Language {

    ENGLISH("English", new Locale("en")),
    UKRAINIAN("Українська", new Locale("uk"));

    private final String langName;

    private final Locale locale;

    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;

    Language(String langName, Locale locale) {
        this.langName = langName;
        this.locale = locale;
    }

    public String getLangName() {
        return langName;
    }

    public Locale getLocale() {
        return locale;
    }
}
