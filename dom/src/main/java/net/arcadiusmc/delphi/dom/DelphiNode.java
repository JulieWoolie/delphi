package net.arcadiusmc.delphi.dom;

import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphi.dom.scss.ComputedStyle;
import net.arcadiusmc.delphi.dom.scss.PropertySet;
import net.arcadiusmc.dom.Node;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class DelphiNode implements Node {

  DelphiDocument owningDocument;
  int siblingIndex = -1;
  int depth;
  DelphiElement parent;

  private int flags = 0;

  public final ComputedStyle style;
  public final PropertySet styleSet;

  public DelphiNode(DelphiDocument document) {
    this.owningDocument = document;

    this.style = new ComputedStyle();
    this.styleSet = new PropertySet();
  }

  public boolean hasFlag(NodeFlag flag) {
    return (flags & flag.mask) == flag.mask;
  }

  public void addFlag(NodeFlag flag) {
    this.flags |= flag.mask;
  }

  public void removeFlag(NodeFlag flag) {
    this.flags &= ~flag.mask;
  }

  public void setAdded(boolean added) {
    if (added) {
      addFlag(NodeFlag.ADDED);
    } else {
      removeFlag(NodeFlag.ADDED);
    }
  }

  @Override
  public @Nullable Node nextSibling() {
    return siblingInDir(1);
  }

  @Override
  public @Nullable Node previousSibling() {
    return siblingInDir(-1);
  }

  private Node siblingInDir(int dir) {
    if (parent == null) {
      return null;
    }

    int idx = dir + siblingIndex;
    if (idx < 0) {
      return null;
    }

    List<DelphiNode> list = parent.children;

    if (idx >= list.size()) {
      return null;
    }

    return list.get(idx);
  }

  protected void setDepth(int depth) {
    this.depth = depth;
  }
}
