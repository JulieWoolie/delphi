package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Visitor;
import net.kyori.adventure.text.Component;

public class ChatNode extends DelphiNode implements ComponentNode {

  private Component component;

  public ChatNode(DelphiDocument document) {
    super(document);
  }

  @Override
  public Component getContent() {
    return component;
  }

  @Override
  public void setContent(Component content) {
    this.component = content;

    if (document.getView() != null) {
      document.getView().contentChanged(this);
    }
  }

  @Override
  public void enterVisitor(Visitor visitor) {
    visitor.enterComponent(this);
  }

  @Override
  public void exitVisitor(Visitor visitor) {
    visitor.exitComponent(this);
  }
}
