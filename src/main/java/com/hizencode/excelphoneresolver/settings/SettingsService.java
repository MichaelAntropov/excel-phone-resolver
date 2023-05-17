package com.hizencode.excelphoneresolver.settings;

import com.hizencode.excelphoneresolver.i18n.I18NService;
import com.hizencode.excelphoneresolver.i18n.Language;
import com.hizencode.excelphoneresolver.main.App;
import com.hizencode.excelphoneresolver.ui.theme.Theme;
import com.hizencode.excelphoneresolver.ui.theme.ThemeService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class SettingsService {

    public static void loadSettings() throws IOException {
        try {
            Path settingsLocation = Paths.get(System.getProperty("user.home"), ".excel-phone-resolver", "settings", "settings.cfg");
            if (!Files.exists(settingsLocation)) {

                if (!Files.exists(settingsLocation.getParent())) {
                    Files.createDirectories(settingsLocation.getParent());
                }
                Files.copy(
                        Objects.requireNonNull(SettingsService.class.getResourceAsStream("/settings/default-settings.cfg")),
                        settingsLocation
                );
            }

            var settingsProperties = new Properties();
            try (var in = Files.newInputStream(settingsLocation)) {
                settingsProperties.load(in);
                readProperties(settingsProperties);
            }

        } catch (IOException e) {
            loadDefault();
            throw new IOException("Failed to load user settings!" , e);
        }
    }

    public static void saveSettings() throws IOException {
        Path settingsLocation = Paths.get(System.getProperty("user.home"), ".excel-phone-resolver", "settings", "settings.cfg");

        try (var out = new FileOutputStream(settingsLocation.toFile())) {
            Properties settingsProperties = new Properties();
            writeProperties(settingsProperties);
            settingsProperties.store(out, App.CLIENT_VERSION);
        }
    }

    private static void loadDefault() throws IOException {
        var defaultSettings = new Properties();
        defaultSettings.load(SettingsService.class.getResourceAsStream("/settings/default-settings.cfg"));
        readProperties(defaultSettings);
    }

    private static void readProperties(Properties properties) {
        I18NService.getCurrentLanguageProperty().set(Language.valueOf(properties.getProperty("language")));
        ThemeService.getCurrentThemeProperty().set(Theme.valueOf(properties.getProperty("theme")));
    }

    private static void writeProperties(Properties properties) {
        properties.setProperty("language", I18NService.getCurrentLanguageProperty().get().name());
        properties.setProperty("theme", ThemeService.getCurrentThemeProperty().get().name());
    }
}
