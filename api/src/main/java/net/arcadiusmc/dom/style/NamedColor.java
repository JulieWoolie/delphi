package net.arcadiusmc.dom.style;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import org.jetbrains.annotations.Contract;

public final class NamedColor {
  private NamedColor() {}

  public static final Map<String, Color> NAMES;
  public static final Int2ObjectMap<String> VALUE_TO_NAME;

  /** AliceBlue. */
  public static final Color ALICE_BLUE = Color.rgb(0xf0f8ff);

  /** AntiqueWhite. */
  public static final Color ANTIQUE_WHITE = Color.rgb(0xfaebd7);

  /** Aqua. */
  public static final Color AQUA = Color.rgb(0x00ffff);

  /** Aquamarine. */
  public static final Color AQUAMARINE = Color.rgb(0x7fffd4);

  /** Azure. */
  public static final Color AZURE = Color.rgb(0xf0ffff);

  /** Beige. */
  public static final Color BEIGE = Color.rgb(0xf5f5dc);

  /** Bisque. */
  public static final Color BISQUE = Color.rgb(0xffe4c4);

  /** Black. */
  public static final Color BLACK = Color.rgb(0x000000);

  /** BlanchedAlmond. */
  public static final Color BLANCHED_ALMOND = Color.rgb(0xffebcd);

  /** Blue. */
  public static final Color BLUE = Color.rgb(0x0000ff);

  /** BlueViolet. */
  public static final Color BLUE_VIOLET = Color.rgb(0x8a2be2);

  /** Brown. */
  public static final Color BROWN = Color.rgb(0xa52a2a);

  /** BurlyWood. */
  public static final Color BURLY_WOOD = Color.rgb(0xdeb887);

  /** CadetBlue. */
  public static final Color CADET_BLUE = Color.rgb(0x5f9ea0);

  /** Chartreuse. */
  public static final Color CHARTREUSE = Color.rgb(0x7fff00);

  /** Chocolate. */
  public static final Color CHOCOLATE = Color.rgb(0xd2691e);

  /** Coral. */
  public static final Color CORAL = Color.rgb(0xff7f50);

  /** CornflowerBlue. */
  public static final Color CORNFLOWER_BLUE = Color.rgb(0x6495ed);

  /** Cornsilk. */
  public static final Color CORNSILK = Color.rgb(0xfff8dc);

  /** Crimson. */
  public static final Color CRIMSON = Color.rgb(0xdc143c);

  /** Cyan. */
  public static final Color CYAN = Color.rgb(0x00ffff);

  /** DarkBlue. */
  public static final Color DARK_BLUE = Color.rgb(0x00008b);

  /** DarkCyan. */
  public static final Color DARK_CYAN = Color.rgb(0x008b8b);

  /** DarkGoldenRod. */
  public static final Color DARK_GOLDEN_ROD = Color.rgb(0xb8860b);

  /** DarkGray. */
  public static final Color DARK_GRAY = Color.rgb(0xa9a9a9);

  /** DarkGrey. */
  public static final Color DARK_GREY = Color.rgb(0xa9a9a9);

  /** DarkGreen. */
  public static final Color DARK_GREEN = Color.rgb(0x006400);

  /** DarkKhaki. */
  public static final Color DARK_KHAKI = Color.rgb(0xbdb76b);

  /** DarkMagenta. */
  public static final Color DARK_MAGENTA = Color.rgb(0x8b008b);

  /** DarkOliveGreen. */
  public static final Color DARK_OLIVE_GREEN = Color.rgb(0x556b2f);

  /** DarkOrange. */
  public static final Color DARK_ORANGE = Color.rgb(0xff8c00);

  /** DarkOrchid. */
  public static final Color DARK_ORCHID = Color.rgb(0x9932cc);

  /** DarkRed. */
  public static final Color DARK_RED = Color.rgb(0x8b0000);

  /** DarkSalmon. */
  public static final Color DARK_SALMON = Color.rgb(0xe9967a);

  /** DarkSeaGreen. */
  public static final Color DARK_SEA_GREEN = Color.rgb(0x8fbc8f);

  /** DarkSlateBlue. */
  public static final Color DARK_SLATE_BLUE = Color.rgb(0x483d8b);

