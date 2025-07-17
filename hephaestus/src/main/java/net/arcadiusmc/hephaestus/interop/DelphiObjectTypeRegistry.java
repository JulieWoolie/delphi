package net.arcadiusmc.hephaestus.interop;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;
import net.arcadiusmc.delphidom.Loggers;
import org.slf4j.Logger;

public class DelphiObjectTypeRegistry {

  private static final Logger LOGGER = Loggers.getLogger();

  private final Map<Class, DelphiScriptClass<?>> scriptClassMap = new Object2ObjectOpenHashMap<>();

  public <T> void register(Class<T> type, Class<?> methodHolder) {
    DelphiScriptClass<T> sc = (DelphiScriptClass<T>) scriptClassMap.get(type);
    boolean addToMap = false;
    if (sc == null) {
      addToMap = true;
      sc = new DelphiScriptClass<>(type);
    }

    boolean success;
    try {
      success = scanClass(sc, methodHolder);
    } catch (IllegalAccessException e) {
      // Shouldn't happen
      throw new RuntimeException(e);
    }

    if (!success) {
      return;
    }

    if (addToMap) {
      scriptClassMap.put(type, sc);
    }
  }

  public <T> DelphiScriptObject<T> wrapObject(T object) {
    Class<?> type = object.getClass();
    DelphiScriptClass<T> sc = (DelphiScriptClass<T>) scriptClassMap.get(type);

    if (sc != null) {
      return new DelphiScriptObject<>(object, sc);
    }

    Class<?>[] interfaces = type.getInterfaces();
    for (Class<?> anInterface : interfaces) {
      sc = (DelphiScriptClass<T>) scriptClassMap.get(anInterface);
      if (sc == null) {
        continue;
      }

      return new DelphiScriptObject<>(object, sc);
    }

    return null;
  }

  public <T> DelphiScriptObject<T> wrapObject(Class<T> interfaceType, T object) {
    DelphiScriptClass<T> sc = (DelphiScriptClass<T>) scriptClassMap.get(interfaceType);
    if (sc == null) {
      return null;
    }
    return new DelphiScriptObject<>(object, sc);
  }

  public <T> boolean scanClass(DelphiScriptClass<T> scriptClass, Class<?> jsMethodHolder)
      throws IllegalAccessException
  {
    Method[] methods = jsMethodHolder.getMethods();
    Lookup lookup = MethodHandles.publicLookup();

    Class<T> jType = scriptClass.getTypeClass();
    String typename = jType.getSimpleName();

    Set<String> properties = new ObjectOpenHashSet<>();
    Map<String, MethodHandle> getters = new Object2ObjectOpenHashMap<>();
    Map<String, DelphiClassMethod> setters = new Object2ObjectOpenHashMap<>();
    Map<String, DelphiClassMethod> scriptMethods = new Object2ObjectOpenHashMap<>();

    for (Method method : methods) {
      SetProperty pset = method.getAnnotation(SetProperty.class);
      GetProperty pget = method.getAnnotation(GetProperty.class);
      ScriptFunction sfunc = method.getAnnotation(ScriptFunction.class);

      if (pget == null && pset == null && sfunc == null) {
        continue;
      }

      if (!Modifier.isStatic(method.getModifiers())) {
        LOGGER.error("Script method {} is not static", method);
        continue;
      }

      Parameter[] params = method.getParameters();
      if (params.length < 1 || !areCompatible(params[0].getType(), jType)) {
        LOGGER.error("Script method {} does not have a {} parameter", method, typename);
        continue;
      }

      MethodHandle handle = lookup.unreflect(method);

      if (pget != null) {
        if (params.length != 1) {
          LOGGER.error("GetProperty method {} must have 1 argument: {}", method, typename);
          continue;
        }

        String value = pget.value();
        if (Strings.isNullOrEmpty(value)) {
          value = stripPrefix(method.getName(), "get");
        }

        if (getters.containsKey(value)) {
          LOGGER.error("GetProperty with name {} already defined", value);
          continue;
        }

        getters.put(value, handle);
        properties.add(value);

        continue;
      }

      if (pset != null) {
        if (params.length != 2) {
          LOGGER.error("SetProperty method {} must have 2 arguments: {} and a value", method, typename);
          continue;
        }

        String value = pset.value();
        if (Strings.isNullOrEmpty(value)) {
          value = stripPrefix(method.getName(), "set");
        }

        if (setters.containsKey(value)) {
          LOGGER.error("SetProperty with name {} already defined", value);
          continue;
        }

        Parameter[] onlyParam = new Parameter[1];
        onlyParam[0] = params[1];

        DelphiClassMethod dMethod = new DelphiClassMethod(handle, onlyParam, 1, 1);
        setters.put(value, dMethod);
        properties.add(value);

        continue;
      }

      Parameter[] funcParams = new Parameter[params.length - 1];
      int minArity = 0;
      int maxArity = 0;

      for (int i = 1; i < params.length; i++) {
        Parameter param = params[i];
        if (param.isVarArgs()) {
          maxArity = Integer.MAX_VALUE;
        } else {
          minArity++;
          maxArity++;
        }
        funcParams[i - 1] = param;
      }

      String fname = sfunc.value();
      if (Strings.isNullOrEmpty(fname)) {
        fname = method.getName();
      }

      if (scriptMethods.containsKey(fname)) {
        LOGGER.error("Script method {} already defined", fname);
        continue;
      }

      DelphiClassMethod dMethod = new DelphiClassMethod(handle, funcParams, minArity, maxArity);
      scriptMethods.put(fname, dMethod);
      properties.add(fname);
    }

    if (properties.isEmpty()) {
      return false;
    }

    for (String string : setters.keySet()) {
      MethodHandle getter = getters.get(string);
      if (getter != null) {
        continue;
      }

      LOGGER.warn("Script property '{}' has setter, but not getter", string);
    }

    for (String property : properties) {
      if (!scriptClass.properties.contains(property)) {
        continue;
      }

      LOGGER.error("Property/method {} for class {} is already defined", property, typename);
      return false;
    }

    scriptClass.propertySetters.putAll(setters);
    scriptClass.propertyGetters.putAll(getters);
    scriptClass.methods.putAll(scriptMethods);
    scriptClass.properties.addAll(properties);

    return true;
  }

  private String stripPrefix(String mname, String prefix) {
    if (mname.startsWith(prefix)) {
      LOGGER.info("mname={} prefix={}", mname, prefix);
      String sub = mname.substring(prefix.length());
      String result = sub.substring(0, 1).toLowerCase() + sub.substring(1);

      LOGGER.debug("sub={} result={}", sub, result);

      return result;
    }

    return mname;
  }

  private static boolean areCompatible(Class<?> t1, Class<?> t2) {
    return t1.isAssignableFrom(t2);
  }
}
