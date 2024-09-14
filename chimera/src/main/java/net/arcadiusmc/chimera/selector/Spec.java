package net.arcadiusmc.chimera.selector;

import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

public class Spec implements Comparable<Spec> {

  static final Comparator<Spec> COMPARATOR = Comparator.<Spec>comparingInt(value -> value.idColumn)
      .thenComparingInt(value -> value.classColumn)
      .thenComparingInt(value -> value.typeColumn);

  public int idColumn = 0;
  public int classColumn = 0;
  public int typeColumn = 0;

  @Override
  public String toString() {
    return "(" + idColumn + ", " + classColumn + ", " + typeColumn + ")";
  }

  @Override
  public int compareTo(@NotNull Spec o) {
    return COMPARATOR.compare(this, o);
  }

  public void add(Spec specificity) {
    this.idColumn += specificity.idColumn;
    this.classColumn += specificity.classColumn;
    this.typeColumn += specificity.typeColumn;
  }

  public void set(int i) {
    this.idColumn = i;
    this.classColumn = i;
    this.typeColumn = i;
  }
}
