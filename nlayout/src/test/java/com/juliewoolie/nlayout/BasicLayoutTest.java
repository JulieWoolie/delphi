package com.juliewoolie.nlayout;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import com.juliewoolie.dom.style.VerticalAlign;
import java.io.IOException;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

public class BasicLayoutTest {

  @Test
  void run() throws IOException {
    Vector2f screen = new Vector2f(3, 2);

    FlowLayoutBox root = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    root.position.y = screen.y;

    root.cstyle.width = ValueOrAuto.valueOf(Primitive.create(100.0f, Unit.VW));
    root.cstyle.height = ValueOrAuto.valueOf(Primitive.create(100.0f, Unit.VH));

    root.cstyle.paddingTop = ValueOrAuto.valueOf(Primitive.create(5, Unit.PX));
    root.cstyle.paddingLeft = ValueOrAuto.valueOf(Primitive.create(5, Unit.PX));
    root.cstyle.paddingBottom = ValueOrAuto.valueOf(Primitive.create(5, Unit.PX));
    root.cstyle.paddingRight = ValueOrAuto.valueOf(Primitive.create(5, Unit.PX));

    root.cstyle.boxSizing = BoxSizing.BORDER_BOX;

    FlowLayoutBox child1 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    child1.cstyle.width = ValueOrAuto.valueOf(Primitive.create(50f, Unit.PERCENT));
    child1.cstyle.height = ValueOrAuto.valueOf(Primitive.create(12, Unit.PX));
    child1.cstyle.verticalAlign = VerticalAlign.SUPER;

    FlowLayoutBox nested = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    nested.cstyle.width = ValueOrAuto.valueOf(Primitive.create(50f, Unit.PERCENT));
    nested.cstyle.height = ValueOrAuto.valueOf(Primitive.create(6, Unit.PX));

    FlowLayoutBox child2 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    child2.cstyle.height = ValueOrAuto.valueOf(Primitive.create(16, Unit.PX));
    child2.cstyle.width = ValueOrAuto.valueOf(Primitive.create(20, Unit.PX));
    child2.cstyle.marginInlineStart = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));

    FlowLayoutBox child3 = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
    child3.cstyle.height = ValueOrAuto.valueOf(Primitive.create(16, Unit.PX));
    child3.cstyle.width = ValueOrAuto.valueOf(Primitive.create(25, Unit.PX));
    child3.cstyle.marginInlineStart = ValueOrAuto.valueOf(Primitive.create(2, Unit.PX));

    root.nodes.add(child1);
    root.nodes.add(child2);
    root.nodes.add(child3);

    LayoutContext ctx = new LayoutContext(screen);
    root.reflow(ctx);

    LayoutPrinter.dumpLayout(root,300, 200, new Vector2f(100), screen);
  }

}
