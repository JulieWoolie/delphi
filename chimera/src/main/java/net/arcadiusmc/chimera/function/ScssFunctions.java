package net.arcadiusmc.chimera.function;

public interface ScssFunctions {
  ScssFunction RGB = new RgbFunction(false);
  ScssFunction RGBA = new RgbFunction(true);
}
