package net.arcadiusmc.chimera.selector;

public record AnB(int a, int b) {

  public static final AnB EVEN = new AnB(2, 0);
  public static final AnB ODD = new AnB(2, 1);

  public boolean indexMatches(int idx) {
    if (a == 0) {
      return idx == b;
    }

    if (a > 0) {
      if (idx < b) {
        return false;
      }

      return (idx - b) % a == 0;
    }

    if (idx > b) {
      return false;
    }

    return (b - idx) % (-a) == 0;
  }

  public void append(StringBuilder builder) {
    if (a == -1) {
      builder.append("-n+");
    } else if (a != 0) {
      if (a != 1) {
        builder.append(a);
      }

      builder.append('n');
      builder.append('+');
    }

    builder.append(b);
  }
}
