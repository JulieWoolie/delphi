package net.arcadiusmc.hephaestus.interop;

import com.oracle.truffle.api.impl.Accessor;
import java.lang.reflect.Field;
import org.graalvm.polyglot.impl.AbstractPolyglotImpl.APIAccess;

public final class Interop {

  private static Class engineAccessor;
  private static Class APIAccessImpl;
  private static Field engineAccessor_ACCESSOR;
  private static Field apiAccessImpl_INSTANCE;

  private static Accessor cachedAccessor;
  private static APIAccess cachedApiAccess;

  public static Accessor getHostAccessor() {
    if (cachedAccessor != null) {
      return cachedAccessor;
    }

    if (engineAccessor_ACCESSOR == null) {
      try {
        findEngineAccessor();
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    try {
      return cachedAccessor = (Accessor) engineAccessor_ACCESSOR.get(null);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public static APIAccess getApiAccess() {
    if (cachedApiAccess != null) {
      return cachedApiAccess;
    }

    if (apiAccessImpl_INSTANCE == null) {
      try {
        findApiAccess();
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    try {
      APIAccess access = (APIAccess) apiAccessImpl_INSTANCE.get(null);
      return cachedApiAccess = access;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static void findEngineAccessor() throws Throwable {
    if (engineAccessor == null) {
      engineAccessor = findClass("com.oracle.truffle.polyglot.EngineAccessor");
    }

    engineAccessor_ACCESSOR = engineAccessor.getDeclaredField("ACCESSOR");
    engineAccessor_ACCESSOR.setAccessible(true);
  }

  private static void findApiAccess() throws Throwable {
    if (APIAccessImpl == null) {
      APIAccessImpl = findClass("org.graalvm.polyglot.Engine$APIAccessImpl");
    }

    apiAccessImpl_INSTANCE = APIAccessImpl.getDeclaredField("INSTANCE");
    apiAccessImpl_INSTANCE.setAccessible(true);
  }

  private static Class<?> findClass(String name) throws ClassNotFoundException {
    return Class.forName(name, true, Interop.class.getClassLoader());
  }
}
