package net.arcadiusmc.delphidom.system;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;

public class IdSystem implements ObjectModelSystem {

  public final Map<String, Element> lookupMap = new Object2ObjectOpenHashMap<>();

  DelphiDocument document;
  final AttrListener attrListener = new AttrListener();
  final MutateListener mutateListener = new MutateListener();

  @Override
  public void onAttach(DelphiDocument document) {
    this.document = document;

    EventListenerList g = document.getGlobalTarget();
    g.addEventListener(EventTypes.MODIFY_ATTR, attrListener);
    g.addEventListener(EventTypes.APPEND_CHILD, mutateListener);
    g.addEventListener(EventTypes.REMOVE_CHILD, mutateListener);
  }

  @Override
  public void onDetach() {
    EventListenerList g = document.getGlobalTarget();
    g.removeEventListener(EventTypes.MODIFY_ATTR, attrListener);
    g.removeEventListener(EventTypes.APPEND_CHILD, mutateListener);
    g.removeEventListener(EventTypes.REMOVE_CHILD, mutateListener);

    document = null;
    lookupMap.clear();
  }

  void updateId(Element element, String previousId, String newId) {
    if (!Strings.isNullOrEmpty(previousId)) {
      Element referenced = lookupMap.get(previousId);

      if (Objects.equals(referenced, element)) {
        lookupMap.remove(previousId);
      }
    }

    if (Strings.isNullOrEmpty(newId)) {
      return;
    }

    // Do not override existing
    Element existingValue = lookupMap.get(newId);
    if (existingValue != null) {
      return;
    }

    lookupMap.put(newId, element);
  }

  class AttrListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (!event.getKey().equals(Attributes.ID)) {
        return;
      }

      String oldVal = event.getPreviousValue();
      String newVal = event.getNewValue();

      updateId(event.getTarget(), oldVal, newVal);
    }
  }

  class MutateListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Node node = event.getNode();
      assert node != null;

      if (!(node instanceof Element target)) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        updateId(target, null, target.getId());
      } else {
        updateId(target, target.getId(), null);
      }
    }
  }
}
