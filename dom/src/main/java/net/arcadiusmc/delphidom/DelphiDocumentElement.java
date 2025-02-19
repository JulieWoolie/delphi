package net.arcadiusmc.delphidom;

import lombok.Getter;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.NodeFlag;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;

@Getter
public class DelphiDocumentElement extends DelphiElement implements Element {

  private DelphiBodyElement body;
  private DelphiHeaderElement header;

  public DelphiDocumentElement(DelphiDocument document) {
    super(document, TagNames.ROOT);

    ChildListener listener = new ChildListener();
    addEventListener(EventTypes.APPEND_CHILD, listener);
    addEventListener(EventTypes.REMOVE_CHILD, listener);
  }

  class ChildListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Node node = event.getNode();
      if (!(node instanceof Element el)) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        if (el instanceof DelphiHeaderElement headerEl) {
          if (header != null) {
            return;
          }
          header = headerEl;
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

      if (el instanceof DelphiHeaderElement) {
        header = null;
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
