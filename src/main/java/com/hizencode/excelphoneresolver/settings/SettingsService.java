package com.hizencode.excelphoneresolver.settings;

import com.hizencode.excelphoneresolver.i18n.I18NService;
import com.hizencode.excelphoneresolver.i18n.Language;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.theme.Theme;
import com.hizencode.excelphoneresolver.ui.theme.ThemeService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SettingsService {

    private static final ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private static final ObjectProperty<Language> language = new SimpleObjectProperty<>();

    static  {
        theme.bindBidirectional(ThemeService.getCurrentThemeProperty());
        language.bindBidirectional(I18NService.getCurrentLanguageProperty());
    }

    public static void loadDefault() {
        theme.set(Theme.DEFAULT_THEME);
        language.set(Language.DEFAULT_LANGUAGE);
    }

    public static void loadSettings() throws URISyntaxException {
        var appFolder = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath();
        var settingsFile = appFolder.resolve("settings").resolve("settings.cfg").toFile();

        try (var input = new FileInputStream(settingsFile)) {
            Properties settingsProperties = new Properties();
            settingsProperties.load(input);

            var langProperty = settingsProperties.getProperty("language", Language.DEFAULT_LANGUAGE.name());
            language.set(Language.valueOf(langProperty));

            var themeProperty = settingsProperties.getProperty("theme", Theme.DEFAULT_THEME.name());
            theme.set(Theme.valueOf(themeProperty));

        } catch (IOException e) {
            loadDefault();
        }
    }

    public static void saveSettings() throws URISyntaxException, IOException {
        var appFolder = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toPath();
        var settingsFolder = appFolder.resolve("settings");
        Files.createDirectories(settingsFolder);
        Path settingsFilePath = settingsFolder.resolve("settings.cfg");
        try {
            Files.createFile(settingsFilePath);
        } catch (FileAlreadyExistsException e) {
            // Just continue
        }

        try (var out = new FileOutputStream(settingsFilePath.toFile())) {
            Properties settingsProperties = new Properties();

            settingsProperties.setProperty("language", language.get().name());
            settingsProperties.setProperty("theme", theme.get().name());

            settingsProperties.store(out, null);
        }
    }
}
