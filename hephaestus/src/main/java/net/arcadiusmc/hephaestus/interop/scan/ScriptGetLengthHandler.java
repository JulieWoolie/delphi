package net.arcadiusmc.hephaestus.interop.scan;

import static net.arcadiusmc.hephaestus.interop.scan.IndexReadHandler.isValidIndexType;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.ScriptGetLength;

public class ScriptGetLengthHandler extends AnnotationHandler<ScriptGetLength> {

  public ScriptGetLengthHandler() {
    super(ScriptGetLength.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, ScriptGetLength l, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    if (method.getParameterCount() != 1) {
      throw error("ScriptGetLength methods can only have 1 argument: %s", sc.getTypeName());
    }
    if (!isValidIndexType(method.getReturnType())) {
      throw error("ScriptGetLength method %s must return either a long or int", method);
    }
    if (sc.getArrayLen() != null) {
      throw error("ScriptGetLength already defined for type %s", sc.getTypeName());
    }

    sc.setArrayLen(handle);
  }
}
