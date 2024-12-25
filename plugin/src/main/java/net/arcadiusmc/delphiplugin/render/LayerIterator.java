package net.arcadiusmc.delphiplugin.render;

import static net.arcadiusmc.delphiplugin.render.RenderLayer.LAYER_COUNT;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.Getter;

public class LayerIterator implements Iterator<Layer> {

  private final Layer[] layers;

  private int dir;
  private int index;

  @Getter
  private int count;

  public LayerIterator(Layer[] layers, int dir, int index) {
    this.layers = layers;
    this.dir = dir;
    this.index = index;
  }

  private boolean inBounds(int idx) {
    return idx >= 0 && idx < LAYER_COUNT;
  }

  @Override
  public boolean hasNext() {
    if (!inBounds(index)) {
      return false;
    }

    while (inBounds(index)) {
      Layer layer = layers[index];

      if (RenderObject.isNotSpawned(layer)) {
        index += dir;
        continue;
      }

      return true;
    }

    return false;
  }

  @Override
  public Layer next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    Layer l = layers[index];
    index += dir;
    count++;

    return l;
  }
}
