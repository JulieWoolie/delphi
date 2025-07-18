package net.arcadiusmc.hephaestus.interop.scan;

import com.google.common.base.Strings;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import net.arcadiusmc.hephaestus.interop.DelphiClassMethod;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.ScriptFunction;

public class ScriptMethodHandler extends AnnotationHandler<ScriptFunction> {

  public ScriptMethodHandler() {
    super(ScriptFunction.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, ScriptFunction a, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    Parameter[] params = method.getParameters();
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

    String fname = a.value();
    if (Strings.isNullOrEmpty(fname)) {
      fname = method.getName();
    }

    if (sc.getMethods().containsKey(fname)) {
      throw AnnotationScanException.of("Script method '%s' already defined", fname);
    }

    DelphiClassMethod dMethod = new DelphiClassMethod(handle, funcParams, minArity, maxArity);
    sc.getMethods().put(fname, dMethod);
    sc.getProperties().add(fname);
  }
}
