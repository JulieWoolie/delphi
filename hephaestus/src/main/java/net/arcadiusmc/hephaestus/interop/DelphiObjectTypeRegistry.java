package net.arcadiusmc.hephaestus.interop;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.hephaestus.interop.scan.AnnotationHandler;
import net.arcadiusmc.hephaestus.interop.scan.AnnotationHandlers;
import net.arcadiusmc.hephaestus.interop.scan.AnnotationScanException;
import org.slf4j.Logger;

public class DelphiObjectTypeRegistry {

  private static final Logger LOGGER = Loggers.getLogger();

  private final AnnotationHandlers handlers = new AnnotationHandlers();
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
    boolean success = true;

    for (Method method : methods) {
      if (!Modifier.isStatic(method.getModifiers())) {
        continue;
      }

      Parameter[] params = method.getParameters();
      if (params.length < 1 || !areCompatible(params[0].getType(), jType)) {
        continue;
      }

      MethodHandle handle = lookup.unreflect(method);
      Annotation[] annotations = method.getAnnotations();

      for (Annotation annotation : annotations) {
        AnnotationHandler<Annotation> handler = handlers.getHandler(annotation);
        if (handler == null) {
          LOGGER.info("Couldn't find handler for annotation {}", annotation);
          continue;
        }

        try {
          handler.scan(method, handle, annotation, scriptClass);
        } catch (AnnotationScanException exc) {
          LOGGER.error(exc.getMessage());
          success = false;
        }
      }
    }

    return success;
  }

  private static boolean areCompatible(Class<?> t1, Class<?> t2) {
    return t1.isAssignableFrom(t2);
  }
}
