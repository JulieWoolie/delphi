package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.google.common.base.Strings;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.dom.Element;
import com.juliewoolie.nlayout.LayoutBox;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringJoiner;
import org.joml.Vector2f;

public class BoxModelTab extends DevToolTab {

  static final float[] EMPTY_RECT = {0,0,0,0};
  static final float[] EMPTY_VEC = {0,0};

  public BoxModelTab(Devtools devtools) {
    super(devtools);
  }

  private Element createSingleLine(NumberFormat format, float value) {
    Element div = document.createElement("div");
    div.setClassName("box-firstline");

    Element center = document.createElement("div");
    center.setClassName("center");
    center.setTextContent(format.format(value));

    div.appendChild(center);
    return div;
  }

  private Element createBox(NumberFormat format, Rect rect, Element innerBox) {
    Element box = document.createElement("div");
    box.setClassName("box");

    Element top = createSingleLine(format, rect == null ? 0 : rect.top);
    Element bottom = createSingleLine(format, rect == null ? 0 : rect.bottom);

    Element left = document.createElement("div");
    Element right = document.createElement("div");
    left.setClassName("x-measurement");
    right.setClassName("x-measurement");
    left.setTextContent(format.format(rect == null ? 0 : rect.left));
    right.setTextContent(format.format(rect == null ? 0 : rect.right));

    box.appendChild(top);

    box.appendChild(left);
    box.appendChild(innerBox);
    box.appendChild(right);

    box.appendChild(bottom);

    return box;
  }

  private Element createElementBoxes(NumberFormat format, ElementRenderObject ro) {
    Vector2f size = new Vector2f();
    if (ro != null) {
      size.set(ro.size);
      LayoutBox.subtractExtraSpace(size, ro.style);
    }

    Element innerSize = document.createElement("div");
    innerSize.setClassName("box-middle bottombox content");

    Element dimensions = document.createElement("div");
    dimensions.setClassName("center mt-quarter");
    dimensions.setTextContent(
        String.format("%s x %s", format.format(size.x), format.format(size.y))
    );
    innerSize.appendChild(dimensions);

    Element paddingBox = createBox(format, ro == null ? null : ro.style.padding, innerSize);
    paddingBox.getClassList().add("box-middle");
    paddingBox.getClassList().add("padding");

    Element borderBox = createBox(format, ro == null ? null : ro.style.border, paddingBox);
    borderBox.getClassList().add("box-middle");
    borderBox.getClassList().add("border");

    Element outlineBox = createBox(format, ro == null ? null : ro.style.outline, borderBox);
    outlineBox.getClassList().add("box-middle");
    outlineBox.getClassList().add("outline");

    Element marginBox = createBox(format, ro == null ? null : ro.style.margin, outlineBox);
    marginBox.getClassList().add("topbox");
    marginBox.getClassList().add("margin");

    return marginBox;
  }

  float[] fromRect(Rect r) {
    return new float[] {r.top, r.right, r.bottom, r.left};
  }

  float[] fromVec2f(Vector2f v) {
    return new float[] {v.x, v.y};
  }

  Element createMetadata(NumberFormat format, ElementRenderObject ero, Locale l) {
    Element container = document.createElement("div");
    container.setClassName("boxmetadata");

    String[] fields = {
        "delphi.devtools.boxmodel.margin",
        "delphi.devtools.boxmodel.outline",
        "delphi.devtools.boxmodel.border",
        "delphi.devtools.boxmodel.padding",
        "delphi.devtools.boxmodel.elementSize",
        "delphi.devtools.boxmodel.innerSize",
        "delphi.devtools.boxmodel.position",
    };
    String[] extraClasses = {
        "margin",
        "outline",
        "border",
        "padding",
        "",
        "content",
        "",
    };
    float[][] fieldValues = new float[fields.length][];

    if (ero == null) {
      for (int i = 0; i < 4; i++) {
        fieldValues[i] = EMPTY_RECT;
      }
      for (int i = 4; i < fields.length; i++) {
        fieldValues[i] = EMPTY_VEC;
      }
    } else {
      FullStyle s = ero.style;
      fieldValues[0] = fromRect(s.margin);
      fieldValues[1] = fromRect(s.outline);
      fieldValues[2] = fromRect(s.border);
      fieldValues[3] = fromRect(s.padding);
      fieldValues[4] = fromVec2f(ero.size);
      fieldValues[6] = fromVec2f(ero.position);

      Vector2f innerSize = new Vector2f(ero.size);
      LayoutBox.subtractExtraSpace(innerSize, s);
      fieldValues[5] = fromVec2f(innerSize);
    }

    for (int i = 0; i < fields.length; i++) {
      String field = translateToString(l, fields[i]);
      float[] value = fieldValues[i];

      StringJoiner joiner = new StringJoiner(", ");
      for (float v : value) {
        joiner.add(format.format(v));
      }

      Element prop = document.createElement("div");
      prop.setClassName("box-meta-property");

      String extraClass = extraClasses[i];
      if (!Strings.isNullOrEmpty(extraClass)) {
        prop.getClassList().add(extraClass);
      }

      Element nameDiv = document.createElement("div");
      Element valueDiv = document.createElement("div");

      nameDiv.setClassName("box-meta-field");
      valueDiv.setClassName("box-meta-value");

      nameDiv.setTextContent(field);
      valueDiv.setTextContent(joiner.toString());

      prop.appendChild(nameDiv);
      prop.appendChild(valueDiv);

      container.appendChild(prop);
    }

    return container;
  }

  @Override
  public void onOpen() {
    Locale l = devtools.getLocale();
    NumberFormat format = NumberFormat.getInstance(l);

    PageView target = (PageView) devtools.getTarget();
    ElementRenderObject ero = (ElementRenderObject) target
        .getRenderer()
        .getRenderElement(devtools.getSelectedElement());

    Element boxes = createElementBoxes(format, ero);
    Element meta = createMetadata(format, ero, l);

    Element upperContent = document.createElement("div");
    upperContent.setClassName("upper-content");

    upperContent.appendChild(boxes);
    upperContent.appendChild(meta);

    devtools.getContentEl().appendChild(upperContent);
  }
}
