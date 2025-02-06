package net.arcadiusmc.delphidom.parser;

public interface ElementInputConsumer<T> {

  void consume(String input, T element);
}
