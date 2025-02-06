package net.arcadiusmc.delphiplugin.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.arcadiusmc.delphi.util.Nothing;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.parser.ElementInputConsumer;
import net.arcadiusmc.delphidom.parser.ParserCallbacks;
import net.arcadiusmc.dom.ComponentElement;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.ItemElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

public class SaxCallbacks implements ParserCallbacks {

  private static final Logger LOGGER = Loggers.getDocumentLogger();
  static final String ON_DOM_INIT = "onDomInitialize";

  @Override
  public boolean isPluginEnabled(String pluginName) {
    return Bukkit.getPluginManager().isPluginEnabled(pluginName);
  }

  @Override
  public ElementInputConsumer<ItemElement> createItemJsonParser() {
    return new ElementInputConsumer<ItemElement>() {
      @Override
      public void consume(String input, ItemElement element) {
        PageResources.parseItem(input)
            .mapError(e -> "Failed to load item: " + e.getMessage())
            .ifError(LOGGER::error)
            .ifSuccess(element::setItemStack);
      }
    };
  }

  @Override
  public ElementInputConsumer<ComponentElement> createTextJsonParser() {
    return new ElementInputConsumer<ComponentElement>() {
      @Override
      public void consume(String input, ComponentElement element) {
        JsonElement jsonElement;
        try {
          jsonElement = JsonParser.parseString(input);
        } catch (JsonParseException exc) {
          LOGGER.error("Failed to read chat component JSON from {}", input, exc);
          return;
        }

        Component text;
        try {
          text = GsonComponentSerializer.gson().deserializeFromTree(jsonElement);
        } catch (JsonParseException exc) {
          LOGGER.error("Bad JSON component: {}", input, exc);
          return;
        }

        element.setContent(text);
      }
    };
  }

  @Override
  public Result<Nothing, Exception> loadDomClass(Document document, String className) {
    Class<?> jtype;

    try {
      jtype = Class.forName(className, true, getClass().getClassLoader());
    } catch (ClassNotFoundException e) {
      return Result.err(e);
    }

    Method initializerMethod = findDomInitializerMethod(jtype);
    if (initializerMethod != null) {
      try {
        initializerMethod.invoke(null, document);
      } catch (InvocationTargetException | IllegalAccessException e) {
        return Result.err(e);
      }

      return Result.nothing();
    }

    Constructor<?> ctor = findConstructor(jtype);
    if (ctor == null) {
      return Result.err(new NoSuchMethodException());
    }

    try {
      if (ctor.getParameterCount() == 1) {
        ctor.newInstance(document);
      } else {
        ctor.newInstance();
      }
    } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
      return Result.err(e);
    }

    return Result.nothing();
  }

  private Constructor<?> findConstructor(Class<?> jtype) {
    try {
      return jtype.getConstructor(Document.class);
    } catch (NoSuchMethodException exc) {
      return null;
    }
  }

  private Method findDomInitializerMethod(Class<?> jtype) {
    Method initializerMethod;

    try {
      initializerMethod = jtype.getMethod(ON_DOM_INIT, Document.class);
      if (!Modifier.isStatic(initializerMethod.getModifiers())) {
        return null;
      }

      return initializerMethod;
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
