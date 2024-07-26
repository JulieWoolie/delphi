package net.arcadiusmc.delphi.dom.scss.func;

public interface StyleFunctions {

  ScssFunction LIGHTEN = new BrightnessFunction(false);
  ScssFunction DARKEN = new BrightnessFunction(true);

  ScssFunction RGB = new RgbFunction(false);
  ScssFunction RGBA = new RgbFunction(true);
}
