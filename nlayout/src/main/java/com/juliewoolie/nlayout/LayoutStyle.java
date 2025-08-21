package com.juliewoolie.nlayout;

import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.dom.style.AlignItems;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.FlexWrap;
import com.juliewoolie.dom.style.JustifyContent;
import com.juliewoolie.dom.style.VerticalAlign;
import com.juliewoolie.dom.style.Visibility;
import org.joml.Vector2f;

public class LayoutStyle {

  public static final float UNSET = -1.0f;

  public final Rect padding = new Rect();
  public final Rect border = new Rect();
  public final Rect outline = new Rect();
  public final Rect margin = new Rect();

  public DisplayType display = DisplayType.DEFAULT;

  public final Vector2f size = new Vector2f(UNSET);
  public final Vector2f minSize = new Vector2f(UNSET);
  public final Vector2f maxSize = new Vector2f(UNSET);

  public float marginInlineStart = 0.0f;
  public float marginInlineEnd = 0.0f;
  public float fontSize = 1.0f;
  public float flexBasis = UNSET;
  public float rowGap = 0;
  public float columnGap = 0;

  public int order = 0;
  public int grow = 0;
  public int shrink = 0;

  public AlignItems alignItems = AlignItems.DEFAULT;
  public AlignItems alignSelf = AlignItems.DEFAULT;
  public FlexDirection flexDirection = FlexDirection.DEFAULT;
  public FlexWrap flexWrap = FlexWrap.DEFAULT;
  public JustifyContent justify = JustifyContent.DEFAULT;
  public BoxSizing boxSizing = BoxSizing.DEFAULT;
  public Visibility visibility = Visibility.DEFAULT;
  public VerticalAlign verticalAlign = VerticalAlign.DEFAULT;
}
