package net.arcadiusmc.chimera.function;

public interface ScssFunctions {
  ScssFunction RGB = new RgbFunction(false);
  ScssFunction RGBA = new RgbFunction(true);
  ScssFunction BRIGHTEN = new BrightnessFunction(false);
  ScssFunction DARKEN = new BrightnessFunction(true);
}
