package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.ComponentElement;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.Visitor;
import net.kyori.adventure.text.Component;

public class ChatElement extends DelphiElement implements ComponentElement {

  private Component component;

  public ChatElement(DelphiDocument document) {
    super(document, TagNames.COMPONENT);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
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
