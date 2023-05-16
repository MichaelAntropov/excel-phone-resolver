package com.hizencode.excelphoneresolver.ui.theme;

import com.hizencode.excelphoneresolver.main.App;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ThemeService {

    private static final ObjectProperty<Theme> currentThemeProperty = new SimpleObjectProperty<>();

    static {
        currentThemeProperty.addListener((observableValue, theme, t1) -> {
            var cssUrl = ThemeService.class.getResource(currentThemeProperty.get().getCssPath());
            if (cssUrl != null) {
                App.setUserAgentStylesheet(cssUrl.toExternalForm());
            }
        });
    }

    public static ObjectProperty<Theme> getCurrentThemeProperty() {
        return currentThemeProperty;
    }
}
