package net.arcadiusmc.chimera;

import com.google.common.base.Strings;
import java.util.Optional;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.ChimeraParser;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.Interpreter;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.ImportantMarker;
import net.arcadiusmc.chimera.system.StyleObjectModel;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.BoxSizing;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.StyleProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

public class PropertiesMap extends ReadonlyProperties implements StyleProperties {

  private final @Nullable StyleObjectModel system;

  public PropertiesMap(PropertySet set, @Nullable StyleObjectModel system) {
    super(set);
    this.system = system;
  }

  public boolean importantAllowed() {
    return true;
  }

  private <T> void parse(Property<T> property, String value) {
    ChimeraParser parser = new ChimeraParser(value);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("<property value>");
    errors.setListener(error -> {
      StyleLoggers.getLogger()
          .atLevel(error.getLevel())
          .setMessage(error.getFormattedError())
          .log();
    });

    ChimeraContext ctx = new ChimeraContext(parser.getStream().getInput());
    ctx.setErrors(errors);

    Scope scope = Scope.createTopLevel();
    if (system != null) {
      scope.getVariableMap().putAll(system.getVariables());
    }

    Expression expression = parser.expr();
    ImportantMarker marker = parser.importantMarker();

    if (!importantAllowed() && marker != null) {
      errors.error(marker.getStart(), "'!important' not allowed here");
    }

    Interpreter inter = new Interpreter(ctx, scope);
    Object obj = expression.visit(inter);

    if (obj == null) {
      return;
    }

    Value<T> cssValue = Chimera.coerceCssValue(
        value,
        marker != null,
        property,
        obj,
        errors,
        Location.START
    );

    long errorCount = errors.getErrors().stream()
        .filter(err -> err.getLevel() == Level.ERROR)
        .count();

    if (errorCount > 0) {
      return;
    }

    set.setValue(property, cssValue);
  }

  private <T> void set(Property<T> property, T value) {
    if (value == null) {
      set.remove(property);
      return;
    }

    Optional<String> errorOpt = property.validateValue(value);
    if (errorOpt.isPresent()) {
      StyleLoggers.getLogger().error(
          "Invalid value for property {}: {}",
          property.getKey(),
          errorOpt.get()
      );

      return;
    }

    set.set(property, value);
  }

  public PropertiesMap triggerChange() {
    return this;
  }