  /** DarkSlateGray. */
  public static final Color DARK_SLATE_GRAY = Color.rgb(0x2f4f4f);

  /** DarkSlateGrey. */
  public static final Color DARK_SLATE_GREY = Color.rgb(0x2f4f4f);

  /** DarkTurquoise. */
  public static final Color DARK_TURQUOISE = Color.rgb(0x00ced1);

  /** DarkViolet. */
  public static final Color DARK_VIOLET = Color.rgb(0x9400d3);

  /** DeepPink. */
  public static final Color DEEP_PINK = Color.rgb(0xff1493);

  /** DeepSkyBlue. */
  public static final Color DEEP_SKY_BLUE = Color.rgb(0x00bfff);

  /** DimGray. */
  public static final Color DIM_GRAY = Color.rgb(0x696969);

  /** DimGrey. */
  public static final Color DIM_GREY = Color.rgb(0x696969);

  /** DodgerBlue. */
  public static final Color DODGER_BLUE = Color.rgb(0x1e90ff);

  /** FireBrick. */
  public static final Color FIRE_BRICK = Color.rgb(0xb22222);

  /** FloralWhite. */
  public static final Color FLORAL_WHITE = Color.rgb(0xfffaf0);

  /** ForestGreen. */
  public static final Color FOREST_GREEN = Color.rgb(0x228b22);

  /** Fuchsia. */
  public static final Color FUCHSIA = Color.rgb(0xff00ff);

  /** Gainsboro. */
  public static final Color GAINSBORO = Color.rgb(0xdcdcdc);

  /** GhostWhite. */
  public static final Color GHOST_WHITE = Color.rgb(0xf8f8ff);

  /** Gold. */
  public static final Color GOLD = Color.rgb(0xffd700);

  /** GoldenRod. */
  public static final Color GOLDEN_ROD = Color.rgb(0xdaa520);

  /** Gray. */
  public static final Color GRAY = Color.rgb(0x808080);

  /** Grey. */
  public static final Color GREY = Color.rgb(0x808080);

  /** Green. */
  public static final Color GREEN = Color.rgb(0x008000);

  /** GreenYellow. */
  public static final Color GREEN_YELLOW = Color.rgb(0xadff2f);

  /** HoneyDew. */
  public static final Color HONEY_DEW = Color.rgb(0xf0fff0);

  /** HotPink. */
  public static final Color HOT_PINK = Color.rgb(0xff69b4);

  /** IndianRed. */
  public static final Color INDIAN_RED = Color.rgb(0xcd5c5c);

  /** Indigo. */
  public static final Color INDIGO = Color.rgb(0x4b0082);

  /** Ivory. */
  public static final Color IVORY = Color.rgb(0xfffff0);

  /** Khaki. */
  public static final Color KHAKI = Color.rgb(0xf0e68c);

  /** Lavender. */
  public static final Color LAVENDER = Color.rgb(0xe6e6fa);

  /** LavenderBlush. */
  public static final Color LAVENDER_BLUSH = Color.rgb(0xfff0f5);

  /** LawnGreen. */
  public static final Color LAWN_GREEN = Color.rgb(0x7cfc00);

  /** LemonChiffon. */
  public static final Color LEMON_CHIFFON = Color.rgb(0xfffacd);

  /** LightBlue. */
  public static final Color LIGHT_BLUE = Color.rgb(0xadd8e6);

  /** LightCoral. */
  public static final Color LIGHT_CORAL = Color.rgb(0xf08080);

  /** LightCyan. */
  public static final Color LIGHT_CYAN = Color.rgb(0xe0ffff);

  /** LightGoldenRodYellow. */
  public static final Color LIGHT_GOLDEN_ROD_YELLOW = Color.rgb(0xfafad2);

  /** LightGray. */
  public static final Color LIGHT_GRAY = Color.rgb(0xd3d3d3);

  /** LightGrey. */
  public static final Color LIGHT_GREY = Color.rgb(0xd3d3d3);

  /** LightGreen. */
  public static final Color LIGHT_GREEN = Color.rgb(0x90ee90);

