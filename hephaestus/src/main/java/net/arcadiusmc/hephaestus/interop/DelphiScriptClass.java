package net.arcadiusmc.hephaestus.interop;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class DelphiScriptClass<T> {

  private final Class<T> typeClass;

  final Map<String, MethodHandle> propertyGetters = new Object2ObjectOpenHashMap<>();
  final Map<String, DelphiClassMethod> propertySetters = new Object2ObjectOpenHashMap<>();
  final Map<String, DelphiClassMethod> methods = new Object2ObjectOpenHashMap<>();
  final List<String> properties = new ObjectArrayList<>();

  public DelphiScriptClass(Class<T> typeClass) {
    this.typeClass = typeClass;
  }
}
