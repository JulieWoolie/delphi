package com.juliewoolie.nlayout;

import com.juliewoolie.chimera.PropertySet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

abstract class BoxNode extends TestNode {

  PropertySet styleProperties = new PropertySet();
  final List<TestNode> childNodes = new ObjectArrayList<>();

  public void addChild(TestNode child) {
    child.node.domIndex = childNodes.size();
    childNodes.add(child);

    LayoutBox box = (LayoutBox) this.node;
    box.nodes.add(child.node);
  }

  @Override
  void runTestRecursive() {
    super.runTestRecursive();

    for (TestNode childNode : childNodes) {
      childNode.runTestRecursive();
    }
  }
}
