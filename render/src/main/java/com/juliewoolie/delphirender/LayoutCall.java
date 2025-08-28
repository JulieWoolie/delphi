package com.juliewoolie.delphirender;

import static com.juliewoolie.delphirender.Consts.ITEM_SPRITE_SIZE;
import static com.juliewoolie.delphirender.FullStyle.toBukkitColor;
import static com.juliewoolie.delphirender.FullStyle.toTextColor;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.ItemRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.nlayout.FlexLayoutBox;
import com.juliewoolie.nlayout.FlowLayoutBox;
import com.juliewoolie.nlayout.LayoutBox;
import com.juliewoolie.nlayout.LayoutContext;
import com.juliewoolie.nlayout.LayoutItem;
import com.juliewoolie.nlayout.LayoutNode;
import com.juliewoolie.nlayout.MeasureFunc;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import org.joml.Vector2f;

public class LayoutCall {

  static final MeasureFunc ITEM_MEASURE_FUNC = out -> {
    out.set(ITEM_SPRITE_SIZE);
  };

  static void nlayout(ElementRenderObject root, Vector2f screenSize) {
    RenderToLayoutMapper lookupMap = new RenderToLayoutMapper(100);
    LayoutBox box = createLayoutTree(root, lookupMap);

    // Apply visual style before layout calculation, as layout may change
    // with visual changes, such as bold making text wider
    for (int i = 0; i < lookupMap.length; i++) {
      RenderObject obj = lookupMap.renderObjects[i];
      if (obj instanceof ElementRenderObject ero) {
        applyVisualStyle(ero.computedStyleSet, ero.style);
      }
    }

    LayoutContext ctx = new LayoutContext(screenSize);
    box.reflow(ctx);

    for (int i = 0; i < lookupMap.length; i++) {
      LayoutNode layout = lookupMap.layoutNodes[i];
      RenderObject obj = lookupMap.renderObjects[i];

      obj.size.set(layout.size);
      obj.moveTo(layout.position);
    }
  }

  static void applyVisualStyle(ComputedStyleSet cstyle, FullStyle style) {
    style.textColor = toTextColor(cstyle.color);
    style.backgroundColor = toBukkitColor(cstyle.backgroundColor);
    style.borderColor = toBukkitColor(cstyle.borderColor);
    style.outlineColor = toBukkitColor(cstyle.outlineColor);

    style.textShadowed = cstyle.textShadow;
    style.bold = cstyle.bold;
    style.italic = cstyle.italic;
    style.underlined = cstyle.underlined;
    style.strikethrough = cstyle.strikethrough;
    style.obfuscated = cstyle.obfuscated;
    style.zindex = cstyle.zindex;
  }

  static LayoutBox createLayoutTree(ElementRenderObject object, RenderToLayoutMapper lookupMap) {
    LayoutBox box;
    if (object.computedStyleSet.display == DisplayType.FLEX) {
      box = new FlexLayoutBox(object.style, object.computedStyleSet);
    } else {
      box = new FlowLayoutBox(object.style, object.computedStyleSet);
    }

    box.position.set(object.position);
    box.domIndex = object.domIndex;

    if (object.size.x > 0 || object.size.y > 0) {
      box.size.set(object.size);
    }

    lookupMap.add(box, object);

    for (RenderObject childObject : object.getChildObjects()) {
      LayoutNode node = createLayoutNode(childObject, lookupMap);
      box.getNodes().add(node);
    }

    return box;
  }

  static LayoutNode createLayoutNode(RenderObject object, RenderToLayoutMapper lookupMap) {
    if (object instanceof ElementRenderObject ero) {
      return createLayoutTree(ero, lookupMap);
    }

    LayoutItem item = new LayoutItem();
    item.domIndex = object.domIndex;

    item.position.set(object.position);
    item.size.set(object.size);

    lookupMap.add(item, object);

    switch (object) {
      case MeasureFunc f -> {
        item.measureFunc = f;
      }
      case ItemRenderObject obj -> {
        item.measureFunc = ITEM_MEASURE_FUNC;
      }
      default -> {
        throw new IllegalArgumentException();
      }
    }

    return item;
  }

  private static class RenderToLayoutMapper {
    private LayoutNode[] layoutNodes;
    private RenderObject[] renderObjects;
    private int length = 0;

    public RenderToLayoutMapper(int len) {
      this.layoutNodes = new LayoutNode[len];
      this.renderObjects = new RenderObject[len];
    }

    void add(LayoutNode node, RenderObject object) {
      if (length >= layoutNodes.length) {
        int nlen = layoutNodes.length * 2;
        layoutNodes = ObjectArrays.grow(layoutNodes, nlen);
        renderObjects = ObjectArrays.grow(renderObjects, nlen);
      }

      layoutNodes[length] = node;
      renderObjects[length] = object;

      length++;
    }
  }
}
