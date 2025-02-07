package net.arcadiusmc.chimera.function;

public interface ScssFunctions {
  // Color functions
  ScssFunction RGB = new RgbFunction();
  ScssFunction HSL = new HslFunction();
  ScssFunction BRIGHTEN = new BrightnessFunction(false);
  ScssFunction DARKEN = new BrightnessFunction(true);

  // f(x) functions
  ScssFunction SQRT = new MathFunction(Math::sqrt);
  ScssFunction SIN = new MathAngleUnaryFunction(Math::sin);
  ScssFunction COS = new MathAngleUnaryFunction(Math::cos);
  ScssFunction TAN = new MathAngleUnaryFunction(Math::tan);
  ScssFunction SIGN = new MathFunction(Math::signum);
  ScssFunction EXP = new MathFunction(Math::exp);
  ScssFunction ATAN = new MathFunction(Math::atan);
  ScssFunction ASIN = new MathFunction(Math::asin);
  ScssFunction ACOS = new MathFunction(Math::acos);
  ScssFunction ABS = new MathFunction(Math::abs);

  // f(x,y) functions
  ScssFunction MAX = new MathBiFunction(Math::max);
  ScssFunction MIN = new MathBiFunction(Math::min);
  ScssFunction ATAN2 = new MathBiFunction(Math::atan2);

  ScssFunction CLAMP = new ClampFunction();

  ScssFunction GET_PROPERTY = new GetPropertyFunction();
  ScssFunction SET_PROPERTY = new SetPropertyFunction();

  ScssFunction IF = new IfFunction();
}
