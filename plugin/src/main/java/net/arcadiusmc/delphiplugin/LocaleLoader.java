package net.arcadiusmc.delphiplugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocaleLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger("LocaleLoader");

  static final Set<Locale> LOCALES = Set.of(Locale.ENGLISH);
  static final Key KEY = Key.key("delphi", "messages");

  static void loadFrom(Path pluginDataPath, TranslationStore<Component> store) {
    Path dataPath = pluginDataPath.resolve("data").resolve("lang");
    if (!Files.isDirectory(dataPath)) {
      return;
    }

    try (var stream = Files.newDirectoryStream(dataPath, "*.properties")) {
      for (Path path : stream) {
        String fName = path.getFileName().toString().replace(".properties", "");
        Locale locale = Locale.forLanguageTag(fName);

        try (InputStream inStream = Files.newInputStream(path)) {
          loadTranslations(store, inStream, locale);
        } catch (IOException exc) {
          LOGGER.error("Failed to read translations from {}:", path, exc);
        }
      }
    } catch (IOException exc) {
      LOGGER.error("Failed load language from {} directory", dataPath, exc);
    }
  }

  static void load(Path pluginPath) {
    TranslationStore<Component> store = TranslationStore.component(KEY);
    GlobalTranslator.translator().addSource(store);

    for (Locale locale : LOCALES) {
      loadLocale(locale, store);
    }

    loadFrom(pluginPath, store);
  }

  private static void loadLocale(Locale locale, TranslationStore<Component> registry) {
    String filePath = "lang/" + locale.toLanguageTag() + ".properties";
    URL resource = LocaleLoader.class.getClassLoader().getResource(filePath);

    if (resource == null) {
      LOGGER.warn("Failed to find translations file for {}", locale);
      return;
    }

    try (InputStream inStream = resource.openStream()) {
      loadTranslations(registry, inStream, locale);
    } catch (IOException e) {
      LOGGER.error("Failed to load locale from {} (language: {})", resource, locale, e);
    }
  }

  private static void loadTranslations(TranslationStore<Component> registry, InputStream stream, Locale locale) {
    Properties properties;

    try {
      properties = new Properties();
      properties.load(stream);
    } catch (IOException e) {
      LOGGER.error("Failed to load translations for locale {}: ", locale, e);
      return;
    }

    for (Object o : properties.keySet()) {
      String key = String.valueOf(o);
      String value = properties.getProperty(key, "");
      Component base = MiniMessage.miniMessage().deserialize(value);

      if (registry.contains(key)) {
        registry.unregister(key);
      }

      registry.register(key, locale, base);
    }
  }
}
