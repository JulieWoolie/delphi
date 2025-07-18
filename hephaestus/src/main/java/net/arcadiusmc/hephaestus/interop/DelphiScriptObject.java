package net.arcadiusmc.hephaestus.interop;

import static net.arcadiusmc.hephaestus.interop.DelphiClassMethod.callSafe;

import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
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
    if (isIndexMember(member)) {
      long idx = toIndex(member);
      return isArrayElementReadable(idx);
    }
    return type.getters.containsKey(member);
  }

  @ExportMessage
  public Object readMember(String name)
      throws UnknownIdentifierException, UnsupportedMessageException
  {
    if (isIndexMember(name)) {
      long idx = toIndex(name);
      System.out.println("indexRead: " + name + " hasArrayElements: " + hasArrayElements() + " readable: " + isArrayElementReadable(idx));
      try {
        return readArrayElement(idx);
      } catch (InvalidArrayIndexException e) {
        throw UnknownIdentifierException.create(name);
      }
    }

    MethodHandle methodHandle = type.getters.get(name);
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
    if (isIndexMember(name)) {
      return isArrayElementModifiable(toIndex(name));
    }

    return type.setters.containsKey(name);
  }

  @ExportMessage
  public void writeMember(String name, Object value)
      throws UnknownIdentifierException, UnsupportedTypeException, UnsupportedMessageException
  {
    if (isIndexMember(name)) {
      long idx = toIndex(name);

      try {
        writeArrayElement(idx, value);
      } catch (InvalidArrayIndexException e) {
        throw UnknownIdentifierException.create(name);
      }

      return;
    }

    DelphiClassMethod handle = type.setters.get(name);
    if (handle == null) {
      throw UnknownIdentifierException.create(name);
    }

    try {
      handle.execute(object, value);
    } catch (ArityException ignored) {
      // should not happen
      throw new IllegalStateException();
    }
  }

  private long toIndex(String member) {
    return Long.parseLong(member);
  }

  private boolean isIndexMember(String name) {
    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (i == 0 && ch == '-') {
        continue;
      }
      if (ch >= '0' && ch <= '9') {
        continue;
      }
      return false;
    }
    return true;
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

  @ExportMessage
  public boolean hasArrayElements() {
    return type.arrayLen != null && type.arrayRead != null;
  }

  @ExportMessage
  public Object readArrayElement(long idx)
      throws UnsupportedMessageException, InvalidArrayIndexException
  {
    if (!hasArrayElements()) {
      throw UnsupportedMessageException.create();
    }

    Object index;
    if (type.arrayReadInt) {
      index = (int) idx;
    } else {
      index = idx;
    }

    try {
      return callSafe(type.arrayRead, object, index);
    } catch (IndexOutOfBoundsException exc) {
      throw InvalidArrayIndexException.create(idx);
    }
  }

  @ExportMessage
  public long getArraySize() throws UnsupportedMessageException {
    if (!hasArrayElements()) {
      throw UnsupportedMessageException.create();
    }

    Object o = callSafe(type.arrayLen, object);
    if (o instanceof Integer i) {
      return i.longValue();
    }
    return (Long) o;
  }

  @ExportMessage
  public boolean isArrayElementReadable(long idx) {
    if (!hasArrayElements()) {
      return false;
    }

    try {
      long length = getArraySize();
      return idx >= 0 && idx < length;
    } catch (UnsupportedMessageException e) {
      throw new RuntimeException(e);
    }
  }

  @ExportMessage
  public void writeArrayElement(long idx, Object value)
      throws UnsupportedMessageException, UnsupportedTypeException, InvalidArrayIndexException {
    if (!hasArrayElements() || type.arrayWrite == null) {
      throw UnsupportedMessageException.create();
    }

    try {
      type.arrayWrite.execute(object, idx, value);
    } catch (ArityException ignored) {
      // Shouldn't be possible
    } catch (IndexOutOfBoundsException exc) {
      throw InvalidArrayIndexException.create(idx);
    }
  }

  @ExportMessage
  public boolean isArrayElementModifiable(long idx) {
    if (!isArrayElementReadable(idx)) {
      return false;
    }
    return type.arrayWrite != null;
  }

  @ExportMessage
  public boolean isArrayElementInsertable(long idx) {
    return false;
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
