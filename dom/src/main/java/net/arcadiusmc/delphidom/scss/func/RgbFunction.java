package net.arcadiusmc.delphidom.scss.func;

import net.arcadiusmc.delphidom.scss.func.ArgsParser.Argument;
import net.arcadiusmc.delphidom.parser.ParserErrors;
import net.arcadiusmc.delphidom.parser.Token;
import net.arcadiusmc.dom.style.Color;

public class RgbFunction implements ScssFunction {

  static final int MAX_VALUE = 255;

  private final boolean alphaChannelIncluded;

  public RgbFunction(boolean alphaChannelIncluded) {
    this.alphaChannelIncluded = alphaChannelIncluded;
  }

  @Override
  public Object evaluate(String functionName, ArgsParser parser, ParserErrors errors) {
    Argument<Float> redArg = maybePercent(parser);
    Argument<Float> greenArg = maybePercent(parser);
    Argument<Float> blueArg = maybePercent(parser);
    Argument<Float> alphaArg;

    if (alphaChannelIncluded) {
      alphaArg = parser.number();
    } else {
      alphaArg = null;
    }

    parser.end();

    int r = redArg.value().intValue();
    int g = greenArg.value().intValue();
    int b = blueArg.value().intValue();

    int a = alphaArg == null
        ? MAX_VALUE
        : (int) (MAX_VALUE * alphaArg.value());

    return Color.argb(a, r, g, b);
  }

  private Argument<Float> maybePercent(ArgsParser parser) {
    Argument<Float> arg = parser.number();
    float value = arg.value();

    if (parser.nextMatches(Token.PERCENT)) {
      parser.next();

      if (value < 0.0f) {
        value = 0.0f;
      } else if (value > 100.0f) {
        value = 100.0f;
      }

      float f = value * Color.MAX_VALUE;

      return new Argument<>(f, arg.location());
    }

    if (value < 0 || value > Color.MAX_VALUE) {
      parser.warn(arg.location(),
          "Color channel value out of bounds [%s..%s]: %s ... clamping",
          Color.MIN_VALUE,
          Color.MAX_VALUE,
          value
      );

      return new Argument<>(Math.clamp(value, Color.MIN_VALUE, Color.MAX_VALUE), arg.location());
    }

    return arg;
  }
}
