package net.arcadiusmc.hephaestus.interop.scan;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import lombok.Getter;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;

public abstract class AnnotationHandler<T extends Annotation> {

  @Getter
  private final Class<T> annotationType;

  public AnnotationHandler(Class<T> annotationType) {
    this.annotationType = annotationType;
  }

  public abstract void scan(Method method, MethodHandle handle, T annot, DelphiScriptClass<?> sc)
      throws AnnotationScanException;

  protected AnnotationScanException error(String format, Object... args) {
    return AnnotationScanException.of(format, args);
  }

  public static String stripPrefix(String mname, String prefix) {
    if (mname.startsWith(prefix)) {
      String sub = mname.substring(prefix.length());
      String result = sub.substring(0, 1).toLowerCase() + sub.substring(1);
      return result;
    }

    return mname;
  }
}