  /** LightPink. */
  public static final Color LIGHT_PINK = Color.rgb(0xffb6c1);

  /** LightSalmon. */
  public static final Color LIGHT_SALMON = Color.rgb(0xffa07a);

  /** LightSeaGreen. */
  public static final Color LIGHT_SEA_GREEN = Color.rgb(0x20b2aa);

  /** LightSkyBlue. */
  public static final Color LIGHT_SKY_BLUE = Color.rgb(0x87cefa);

  /** LightSlateGray. */
  public static final Color LIGHT_SLATE_GRAY = Color.rgb(0x778899);

  /** LightSlateGrey. */
  public static final Color LIGHT_SLATE_GREY = Color.rgb(0x778899);

  /** LightSteelBlue. */
  public static final Color LIGHT_STEEL_BLUE = Color.rgb(0xb0c4de);

  /** LightYellow. */
  public static final Color LIGHT_YELLOW = Color.rgb(0xffffe0);

  /** Lime. */
  public static final Color LIME = Color.rgb(0x00ff00);

  /** LimeGreen. */
  public static final Color LIME_GREEN = Color.rgb(0x32cd32);

  /** Linen. */
  public static final Color LINEN = Color.rgb(0xfaf0e6);

  /** Magenta. */
  public static final Color MAGENTA = Color.rgb(0xff00ff);

  /** Maroon. */
  public static final Color MAROON = Color.rgb(0x800000);

  /** MediumAquaMarine. */
  public static final Color MEDIUM_AQUA_MARINE = Color.rgb(0x66cdaa);

  /** MediumBlue. */
  public static final Color MEDIUM_BLUE = Color.rgb(0x0000cd);

  /** MediumOrchid. */
  public static final Color MEDIUM_ORCHID = Color.rgb(0xba55d3);

  /** MediumPurple. */
  public static final Color MEDIUM_PURPLE = Color.rgb(0x9370db);

  /** MediumSeaGreen. */
  public static final Color MEDIUM_SEA_GREEN = Color.rgb(0x3cb371);

  /** MediumSlateBlue. */
  public static final Color MEDIUM_SLATE_BLUE = Color.rgb(0x7b68ee);

  /** MediumSpringGreen. */
  public static final Color MEDIUM_SPRING_GREEN = Color.rgb(0x00fa9a);

  /** MediumTurquoise. */
  public static final Color MEDIUM_TURQUOISE = Color.rgb(0x48d1cc);

  /** MediumVioletRed. */
  public static final Color MEDIUM_VIOLET_RED = Color.rgb(0xc71585);

  /** MidnightBlue. */
  public static final Color MIDNIGHT_BLUE = Color.rgb(0x191970);

  /** MintCream. */
  public static final Color MINT_CREAM = Color.rgb(0xf5fffa);

  /** MistyRose. */
  public static final Color MISTY_ROSE = Color.rgb(0xffe4e1);

  /** Moccasin. */
  public static final Color MOCCASIN = Color.rgb(0xffe4b5);

  /** NavajoWhite. */
  public static final Color NAVAJO_WHITE = Color.rgb(0xffdead);

  /** Navy. */
  public static final Color NAVY = Color.rgb(0x000080);

  /** OldLace. */
  public static final Color OLD_LACE = Color.rgb(0xfdf5e6);

  /** Olive. */
  public static final Color OLIVE = Color.rgb(0x808000);

  /** OliveDrab. */
  public static final Color OLIVE_DRAB = Color.rgb(0x6b8e23);

  /** Orange. */
  public static final Color ORANGE = Color.rgb(0xffa500);

  /** OrangeRed. */
  public static final Color ORANGE_RED = Color.rgb(0xff4500);

  /** Orchid. */
  public static final Color ORCHID = Color.rgb(0xda70d6);

  /** PaleGoldenRod. */
  public static final Color PALE_GOLDEN_ROD = Color.rgb(0xeee8aa);

  /** PaleGreen. */
  public static final Color PALE_GREEN = Color.rgb(0x98fb98);

  /** PaleTurquoise. */
  public static final Color PALE_TURQUOISE = Color.rgb(0xafeeee);

