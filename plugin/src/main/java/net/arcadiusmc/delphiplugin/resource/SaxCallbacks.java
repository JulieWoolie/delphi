package net.arcadiusmc.delphiplugin.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.arcadiusmc.delphi.util.Nothing;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.parser.ParserCallbacks;
import net.arcadiusmc.dom.Document;
import org.bukkit.Bukkit;

public class SaxCallbacks implements ParserCallbacks {

  static final String ON_DOM_INIT = "onDomInitialize";

  @Override
  public boolean isPluginEnabled(String pluginName) {
    return Bukkit.getPluginManager().isPluginEnabled(pluginName);
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
