package net.arcadiusmc.chimera;

import static net.arcadiusmc.chimera.PropertyValidator.NON_ANGLE;
import static net.arcadiusmc.chimera.PropertyValidator.NON_ANGLE_RECT;
import static net.arcadiusmc.chimera.PropertyValidator.SCALAR;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Map;
import java.util.Objects;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.BoxSizing;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;

public final class Properties {
  private Properties() {}

  private static final Map<String, Property> nameLookup = new Object2ObjectOpenHashMap<>();
  private static Property[] idLookup = new Property[50];
  private static int nextId = 0;


  public static final Property<Color> COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Color> BACKGROUND_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.TRANSPARENT)
      .cascading(false)
      .layoutAffecting(false)
      .visualAffecting(true)
      .build();

  public static final Property<Color> BORDER_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(false)
      .layoutAffecting(false)
      .visualAffecting(true)
      .build();

  public static final Property<Color> OUTLINE_COLOR = Property.builder(Color.class)
      .defaultValue(NamedColor.BLACK)
      .cascading(false)
      .layoutAffecting(false)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> TEXT_SHADOW = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> BOLD = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(true)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> ITALIC = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> UNDERLINED = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> STRIKETHROUGH = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<Boolean> OBFUSCATED = Property.builder(Boolean.class)
      .defaultValue(false)
      .cascading(true)
      .layoutAffecting(false)
      .contentAffecting(true)
      .visualAffecting(true)
      .build();

  public static final Property<DisplayType> DISPLAY = Property.builder(DisplayType.class)
      .defaultValue(DisplayType.DEFAULT)
      .cascading(true)
      .layoutAffecting(true)
      .build();

  public static final Property<Primitive> SCALE = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(1))
      .cascading(true)
      .layoutAffecting(true)
      .validator(SCALAR)
      .build();

  public static final Property<Primitive> WIDTH = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> HEIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MIN_WIDTH = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MIN_HEIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MAX_WIDTH = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(Float.MAX_VALUE))
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MAX_HEIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.create(Float.MAX_VALUE))
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> BORDER_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> BORDER_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> BORDER_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> BORDER_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> OUTLINE_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> OUTLINE_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> OUTLINE_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> OUTLINE_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> PADDING_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> PADDING_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> PADDING_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> PADDING_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MARGIN_TOP = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MARGIN_BOTTOM = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MARGIN_LEFT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<Primitive> MARGIN_RIGHT = Property.builder(Primitive.class)
      .defaultValue(Primitive.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE)
      .build();

  public static final Property<PrimitiveRect> PADDING = Property.builder(PrimitiveRect.class)
      .defaultValue(PrimitiveRect.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE_RECT)
      .build();

  public static final Property<PrimitiveRect> BORDER = Property.builder(PrimitiveRect.class)
      .defaultValue(PrimitiveRect.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE_RECT)
      .build();

  public static final Property<PrimitiveRect> OUTLINE = Property.builder(PrimitiveRect.class)
      .defaultValue(PrimitiveRect.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE_RECT)
      .build();

  public static final Property<PrimitiveRect> MARGIN = Property.builder(PrimitiveRect.class)
      .defaultValue(PrimitiveRect.ZERO)
      .cascading(false)
      .layoutAffecting(true)
      .validator(NON_ANGLE_RECT)
      .build();

  public static final Property<Integer> Z_INDEX = Property.builder(Integer.class)
      .defaultValue(0)
      .cascading(true)
      .layoutAffecting(false)
      .build();

  public static final Property<AlignItems> ALIGN_ITEMS = Property.builder(AlignItems.class)
      .defaultValue(AlignItems.DEFAULT)
      .cascading(false)
      .layoutAffecting(true)
      .build();

  public static final Property<FlexDirection> FLEX_DIRECTION = Property.builder(FlexDirection.class)
      .defaultValue(FlexDirection.DEFAULT)
      .cascading(false)
      .layoutAffecting(true)
      .build();

  public static final Property<FlexWrap> FLEX_WRAP = Property.builder(FlexWrap.class)
      .defaultValue(FlexWrap.DEFAULT)
      .cascading(false)
      .layoutAffecting(true)
      .build();

  public static final Property<JustifyContent> JUSTIFY_CONTENT
      = Property.builder(JustifyContent.class)
      .defaultValue(JustifyContent.DEFAULT)
      .cascading(false)
      .layoutAffecting(true)
      .build();

  public static final Property<Integer> ORDER = Property.builder(Integer.class)
      .defaultValue(0)
      .cascading(false)
      .layoutAffecting(true)
      .build();

  public static final Property<BoxSizing> BOX_SIZING = Property.builder(BoxSizing.class)
      .defaultValue(BoxSizing.CONTENT_BOX)
      .cascading(false)
      .layoutAffecting(true)
      .contentAffecting(false)
      .visualAffecting(true)
      .build();

  static {
    registerAll();
  }

  public static <T> Property<T> getByKey(String key) {
    return (Property<T>) nameLookup.get(key);
  }

  public static <T> Property<T> getById(int id) {
    Objects.checkIndex(id, nameLookup.size());
    return idLookup[id];
  }

  public static int count() {
    return nameLookup.size();
  }

  private static void registerAll() {
    register("color",             COLOR);
    register("background-color",  BACKGROUND_COLOR);
    register("outline-color",     OUTLINE_COLOR);

    register("align-items",       ALIGN_ITEMS);
    register("flex-direction",    FLEX_DIRECTION);
    register("flex-wrap",         FLEX_WRAP);
    register("justify-content",   JUSTIFY_CONTENT);
    register("order",             ORDER);

    register("box-sizing",        BOX_SIZING);

    register("border-color",      BORDER_COLOR);
    register("display",           DISPLAY);
    register("text-shadow",       TEXT_SHADOW);
    register("scale",             SCALE);
    register("z-index",           Z_INDEX);

    register("bold",              BOLD);
    register("italic",            ITALIC);
    register("underlined",        UNDERLINED);
    register("strikethrough",     STRIKETHROUGH);
    register("obfuscated",        OBFUSCATED);

    register("width",             WIDTH);
    register("height",            HEIGHT);

    register("min-width",         MIN_WIDTH);
    register("min-height",        MIN_HEIGHT);

    register("max-width",         MAX_WIDTH);
    register("max-height",        MAX_HEIGHT);

    register("border-top",        BORDER_TOP);
    register("border-bottom",     BORDER_BOTTOM);
    register("border-left",       BORDER_LEFT);
    register("border-right",      BORDER_RIGHT);

    register("outline-top",       OUTLINE_TOP);
    register("outline-bottom",    OUTLINE_BOTTOM);
    register("outline-left",      OUTLINE_LEFT);
    register("outline-right",     OUTLINE_RIGHT);

    register("padding-top",       PADDING_TOP);
    register("padding-bottom",    PADDING_BOTTOM);
    register("padding-left",      PADDING_LEFT);
    register("padding-right",     PADDING_RIGHT);

    register("margin-top",        MARGIN_TOP);
    register("margin-bottom",     MARGIN_BOTTOM);
    register("margin-left",       MARGIN_LEFT);
    register("margin-right",      MARGIN_RIGHT);

    register("padding",           PADDING);
    register("outline",           OUTLINE);
    register("border",            BORDER);
    register("margin",            MARGIN);
  }

  private static <T> void register(String key, Property<T> property) {
    if (nameLookup.containsKey(key)) {
      throw new IllegalArgumentException("Key already registered: " + key);
    }

    nameLookup.put(key, property);
    property.key = key;

    int id = nextId++;
    if (id >= idLookup.length) {
      idLookup = ObjectArrays.forceCapacity(idLookup, idLookup.length + 10, idLookup.length);
    }

    idLookup[id] = property;
    property.id = id;
  }
}
