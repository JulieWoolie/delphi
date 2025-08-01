package com.juliewoolie.delphidom;

import java.util.Objects;
import lombok.Getter;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.TextNode;
import com.juliewoolie.dom.Visitor;
import org.jetbrains.annotations.Nullable;

public class Text extends DelphiNode implements TextNode {

  @Getter
  private String textContent;

  public Text(DelphiDocument document) {
    super(document);
  }

  @Override
  public void setTextContent(@Nullable String textContent) {
    if (Objects.equals(this.textContent, textContent)) {
      return;
    }

    this.textContent = textContent;

    if (hasFlag(NodeFlag.ADDED) && document.getView() != null) {
      document.contentChanged(this);
    }
  }

  @Override
  public String toString() {
    return "#text";
  }

  @Override
  public void enterVisitor(Visitor visitor) {
    visitor.enterText(this);
  }

  @Override
  public void exitVisitor(Visitor visitor) {
    visitor.exitText(this);
  }
}
