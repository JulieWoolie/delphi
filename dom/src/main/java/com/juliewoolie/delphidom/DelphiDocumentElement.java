package com.juliewoolie.delphidom;

import com.juliewoolie.dom.BodyElement;
import com.juliewoolie.dom.HeadElement;
import lombok.Getter;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.TagNames;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MutationEvent;
import org.jetbrains.annotations.Nullable;

@Getter
public class DelphiDocumentElement
    extends DelphiElement
    implements com.juliewoolie.dom.DelphiElement
{

  private DelphiBodyElement body;
  private DelphiHeadElement head;

  public DelphiDocumentElement(DelphiDocument document) {
    super(document, TagNames.ROOT);

    ChildListener listener = new ChildListener();
    addEventListener(EventTypes.APPEND_CHILD, listener);
    addEventListener(EventTypes.REMOVE_CHILD, listener);
  }

  @Override
  public HeadElement getHeadElement() {
    return head;
  }

  @Override
  public @Nullable BodyElement getBodyElement() {
    return body;
  }

  class ChildListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Node node = event.getNode();
      if (!(node instanceof Element el)) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        if (el instanceof DelphiHeadElement headerEl) {
          if (head != null) {
            return;
          }
          head = headerEl;
          return;
        }

        if (el instanceof DelphiBodyElement bodyEl) {
          if (body != null) {
            return;
          }

          body = bodyEl;
          body.addFlag(NodeFlag.ROOT);
        }

        return;
      }

      if (el instanceof DelphiHeadElement) {
        head = null;
        return;
      }

      if (el instanceof DelphiBodyElement) {
        if (body != null) {
          body.removeFlag(NodeFlag.ROOT);
        }

        body = null;
      }
    }
  }
}
