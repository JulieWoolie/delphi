package net.arcadiusmc.hephaestus.interop;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DelphiScriptClass<T> {

  private final Class<T> typeClass;

  final Map<String, MethodHandle> getters = new Object2ObjectOpenHashMap<>();
  final Map<String, DelphiClassMethod> setters = new Object2ObjectOpenHashMap<>();
  final Map<String, DelphiClassMethod> methods = new Object2ObjectOpenHashMap<>();
  final List<String> properties = new ObjectArrayList<>();

  MethodHandle arrayRead;
  boolean arrayReadInt;

  MethodHandle arrayLen;
  DelphiClassMethod arrayWrite;

  public DelphiScriptClass(Class<T> typeClass) {
    this.typeClass = typeClass;
  }

  public String getTypeName() {
    return typeClass.getSimpleName();
  }
}
