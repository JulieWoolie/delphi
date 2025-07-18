package net.arcadiusmc.hephaestus.interop.scan;

import com.google.common.base.Strings;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import net.arcadiusmc.hephaestus.interop.DelphiClassMethod;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.SetProperty;

public class SetPropertyHandler extends AnnotationHandler<SetProperty> {

  public SetPropertyHandler() {
    super(SetProperty.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, SetProperty annot, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    if (method.getParameterCount() != 2) {
      throw AnnotationScanException.of(
          "SetProperty method '{}' must have 2 arguments: {} and a value",
          method, sc.getTypeName()
      );
    }

    String value = annot.value();
    if (Strings.isNullOrEmpty(value)) {
      value = stripPrefix(method.getName(), "set");
    }

    if (sc.getSetters().containsKey(value)) {
      throw AnnotationScanException.of("SetProperty with name '%s' already defined", value);
    }

    Parameter[] params = method.getParameters();
    Parameter[] onlyParam = new Parameter[1];
    onlyParam[0] = params[1];

    DelphiClassMethod dMethod = new DelphiClassMethod(handle, onlyParam, 1, 1);
    sc.getSetters().put(value, dMethod);
    sc.getProperties().add(value);
  }
}