  @Override
  public PropertiesMap setColor(@Nullable String value) {
    parse(Properties.COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setColor(@Nullable Color value) {
    set(Properties.COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBackgroundColor(@Nullable String value) {
    parse(Properties.BACKGROUND_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBackgroundColor(@Nullable Color value) {
    set(Properties.BACKGROUND_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderColor(@Nullable String value) {
    parse(Properties.BORDER_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderColor(@Nullable Color value) {
    set(Properties.BORDER_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineColor(@Nullable String value) {
    parse(Properties.OUTLINE_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineColor(@Nullable Color value) {
    set(Properties.OUTLINE_COLOR, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setTextShadow(@Nullable String value) {
    parse(Properties.TEXT_SHADOW, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setTextShadow(@Nullable Boolean value) {
    set(Properties.TEXT_SHADOW, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBold(@Nullable String value) {
    parse(Properties.BOLD, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBold(@Nullable Boolean value) {
    set(Properties.BOLD, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setItalic(@Nullable String value) {
    parse(Properties.ITALIC, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setItalic(@Nullable Boolean value) {
    set(Properties.ITALIC, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setUnderlined(@Nullable String value) {
    parse(Properties.UNDERLINED, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setUnderlined(@Nullable Boolean value) {
    set(Properties.UNDERLINED, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setStrikethrough(@Nullable String value) {
    parse(Properties.STRIKETHROUGH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setStrikethrough(@Nullable Boolean value) {
    set(Properties.STRIKETHROUGH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setObfuscated(@Nullable String value) {
    parse(Properties.OBFUSCATED, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setObfuscated(@Nullable Boolean value) {
    set(Properties.OBFUSCATED, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setDisplay(@Nullable String value) {
    parse(Properties.DISPLAY, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setDisplay(@Nullable DisplayType value) {
    set(Properties.DISPLAY, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setScale(@Nullable String value) {
    parse(Properties.SCALE, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setScale(@Nullable Primitive value) {
    set(Properties.SCALE, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setWidth(@Nullable String value) {
    parse(Properties.WIDTH, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setWidth(@Nullable Primitive value) {
    set(Properties.WIDTH, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setHeight(@Nullable String value) {
    parse(Properties.HEIGHT, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setHeight(@Nullable Primitive value) {
    set(Properties.HEIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMaxWidth(@Nullable String value) {
    parse(Properties.MAX_WIDTH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMaxWidth(@Nullable Primitive value) {
    set(Properties.MAX_WIDTH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMinWidth(@Nullable String value) {
    parse(Properties.MIN_WIDTH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMinWidth(@Nullable Primitive value) {
    set(Properties.MIN_WIDTH, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMaxHeight(@Nullable String value) {
    parse(Properties.MAX_HEIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMaxHeight(@Nullable Primitive value) {
    set(Properties.MAX_HEIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMinHeight(@Nullable String value) {
    parse(Properties.MIN_HEIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMinHeight(@Nullable Primitive value) {
    set(Properties.MIN_HEIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPadding(@Nullable String value) {
    parse(Properties.PADDING, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPadding(Primitive value) {
    set(Properties.PADDING, PrimitiveRect.create(value));
    return triggerChange();
  }

  @Override
  public PropertiesMap setPadding(Primitive x, Primitive y) {
    set(Properties.PADDING, PrimitiveRect.create(x, y));
    return triggerChange();
  }

  @Override
  public PropertiesMap setPadding(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.PADDING, PrimitiveRect.create(top, x, bottom));
    return triggerChange();
  }

  @Override
  public PropertiesMap setPadding(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.PADDING, PrimitiveRect.create(top, right, bottom, left));
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingTop(@Nullable String value) {
    parse(Properties.PADDING_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingTop(@Nullable Primitive value) {
    set(Properties.PADDING_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingRight(@Nullable String value) {
    parse(Properties.PADDING_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingRight(@Nullable Primitive value) {
    set(Properties.PADDING_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingBottom(@Nullable String value) {
    parse(Properties.PADDING_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingBottom(@Nullable Primitive value) {
    set(Properties.PADDING_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingLeft(@Nullable String value) {
    parse(Properties.PADDING_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setPaddingLeft(@Nullable Primitive value) {
    set(Properties.PADDING_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutline(@Nullable String value) {
    parse(Properties.OUTLINE, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutline(Primitive value) {
    set(Properties.OUTLINE, PrimitiveRect.create(value));
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutline(Primitive x, Primitive y) {
    set(Properties.OUTLINE, PrimitiveRect.create(x, y));
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutline(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.OUTLINE, PrimitiveRect.create(top, x, bottom));
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutline(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.OUTLINE, PrimitiveRect.create(top, right, bottom, left));
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineTop(@Nullable String value) {
    parse(Properties.OUTLINE_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineTop(@Nullable Primitive value) {
    set(Properties.OUTLINE_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineRight(@Nullable String value) {
    parse(Properties.OUTLINE_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineRight(@Nullable Primitive value) {
    set(Properties.OUTLINE_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineBottom(@Nullable String value) {
    parse(Properties.OUTLINE_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineBottom(@Nullable Primitive value) {
    set(Properties.OUTLINE_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineLeft(@Nullable String value) {
    parse(Properties.OUTLINE_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOutlineLeft(@Nullable Primitive value) {
    set(Properties.OUTLINE_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorder(@Nullable String value) {
    parse(Properties.BORDER, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorder(Primitive value) {
    set(Properties.BORDER, PrimitiveRect.create(value));
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorder(Primitive x, Primitive y) {
    set(Properties.BORDER, PrimitiveRect.create(x, y));
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorder(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.BORDER, PrimitiveRect.create(top, x, bottom));
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorder(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.BORDER, PrimitiveRect.create(top, right, bottom, left));
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderTop(@Nullable String value) {
    parse(Properties.BORDER_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderTop(@Nullable Primitive value) {
    set(Properties.BORDER_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderRight(@Nullable String value) {
    parse(Properties.BORDER_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderRight(@Nullable Primitive value) {
    set(Properties.BORDER_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderBottom(@Nullable String value) {
    parse(Properties.BORDER_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderBottom(@Nullable Primitive value) {
    set(Properties.BORDER_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderLeft(@Nullable String value) {
    parse(Properties.BORDER_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setBorderLeft(@Nullable Primitive value) {
    set(Properties.BORDER_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMargin(@Nullable String value) {
    parse(Properties.MARGIN, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMargin(Primitive value) {
    set(Properties.MARGIN, PrimitiveRect.create(value));
    return triggerChange();
  }

  @Override
  public PropertiesMap setMargin(Primitive x, Primitive y) {
    set(Properties.MARGIN, PrimitiveRect.create(x, y));
    return triggerChange();
  }

  @Override
  public PropertiesMap setMargin(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.MARGIN, PrimitiveRect.create(top, x, bottom));
    return triggerChange();
  }

  @Override
  public PropertiesMap setMargin(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.MARGIN, PrimitiveRect.create(top, right, bottom, left));
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginTop(@Nullable String value) {
    parse(Properties.MARGIN_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginTop(@Nullable Primitive value) {
    set(Properties.MARGIN_TOP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginRight(@Nullable String value) {
    parse(Properties.MARGIN_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginRight(@Nullable Primitive value) {
    set(Properties.MARGIN_RIGHT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginBottom(@Nullable String value) {
    parse(Properties.MARGIN_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginBottom(@Nullable Primitive value) {
    set(Properties.MARGIN_BOTTOM, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginLeft(@Nullable String value) {
    parse(Properties.MARGIN_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setMarginLeft(@Nullable Primitive value) {
    set(Properties.MARGIN_LEFT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setZIndex(@Nullable String value) {
    parse(Properties.Z_INDEX, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setZIndex(@Nullable Integer value) {
    set(Properties.Z_INDEX, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setAlignItems(@Nullable String value) {
    parse(Properties.ALIGN_ITEMS, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setAlignItems(@Nullable AlignItems value) {
    set(Properties.ALIGN_ITEMS, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setFlexDirection(@Nullable String value) {
    parse(Properties.FLEX_DIRECTION, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setFlexDirection(@Nullable FlexDirection value) {
    set(Properties.FLEX_DIRECTION, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setFlexWrap(@Nullable String value) {
    parse(Properties.FLEX_WRAP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setFlexWrap(@Nullable FlexWrap value) {
    set(Properties.FLEX_WRAP, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setJustifyContent(@Nullable String value) {
    parse(Properties.JUSTIFY_CONTENT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setJustifyContent(@Nullable JustifyContent value) {
    set(Properties.JUSTIFY_CONTENT, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOrder(@Nullable String value) {
    parse(Properties.ORDER, value);
    return triggerChange();
  }

  @Override
  public PropertiesMap setOrder(@Nullable Integer value) {
    set(Properties.ORDER, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setBoxSizing(@Nullable BoxSizing value) {
    set(Properties.BOX_SIZING, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setBoxSizing(@Nullable String value) {
    parse(Properties.BOX_SIZING, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInlineStart(@Nullable Primitive value) {
    set(Properties.MARGIN_INLINE_START, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInlineStart(@Nullable String value) {
    parse(Properties.MARGIN_INLINE_START, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInlineEnd(@Nullable Primitive value) {
    set(Properties.MARGIN_INLINE_END, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInlineEnd(@Nullable String value) {
    parse(Properties.MARGIN_INLINE_END, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInline(@Nullable String value) {
    parse(Properties.MARGIN_INLINE, value);
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInline(@Nullable Primitive value) {
    set(Properties.MARGIN_INLINE, value == null ? null : new PrimitiveLeftRight(value, value));
    return triggerChange();
  }

  @Override
  public StyleProperties setMarginInline(@Nullable Primitive start, @Nullable Primitive end) {
    set(Properties.MARGIN_INLINE, new PrimitiveLeftRight(start, end));
    return triggerChange();
  }

  @Override
  public StyleProperties setProperty(@NotNull String propertyName, @Nullable String value) {
    if (Strings.isNullOrEmpty(propertyName)) {
      return this;
    }

    Property<Object> prop = Properties.getByKey(propertyName);
    if (prop == null) {
      return null;
    }

    set(prop, value);
    return triggerChange();
  }
}

