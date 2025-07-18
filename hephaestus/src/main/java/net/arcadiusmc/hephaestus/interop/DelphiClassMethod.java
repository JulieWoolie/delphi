package net.arcadiusmc.hephaestus.interop;

import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Value;

public class DelphiClassMethod {

  private final MethodHandle handle;
  private final Parameter[] paramTypes;

  private final int minArity;
  private final int maxArity;

  public DelphiClassMethod(
      MethodHandle handle,
      Parameter[] paramTypes,
      int minArity,
      int maxArity
  ) {
    this.handle = handle;
    this.paramTypes = paramTypes;
    this.minArity = minArity;
    this.maxArity = maxArity;
  }

  public Object execute(Object receiver, Object... arguments)
      throws ArityException, UnsupportedTypeException
  {
    int len = arguments.length;
    if (len < minArity || len > maxArity) {
      throw ArityException.create(minArity, maxArity, arguments.length);
    }

    int offset = (receiver == null ? 0 : 1);
    Object[] javaArgs = new Object[len + offset];
    if (receiver != null) {
      javaArgs[0] = receiver;
    }

    for (int i = 0; i < len; i++) {
      int jArgIdx = i + offset;
      Object arg = arguments[i];
      Parameter param = paramTypes[Math.min(i, paramTypes.length - 1)];
      Class<?> conversionType;

      if (param.isVarArgs()) {
        conversionType = param.getType().getComponentType();
      } else {
        conversionType = param.getType();
      }

      try {
        javaArgs[jArgIdx] = convertToParam(arg, conversionType);
      } catch (ClassCastException exc) {
        throw UnsupportedTypeException.create(
            arguments,
            "Expected argument " + (i - offset) + " to be of type " + param.getType().getSimpleName()
        );
      }
    }

    return callSafe(handle, javaArgs);
  }

  private Object convertToParam(Object scriptArg, Class<?> desiredType) {
    if (desiredType.isInstance(scriptArg)) {
      return scriptArg;
    }
    return Value.asValue(scriptArg).as(desiredType);
  }

  public static Object callSafe(MethodHandle handle, Object... args) {
    try {
      Object o = handle.invokeWithArguments(args);
      return Scripting.wrapReturn(o);
    } catch (InvocationTargetException exc) {
      throw new RuntimeException(exc.getCause());
    } catch (RuntimeException r) {
      throw r;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
}
