package net.arcadiusmc.hephaestus.interop;

import static net.arcadiusmc.hephaestus.interop.DelphiClassMethod.callSafe;

import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.util.Map;

@ExportLibrary(InteropLibrary.class)
public class DelphiScriptObject<T> implements TruffleObject {

  private final T object;
  private final DelphiScriptClass<T> type;

  private Map<String, ScriptMethod> methodMap;

  public DelphiScriptObject(T object, DelphiScriptClass<T> type) {
    this.object = object;
    this.type = type;
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final boolean isMemberReadable(String member) {
    return type.propertyGetters.containsKey(member);
  }

  @ExportMessage
  public Object readMember(String name) throws UnknownIdentifierException {
    MethodHandle methodHandle = type.propertyGetters.get(name);
    if (methodHandle != null) {
      return callSafe(methodHandle, object);
    }

    if (methodMap != null) {
      ScriptMethod scriptMethod = methodMap.get(name);
      if (scriptMethod != null) {
        return scriptMethod;
      }
    }

    DelphiClassMethod method = type.methods.get(name);
    if (method != null) {
      ScriptMethod scriptMethod = new ScriptMethod(object, method);
      if (methodMap == null) {
        methodMap = new Object2ObjectOpenHashMap<>();
      }
      methodMap.put(name, scriptMethod);
      return scriptMethod;
    }

    throw UnknownIdentifierException.create(name);
  }

  @ExportMessage
  public boolean isMemberModifiable(String name) {
    return type.propertySetters.containsKey(name);
  }

  @ExportMessage
  public Object writeMember(String name, Object value)
      throws UnknownIdentifierException, UnsupportedTypeException
  {
    DelphiClassMethod handle = type.propertySetters.get(name);
    if (handle == null) {
      throw UnknownIdentifierException.create(name);
    }

    try {
      return handle.execute(object, value);
    } catch (ArityException ignored) {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @ExportMessage
  public boolean isMemberInsertable(String name) {
    return false;
  }

  @ExportMessage
  public boolean isMemberInvocable(String name) {
    return type.methods.containsKey(name);
  }

  @ExportMessage
  public Object invokeMember(String member, Object... arguments)
      throws UnknownIdentifierException, UnsupportedTypeException, ArityException
  {
    DelphiClassMethod method = type.methods.get(member);
    if (method == null) {
      throw UnknownIdentifierException.create(member);
    }
    return method.execute(object, arguments);
  }

  @ExportMessage
  final Object getMembers(boolean includeInternal) {
    return new KeysObject<>(type);
  }

  @ExportLibrary(InteropLibrary.class)
  static final class ScriptMethod implements TruffleObject {

    private final Object o;
    private final DelphiClassMethod method;

    public ScriptMethod(Object o, DelphiClassMethod method) {
      this.o = o;
      this.method = method;
    }

    @ExportMessage
    boolean isExecutable() {
      return true;
    }

    @ExportMessage
    Object execute(Object... args) throws ArityException, UnsupportedTypeException {
      return method.execute(o, args);
    }
  }

  @ExportLibrary(InteropLibrary.class)
  static final class KeysObject<T> implements TruffleObject {

    private final DelphiScriptClass<T> type;

    public KeysObject(DelphiScriptClass<T> type) {
      this.type = type;
    }

    @ExportMessage
    boolean hasArrayElements() {
      return true;
    }

    @ExportMessage
    long getArraySize() {
      return type.properties.size();
    }

    @ExportMessage
    boolean isArrayElementReadable(long index) {
      return index < getArraySize();
    }

    @ExportMessage
    Object readArrayElement(long index) throws InvalidArrayIndexException {
      if (!isArrayElementReadable(index)) {
        throw InvalidArrayIndexException.create(index);
      }

      int idx = (int) index;
      return type.properties.get(idx);
    }
  }
}
