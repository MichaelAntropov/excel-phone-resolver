package com.hizencode.excelphoneresolver.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18NService {

    private static final ObjectProperty<Language> currentLanguageProperty = new SimpleObjectProperty<>();

    private static ResourceBundle currentResourceBundle;

    static {
        currentLanguageProperty.addListener((observableValue, language, t1) ->
                currentResourceBundle = ResourceBundle
                        .getBundle("/i18n/language", currentLanguageProperty.get().getLocale())
        );
    }

    public static ObjectProperty<Language> getCurrentLanguageProperty() {
        return currentLanguageProperty;
    }

    public static ResourceBundle getCurrentResourceBundle() {
        return currentResourceBundle;
    }

    public static String get(final String key) {
        try {
            return currentResourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