  /** PaleVioletRed. */
  public static final Color PALE_VIOLET_RED = Color.rgb(0xdb7093);

  /** PapayaWhip. */
  public static final Color PAPAYA_WHIP = Color.rgb(0xffefd5);

  /** PeachPuff. */
  public static final Color PEACH_PUFF = Color.rgb(0xffdab9);

  /** Peru. */
  public static final Color PERU = Color.rgb(0xcd853f);

  /** Pink. */
  public static final Color PINK = Color.rgb(0xffc0cb);

  /** Plum. */
  public static final Color PLUM = Color.rgb(0xdda0dd);

  /** PowderBlue. */
  public static final Color POWDER_BLUE = Color.rgb(0xb0e0e6);

  /** Purple. */
  public static final Color PURPLE = Color.rgb(0x800080);

  /** RebeccaPurple. */
  public static final Color REBECCA_PURPLE = Color.rgb(0x663399);

  /** Red. */
  public static final Color RED = Color.rgb(0xff0000);

  /** RosyBrown. */
  public static final Color ROSY_BROWN = Color.rgb(0xbc8f8f);

  /** RoyalBlue. */
  public static final Color ROYAL_BLUE = Color.rgb(0x4169e1);

  /** SaddleBrown. */
  public static final Color SADDLE_BROWN = Color.rgb(0x8b4513);

  /** Salmon. */
  public static final Color SALMON = Color.rgb(0xfa8072);

  /** SandyBrown. */
  public static final Color SANDY_BROWN = Color.rgb(0xf4a460);

  /** SeaGreen. */
  public static final Color SEA_GREEN = Color.rgb(0x2e8b57);

  /** SeaShell. */
  public static final Color SEA_SHELL = Color.rgb(0xfff5ee);

  /** Sienna. */
  public static final Color SIENNA = Color.rgb(0xa0522d);

  /** Silver. */
  public static final Color SILVER = Color.rgb(0xc0c0c0);

  /** SkyBlue. */
  public static final Color SKY_BLUE = Color.rgb(0x87ceeb);

  /** SlateBlue. */
  public static final Color SLATE_BLUE = Color.rgb(0x6a5acd);

  /** SlateGray. */
  public static final Color SLATE_GRAY = Color.rgb(0x708090);

  /** SlateGrey. */
  public static final Color SLATE_GREY = Color.rgb(0x708090);

  /** Snow. */
  public static final Color SNOW = Color.rgb(0xfffafa);

  /** SpringGreen. */
  public static final Color SPRING_GREEN = Color.rgb(0x00ff7f);

  /** SteelBlue. */
  public static final Color STEEL_BLUE = Color.rgb(0x4682b4);

  /** Tan. */
  public static final Color TAN = Color.rgb(0xd2b48c);

  /** Teal. */
  public static final Color TEAL = Color.rgb(0x008080);

  /** Thistle. */
  public static final Color THISTLE = Color.rgb(0xd8bfd8);

  /** Tomato. */
  public static final Color TOMATO = Color.rgb(0xff6347);

  /** Turquoise. */
  public static final Color TURQUOISE = Color.rgb(0x40e0d0);

  /** Violet. */
  public static final Color VIOLET = Color.rgb(0xee82ee);

  /** Wheat. */
  public static final Color WHEAT = Color.rgb(0xf5deb3);

  /** White. */
  public static final Color WHITE = Color.rgb(0xffffff);

  /** WhiteSmoke. */
  public static final Color WHITE_SMOKE = Color.rgb(0xf5f5f5);

  /** Yellow. */
  public static final Color YELLOW = Color.rgb(0xffff00);

  /** YellowGreen. */
  public static final Color YELLOW_GREEN = Color.rgb(0x9acd32);

  /**
   * Transparent
   */
  public static final Color TRANSPARENT = Color.argb(0, 0, 0, 0);

  /**
   * Gets a named color
   * @param name Color name, case-insensitive
   * @return Found color, or {@code null}, if no color with the specified name was found.
   */
  @Contract("null -> null")
  public static Color named(String name) {
    if (name == null) {
      return null;
    }

    return NAMES.get(name.toLowerCase());
  }

