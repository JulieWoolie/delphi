package net.arcadiusmc.hephaestus.interop;

import com.oracle.truffle.api.impl.Accessor;
import java.lang.reflect.Field;

public final class Interop {

  private static Class engineAccessor;
  private static Field engineAccessor_ACCESSOR;
  private static Accessor cached;

  public static Accessor getHostAccessor() {
    if (cached != null) {
      return cached;
    }

    if (engineAccessor_ACCESSOR == null) {
      try {
        reflect();
      } catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }

    try {
      return cached = (Accessor) engineAccessor_ACCESSOR.get(null);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  private static void reflect() throws Throwable {
    if (engineAccessor == null) {
      engineAccessor = Class.forName("com.oracle.truffle.polyglot.EngineAccessor");
    }

    engineAccessor_ACCESSOR = engineAccessor.getDeclaredField("ACCESSOR");
    engineAccessor_ACCESSOR.setAccessible(true);
  }
}
