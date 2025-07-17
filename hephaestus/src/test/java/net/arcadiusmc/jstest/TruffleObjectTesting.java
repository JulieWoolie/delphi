package net.arcadiusmc.jstest;

import static net.arcadiusmc.hephaestus.Scripting.JS_LANGUAGE;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

public class TruffleObjectTesting {

  @ExportLibrary(InteropLibrary.class)
  class TruffleObjectImpl implements TruffleObject {

    @ExportMessage
    boolean isExecutable() {
      return true;
    }

    @ExportMessage
    Object execute(Object... args) {
      return 1;
    }

    @ExportMessage
    boolean hasMembers() {
      return true;
    }

    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
      return new KeysObject();
    }

    @ExportMessage
    boolean isMemberReadable(String member) {
      return "hello".equals(member);
    }

    @ExportMessage
    Object readMember(String member) throws UnknownIdentifierException {
      if ("hello".equals(member)) {
        return "goodbye";
      }
      throw UnknownIdentifierException.create(member);
    }

    @ExportLibrary(InteropLibrary.class)
    static final class KeysObject implements TruffleObject {
      @ExportMessage
      boolean hasArrayElements() {
        return true;
      }

      @ExportMessage
      long getArraySize() {
        return 1;
      }

      @ExportMessage
      boolean isArrayElementReadable(long index) {
        return index == 0;
      }

      @ExportMessage
      Object readArrayElement(long index) throws InvalidArrayIndexException {
        if (index == 0) {
          return "hello";
        }
        throw InvalidArrayIndexException.create(index);
      }
    }
  }

  @Test
  void runTest() {
    Context ctx = Scripting.setupContext();
    Value bindings = ctx.getBindings(JS_LANGUAGE);
    bindings.putMember("testobj", new TruffleObjectImpl());

    Value v = ctx.eval(JS_LANGUAGE, "testobj.hello");
    Value v1 = ctx.eval(JS_LANGUAGE, "testobj()");

    System.out.println(v.toString());
    System.out.println(v1.toString());
  }
}
