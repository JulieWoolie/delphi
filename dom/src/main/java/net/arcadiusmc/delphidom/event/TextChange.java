package net.arcadiusmc.delphidom.event;

import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.Text;
import net.arcadiusmc.dom.event.TextChangeEvent;

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
