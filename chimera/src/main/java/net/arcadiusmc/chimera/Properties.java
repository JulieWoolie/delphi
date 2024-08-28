package net.arcadiusmc.chimera;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Map;

public final class Properties {
  private Properties() {}

  private static final Map<String, Property> nameLookup = new Object2ObjectOpenHashMap<>();
  private static Property[] idLookup = new Property[50];
  private static int nextId = 0;

  static {
    registerAll();
  }

  private static void registerAll() {

  }

  private static <T> void register(String key, Property<T> property) {
    if (nameLookup.containsKey(key)) {
      throw new IllegalArgumentException("Key already registered: " + key);
    }

    nameLookup.put(key, property);
    property.key = key;

    int id = nextId++;
    if (id >= idLookup.length) {
      idLookup = ObjectArrays.forceCapacity(idLookup, idLookup.length + 10, idLookup.length);
    }

    idLookup[id] = property;
    property.id = id;
  }
}
