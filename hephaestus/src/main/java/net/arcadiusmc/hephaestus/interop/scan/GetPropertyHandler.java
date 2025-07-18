package net.arcadiusmc.hephaestus.interop.scan;

import com.google.common.base.Strings;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.GetProperty;

public class GetPropertyHandler extends AnnotationHandler<GetProperty> {

  public GetPropertyHandler() {
    super(GetProperty.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, GetProperty annot, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    if (method.getParameterCount() != 1) {
      throw AnnotationScanException.of("GetProperty method '%s' must have 1 argument: %s",
          method.getName(), sc.getTypeName()
      );
    }

    String value = annot.value();
    if (Strings.isNullOrEmpty(value)) {
      value = stripPrefix(method.getName(), "get");
    }

    if (sc.getGetters().containsKey(value)) {
      throw AnnotationScanException.of("GetProperty with name {} already defined", value);
    }

    sc.getGetters().put(value, handle);
    sc.getProperties().add(value);
  }
}
