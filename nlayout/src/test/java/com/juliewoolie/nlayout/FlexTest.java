package com.juliewoolie.nlayout;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.dom.style.AlignItems;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.FlexWrap;
import com.juliewoolie.dom.style.JustifyContent;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import com.juliewoolie.dom.style.VerticalAlign;
import java.io.IOException;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

public class FlexTest {

  @Test
  void run() throws IOException {
    Vector2f screen = new Vector2f(3, 2);

    FlowLayoutBox root = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    root.position.y = screen.y;

    root.cstyle.width = ValueOrAuto.valueOf(Primitive.create(100.0f, Unit.VW));
    root.cstyle.height = ValueOrAuto.valueOf(Primitive.create(100.0f, Unit.VH));

    root.cstyle.paddingTop = ValueOrAuto.valueOf(Primitive.create(1.25f, Unit.PX));
    root.cstyle.paddingLeft = ValueOrAuto.valueOf(Primitive.create(1.25f, Unit.PX));
    root.cstyle.paddingBottom = ValueOrAuto.valueOf(Primitive.create(1.25f, Unit.PX));
    root.cstyle.paddingRight = ValueOrAuto.valueOf(Primitive.create(1.25f, Unit.PX));
    root.cstyle.verticalAlign = VerticalAlign.TOP;

    root.cstyle.boxSizing = BoxSizing.BORDER_BOX;

    FlexLayoutBox child1 = new FlexLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    child1.cstyle.width = ValueOrAuto.valueOf(Primitive.create(100f, Unit.PERCENT));
//    child1.cstyle.maxHeight = ValueOrAuto.valueOf(Primitive.create(45f, Unit.PERCENT));
    child1.cstyle.borderTop = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));
    child1.cstyle.borderLeft = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));
    child1.cstyle.borderBottom = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));
    child1.cstyle.borderRight = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));
    child1.cstyle.boxSizing = BoxSizing.BORDER_BOX;
    child1.cstyle.justifyContent = JustifyContent.FLEX_START;
    child1.cstyle.alignItems = AlignItems.STRETCH;
    child1.cstyle.columnGap = ValueOrAuto.valueOf(Primitive.create(3, Unit.PX));
    child1.cstyle.rowGap = ValueOrAuto.valueOf(Primitive.create(5, Unit.PX));
//    child1.cstyle.flexDirection = FlexDirection.COLUMN;
    child1.cstyle.flexWrap = FlexWrap.WRAP;

    FlowLayoutBox nested1 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    nested1.cstyle.width = ValueOrAuto.valueOf(Primitive.create(27f, Unit.PX));
    nested1.cstyle.height = ValueOrAuto.valueOf(Primitive.create(6f, Unit.PX));
    nested1.cstyle.shrink = 0;
    nested1.cstyle.grow = 1;
    nested1.cstyle.order = 3;
    nested1.domIndex = 2;

    FlowLayoutBox nested2 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    nested2.cstyle.height = ValueOrAuto.valueOf(Primitive.create(16, Unit.PX));
    nested2.cstyle.width = ValueOrAuto.valueOf(Primitive.create(80, Unit.PX));
    nested2.cstyle.grow = 1;
    nested2.domIndex = 1;

    FlowLayoutBox nested3 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    nested3.cstyle.height = ValueOrAuto.valueOf(Primitive.create(8, Unit.PX));
    nested3.cstyle.width = ValueOrAuto.valueOf(Primitive.create(25, Unit.PX));
    nested3.cstyle.grow = 1;
    nested3.domIndex = 2;

    FlowLayoutBox superNested1 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    superNested1.cstyle.height = ValueOrAuto.valueOf(Primitive.create(4, Unit.PX));
    superNested1.cstyle.width = ValueOrAuto.valueOf(Primitive.create(50, Unit.PERCENT));

//    nested1.nodes.add(superNested1);

    child1.nodes.add(nested1);
    child1.nodes.add(nested2);
    child1.nodes.add(nested3);
    root.nodes.add(child1);

    LayoutContext ctx = new LayoutContext(screen);
    root.reflow(ctx);

    LayoutPrinter.dumpLayout(root,300, 200, new Vector2f(100), screen);

    Vector2f innerSize = new Vector2f();
    child1.getInnerSize(innerSize);

//    assertEquals(nested1.size.x * 0.5f, superNested1.size.x);
  }
}
