package net.arcadiusmc.hephaestus.interop.scan;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import net.arcadiusmc.hephaestus.interop.DelphiScriptClass;
import net.arcadiusmc.hephaestus.interop.IndexRead;

public class IndexReadHandler extends AnnotationHandler<IndexRead> {

  public IndexReadHandler() {
    super(IndexRead.class);
  }

  @Override
  public void scan(Method method, MethodHandle handle, IndexRead annot, DelphiScriptClass<?> sc)
      throws AnnotationScanException
  {
    Parameter[] params = method.getParameters();

    if (params.length != 2 || !isValidIndexParam(params[1])) {
      throw error("IndexRead method must have 2 properties: %s and an int or long index",
          sc.getTypeName()
      );
    }
    if (sc.getArrayRead() != null) {
      throw error("Array read function already defined on %s", sc.getTypeName());
    }

    Class<?> ptype = params[1].getType();
    if (ptype == Integer.class || ptype == Integer.TYPE) {
      sc.setArrayReadInt(true);
    }

    sc.setArrayRead(handle);
  }

  static boolean isValidIndexParam(Parameter param) {
    return isValidIndexType(param.getType());
  }

  static boolean isValidIndexType(Class<?> type) {
    return type == Integer.TYPE || type == Integer.class
        || type == Long.TYPE || type == Long.class;
  }
}