  static {
    // I do not know why, but using a regular HashMap did not work here,
    // it simply failed to return a correct result sometimes
    Object2ObjectMap<String, Color> map = new Object2ObjectOpenHashMap<>();

    map.put("aliceblue", ALICE_BLUE);
    map.put("antiquewhite", ANTIQUE_WHITE);
    map.put("aqua", AQUA);
    map.put("aquamarine", AQUAMARINE);
    map.put("azure", AZURE);
    map.put("beige", BEIGE);
    map.put("bisque", BISQUE);
    map.put("black", BLACK);
    map.put("blanchedalmond", BLANCHED_ALMOND);
    map.put("blue", BLUE);
    map.put("blueviolet", BLUE_VIOLET);
    map.put("brown", BROWN);
    map.put("burlywood", BURLY_WOOD);
    map.put("cadetblue", CADET_BLUE);
    map.put("chartreuse", CHARTREUSE);
    map.put("chocolate", CHOCOLATE);
    map.put("coral", CORAL);
    map.put("cornflowerblue", CORNFLOWER_BLUE);
    map.put("cornsilk", CORNSILK);
    map.put("crimson", CRIMSON);
    map.put("cyan", CYAN);
    map.put("darkblue", DARK_BLUE);
    map.put("darkcyan", DARK_CYAN);
    map.put("darkgoldenrod", DARK_GOLDEN_ROD);
    map.put("darkgray", DARK_GRAY);
    map.put("darkgrey", DARK_GREY);
    map.put("darkgreen", DARK_GREEN);
    map.put("darkkhaki", DARK_KHAKI);
    map.put("darkmagenta", DARK_MAGENTA);
    map.put("darkolivegreen", DARK_OLIVE_GREEN);
    map.put("darkorange", DARK_ORANGE);
    map.put("darkorchid", DARK_ORCHID);
    map.put("darkred", DARK_RED);
    map.put("darksalmon", DARK_SALMON);
    map.put("darkseagreen", DARK_SEA_GREEN);
    map.put("darkslateblue", DARK_SLATE_BLUE);
    map.put("darkslategray", DARK_SLATE_GRAY);
    map.put("darkslategrey", DARK_SLATE_GREY);
    map.put("darkturquoise", DARK_TURQUOISE);
    map.put("darkviolet", DARK_VIOLET);
    map.put("deeppink", DEEP_PINK);
    map.put("deepskyblue", DEEP_SKY_BLUE);
    map.put("dimgray", DIM_GRAY);
    map.put("dimgrey", DIM_GREY);
    map.put("dodgerblue", DODGER_BLUE);
    map.put("firebrick", FIRE_BRICK);
    map.put("floralwhite", FLORAL_WHITE);
    map.put("forestgreen", FOREST_GREEN);
    map.put("fuchsia", FUCHSIA);
    map.put("gainsboro", GAINSBORO);
    map.put("ghostwhite", GHOST_WHITE);
    map.put("gold", GOLD);
    map.put("goldenrod", GOLDEN_ROD);
    map.put("gray", GRAY);
    map.put("grey", GREY);
    map.put("green", GREEN);
    map.put("greenyellow", GREEN_YELLOW);
    map.put("honeydew", HONEY_DEW);
    map.put("hotpink", HOT_PINK);
    map.put("indianred", INDIAN_RED);
    map.put("indigo", INDIGO);
    map.put("ivory", IVORY);
    map.put("khaki", KHAKI);
    map.put("lavender", LAVENDER);
    map.put("lavenderblush", LAVENDER_BLUSH);
    map.put("lawngreen", LAWN_GREEN);
    map.put("lemonchiffon", LEMON_CHIFFON);
    map.put("lightblue", LIGHT_BLUE);
    map.put("lightcoral", LIGHT_CORAL);
    map.put("lightcyan", LIGHT_CYAN);
    map.put("lightgoldenrodyellow", LIGHT_GOLDEN_ROD_YELLOW);
    map.put("lightgray", LIGHT_GRAY);
    map.put("lightgrey", LIGHT_GREY);
    map.put("lightgreen", LIGHT_GREEN);
    map.put("lightpink", LIGHT_PINK);
    map.put("lightsalmon", LIGHT_SALMON);
    map.put("lightseagreen", LIGHT_SEA_GREEN);
    map.put("lightskyblue", LIGHT_SKY_BLUE);
    map.put("lightslategray", LIGHT_SLATE_GRAY);
    map.put("lightslategrey", LIGHT_SLATE_GREY);
    map.put("lightsteelblue", LIGHT_STEEL_BLUE);
    map.put("lightyellow", LIGHT_YELLOW);
    map.put("lime", LIME);
    map.put("limegreen", LIME_GREEN);
    map.put("linen", LINEN);
    map.put("magenta", MAGENTA);
    map.put("maroon", MAROON);
    map.put("mediumaquamarine", MEDIUM_AQUA_MARINE);
    map.put("mediumblue", MEDIUM_BLUE);
    map.put("mediumorchid", MEDIUM_ORCHID);
    map.put("mediumpurple", MEDIUM_PURPLE);
    map.put("mediumseagreen", MEDIUM_SEA_GREEN);
    map.put("mediumslateblue", MEDIUM_SLATE_BLUE);
    map.put("mediumspringgreen", MEDIUM_SPRING_GREEN);
    map.put("mediumturquoise", MEDIUM_TURQUOISE);
    map.put("mediumvioletred", MEDIUM_VIOLET_RED);
    map.put("midnightblue", MIDNIGHT_BLUE);
    map.put("mintcream", MINT_CREAM);
    map.put("mistyrose", MISTY_ROSE);
    map.put("moccasin", MOCCASIN);
    map.put("navajowhite", NAVAJO_WHITE);
    map.put("navy", NAVY);
    map.put("oldlace", OLD_LACE);
    map.put("olive", OLIVE);
    map.put("olivedrab", OLIVE_DRAB);
    map.put("orange", ORANGE);
    map.put("orangered", ORANGE_RED);
    map.put("orchid", ORCHID);
    map.put("palegoldenrod", PALE_GOLDEN_ROD);
    map.put("palegreen", PALE_GREEN);
    map.put("paleturquoise", PALE_TURQUOISE);
    map.put("palevioletred", PALE_VIOLET_RED);
    map.put("papayawhip", PAPAYA_WHIP);
    map.put("peachpuff", PEACH_PUFF);
    map.put("peru", PERU);
    map.put("pink", PINK);
    map.put("plum", PLUM);
    map.put("powderblue", POWDER_BLUE);
    map.put("purple", PURPLE);
    map.put("rebeccapurple", REBECCA_PURPLE);
    map.put("red", RED);
    map.put("rosybrown", ROSY_BROWN);
    map.put("royalblue", ROYAL_BLUE);
    map.put("saddlebrown", SADDLE_BROWN);
    map.put("salmon", SALMON);
    map.put("sandybrown", SANDY_BROWN);
    map.put("seagreen", SEA_GREEN);
    map.put("seashell", SEA_SHELL);
    map.put("sienna", SIENNA);
    map.put("silver", SILVER);
    map.put("skyblue", SKY_BLUE);
    map.put("slateblue", SLATE_BLUE);
    map.put("slategray", SLATE_GRAY);
    map.put("slategrey", SLATE_GREY);
    map.put("snow", SNOW);
    map.put("springgreen", SPRING_GREEN);
    map.put("steelblue", STEEL_BLUE);
    map.put("tan", TAN);
    map.put("teal", TEAL);
    map.put("thistle", THISTLE);
    map.put("tomato", TOMATO);
    map.put("turquoise", TURQUOISE);
    map.put("violet", VIOLET);
    map.put("wheat", WHEAT);
    map.put("white", WHITE);
    map.put("whitesmoke", WHITE_SMOKE);
    map.put("yellow", YELLOW);
    map.put("yellowgreen", YELLOW_GREEN);
    map.put("transparent", TRANSPARENT);

    Int2ObjectMap<String> nameLookup = new Int2ObjectOpenHashMap<>();
    map.forEach((s, color) -> nameLookup.put(color.argb(), s));

    NAMES = Object2ObjectMaps.unmodifiable(map);
    VALUE_TO_NAME = Int2ObjectMaps.unmodifiable(nameLookup);
  }
}
