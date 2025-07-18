package net.arcadiusmc.hephaestus.interop.scan;

import static net.arcadiusmc.hephaestus.interop.scan.IndexReadHandler.isValidIndexParam;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import net.arcadiusmc.hephaestus.interop.DelphiClassMethod;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.IndexWrite;

public class IndexWriteHandler extends AnnotationHandler<IndexWrite> {

  public IndexWriteHandler() {
    super(IndexWrite.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, IndexWrite annot, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    Parameter[] params = method.getParameters();

    if (params.length != 3 || !isValidIndexParam(params[1])) {
      throw error("IndexWrite method must have 3 params: %s, index and a value", sc.getTypeName());
    }
    if (sc.getArrayWrite() != null) {
      throw error("ArrayWrite function already defined for %s", sc.getTypeName());
    }

    Parameter[] funcParams = new Parameter[2];
    funcParams[0] = params[1];
    funcParams[1] = params[2];

    DelphiClassMethod m = new DelphiClassMethod(handle, funcParams, 2, 2);
    sc.setArrayWrite(m);
  }


}
