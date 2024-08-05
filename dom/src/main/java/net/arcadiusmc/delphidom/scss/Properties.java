package net.arcadiusmc.delphidom.scss;

import static net.arcadiusmc.delphi.Screen.DEFAULT_HEIGHT;
import static net.arcadiusmc.delphi.Screen.DEFAULT_WIDTH;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphidom.Consts;
import net.arcadiusmc.delphidom.scss.Property.StyleFunction;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;

public final class Properties {
  private Properties() {}

  private static final Map<String, Property<?>> REGISTRY = new HashMap<>();
  private static Property[] idLookup = new Property[32];


  public static final Property<Color> TEXT_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((s, screen, color) -> s.textColor = color)
      .build();

  public static final Property<Color> BACKGROUND_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.TRANSPARENT)
      .cascading(false)
      .layoutAffecting(false)
      .function((n, screen, color) -> n.backgroundColor = color)
      .build();

  public static final Property<Color> BORDER_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(false)
      .layoutAffecting(false)
      .function((n, screen, color) -> n.borderColor = color)
      .build();

  public static final Property<Color> OUTLINE_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(false)
      .layoutAffecting(false)
      .function((n, screen, color) -> n.outlineColor = color)
      .build();

  public static final Property<Boolean> TEXT_SHADOW = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((n,s,v) -> n.textShadowed = v)
      .build();

  public static final Property<Boolean> BOLD = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(true)
      .contentAffecting(true)
      .function((s, screen, b) -> s.bold = b)
      .build();

  public static final Property<Boolean> ITALIC = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((s, screen, b) -> s.italic = b)
      .build();

  public static final Property<Boolean> UNDERLINED = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((s, screen, b) -> s.underlined = b)
      .build();

  public static final Property<Boolean> STRIKETHROUGH = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((s, screen, b) -> s.strikethrough = b)
      .build();

  public static final Property<Boolean> OBFUSCATED = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(false)
      .layoutAffecting(false)
      .contentAffecting(true)
      .function((s, screen, b) -> s.obfuscated = b)
      .build();

  public static final Property<DisplayType> DISPLAY = Property.builder(DisplayType.class)
      .defaultValue(DisplayType.INLINE)
      .cascading(true)
      .layoutAffecting(true)
      .function((s, screen, d) -> s.display = d)
      .build();

  public static final Property<Primitive> SCALE = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(1))
      .cascading(true)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.scale.set(v)))
      .build();

  public static final Property<Primitive> MIN_WIDTH = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.minSize.x = v))
      .build();

  public static final Property<Primitive> MIN_HEIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.minSize.y = v))
      .build();

  public static final Property<Primitive> MAX_WIDTH = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(Float.MAX_VALUE))
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.maxSize.x = v))
      .build();

  public static final Property<Primitive> MAX_HEIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(Float.MAX_VALUE))
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.maxSize.y = v))
      .build();

  public static final Property<Primitive> BORDER_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.border.top = v))
      .build();

  public static final Property<Primitive> BORDER_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.border.bottom = v))
      .build();

  public static final Property<Primitive> BORDER_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.border.left = v))
      .build();

  public static final Property<Primitive> BORDER_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.border.right = v))
      .build();

  public static final Property<Primitive> OUTLINE_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.outline.top = v))
      .build();

  public static final Property<Primitive> OUTLINE_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.outline.bottom = v))
      .build();

  public static final Property<Primitive> OUTLINE_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.outline.left = v))
      .build();

  public static final Property<Primitive> OUTLINE_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.outline.right = v))
      .build();

  public static final Property<Primitive> PADDING_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.padding.top = v))
      .build();

  public static final Property<Primitive> PADDING_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.padding.bottom = v))
      .build();

  public static final Property<Primitive> PADDING_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.padding.left = v))
      .build();

  public static final Property<Primitive> PADDING_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.padding.right = v))
      .build();

  public static final Property<Primitive> MARGIN_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.margin.top = v))
      .build();

  public static final Property<Primitive> MARGIN_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.margin.bottom = v))
      .build();

  public static final Property<Primitive> MARGIN_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.margin.left = v))
      .build();

  public static final Property<Primitive> MARGIN_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .function(createFunction((n, v) -> n.margin.right = v))
      .build();

  public static final Property<Integer> Z_INDEX = Property.builder(Integer.class)
      .defaultValue(0)
      .cascading(true)
      .layoutAffecting(false)
      .function((n, screen, integer) -> n.zindex = integer)
      .build();

  static {
    registerAll();
  }

  public static <T> Property<T> getByKey(String key) {
    return (Property<T>) REGISTRY.get(key);
  }

  public static <T> Property<T> getById(int id) {
    Objects.checkIndex(id, REGISTRY.size());
    return idLookup[id];
  }

  public static int count() {
    return REGISTRY.size();
  }

  private static StyleFunction<Primitive> createFunction(BiConsumer<ComputedStyle, Float> f) {
    return (s, screen, primitive) -> f.accept(s, resolve(primitive, screen));
  }

  public static float resolve(Primitive primitive, Screen screen) {
    float v = primitive.getValue();

    return switch (primitive.getUnit()) {
      case NONE -> v;
      case VW -> (v / 100.0f) * (screen == null ? DEFAULT_WIDTH : screen.getWidth());
      case VH -> (v / 100.0f) * (screen == null ? DEFAULT_HEIGHT : screen.getHeight());
      case PX -> v * Consts.CHAR_PX_SIZE;
      case CH -> v * Consts.LEN0_PX;
    };
  }

  private static void registerAll() {
    register("color",            TEXT_COLOR);
    register("background-color", BACKGROUND_COLOR);
    register("outline-color",    OUTLINE_COLOR);

    register("border-color",     BORDER_COLOR);
    //register("align",            ALIGN_DIRECTION);
    register("text-shadow",      TEXT_SHADOW);
    register("scale",            SCALE);
    register("z-index",          Z_INDEX);

    register("bold",             BOLD);
    register("italic",           ITALIC);
    register("underlined",       UNDERLINED);
    register("strikethrough",    STRIKETHROUGH);
    register("obfuscated",       OBFUSCATED);

    register("min-width",        MIN_WIDTH);
    register("min-height",       MIN_HEIGHT);

    register("max-width",        MAX_WIDTH);
    register("max-height",       MAX_HEIGHT);

    register("border-top",       BORDER_TOP);
    register("border-bottom",    BORDER_BOTTOM);
    register("border-left",      BORDER_LEFT);
    register("border-right",     BORDER_RIGHT);

    register("outline-top",      OUTLINE_TOP);
    register("outline-bottom",   OUTLINE_BOTTOM);
    register("outline-left",     OUTLINE_LEFT);
    register("outline-right",    OUTLINE_RIGHT);

    register("padding-top",      PADDING_TOP);
    register("padding-bottom",   PADDING_BOTTOM);
    register("padding-left",     PADDING_LEFT);
    register("padding-right",    PADDING_RIGHT);

    register("margin-top",       MARGIN_TOP);
    register("margin-bottom",    MARGIN_BOTTOM);
    register("margin-left",      MARGIN_LEFT);
    register("margin-right",     MARGIN_RIGHT);
  }

  private static void register(String key, Property<?> property) {
    if (REGISTRY.containsKey(key)) {
      throw new IllegalArgumentException("Key '" + key + " is already registered");
    }

    int nextId = REGISTRY.size();

    REGISTRY.put(key, property);

    property.id = nextId;
    property.key = key;

    idLookup = ObjectArrays.ensureCapacity(idLookup, nextId + 1);
    idLookup[nextId] = property;
  }
}
