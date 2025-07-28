package com.juliewoolie.delphidom.event;

import lombok.Getter;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.Text;
import com.juliewoolie.dom.event.TextChangeEvent;

@Getter
public class TextChange extends EventImpl implements TextChangeEvent {

  private Text textNode;

  public TextChange(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(DelphiElement target, boolean bubbles, boolean cancellable, Text node) {
    super.initEvent(target, bubbles, cancellable);
    this.textNode = node;
  }
}
