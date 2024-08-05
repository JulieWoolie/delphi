package net.arcadiusmc.delphiplugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocaleLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger("LocaleLoader");

  static final Set<Locale> LOCALES = Set.of(Locale.ENGLISH);
  static final Key KEY = Key.key("delphi", "messages");

  static void load() {
    TranslationRegistry registry = TranslationRegistry.create(KEY);
    registry.defaultLocale(Locale.ENGLISH);

    for (Locale locale : LOCALES) {
      loadLocale(locale, registry);
    }

    GlobalTranslator.translator().addSource(registry);
  }

  private static void loadLocale(Locale locale, TranslationRegistry registry) {
    String filePath = "lang/" + locale.toLanguageTag() + ".properties";
    URL resource = LocaleLoader.class.getClassLoader().getResource(filePath);

    if (resource == null) {
      LOGGER.warn("Failed to find translations file for {}", locale);
      return;
    }

    Properties properties;

    try (InputStream stream = resource.openStream()) {
      properties = new Properties();
      properties.load(stream);
    } catch (IOException e) {
      LOGGER.error("Failed to load translations for locale {}: ", locale, e);
      return;
    }

    for (Object o : properties.keySet()) {
      String key = String.valueOf(o);
      String value = properties.getProperty(key, "");

      MessageFormat format;

      try {
        format = new MessageFormat(value);
      } catch (IllegalArgumentException exc) {
        LOGGER.error("Malformed translation value {}, in {}:", key, filePath, exc);
        continue;
      }

      registry.register(key, locale, format);
    }
  }
}
