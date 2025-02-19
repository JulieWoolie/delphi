package net.arcadiusmc.delphidom.system;

import com.google.common.base.Strings;
import net.arcadiusmc.chimera.selector.AttributeOperation;
import net.arcadiusmc.chimera.selector.AttributeSelector;
import net.arcadiusmc.chimera.selector.SelectorList;
import net.arcadiusmc.chimera.selector.SelectorList.ListType;
import net.arcadiusmc.chimera.selector.TagNameSelector;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiHeaderElement;
import net.arcadiusmc.delphidom.DelphiOptionElement;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;

public class OptionElementSystem extends ElementTrackingSystem<DelphiOptionElement> {

  private final OptionAttrListener attrListener = new OptionAttrListener();
  private final OptionSetListener docOptionListener = new OptionSetListener();

  public OptionElementSystem() {
    super(DelphiOptionElement.class);
  }

  @Override
  protected boolean filterContainer(Element element) {
    return isInHeader(document, element);
  }

  @Override
  public void onAttach(DelphiDocument document) {
    super.onAttach(document);

    EventListenerList globalTarget = document.getGlobalTarget();
    globalTarget.addEventListener(EventTypes.MODIFY_OPTION, docOptionListener);
  }

  @Override
  public void onDetach() {
    EventListenerList globalTarget = document.getGlobalTarget();
    globalTarget.removeEventListener(EventTypes.MODIFY_OPTION, docOptionListener);

    super.onDetach();
  }

  @Override
  protected void onAppend(DelphiOptionElement opt) {
    opt.addEventListener(EventTypes.MODIFY_ATTR, attrListener);
    setOptionValue(opt, opt.getValue());
  }

  @Override
  protected void onRemove(DelphiOptionElement opt) {
    opt.removeEventListener(EventTypes.MODIFY_ATTR, attrListener);
    setOptionValue(opt, null);
  }

  @Override
  protected void onDetachElement(DelphiOptionElement opt) {
    opt.removeEventListener(EventTypes.MODIFY_ATTR, attrListener);
  }

  void setOptionValue(DelphiOptionElement opt, String val) {
    if (opt.suppressingUpdates) {
      return;
    }

    String name = opt.getName();
    if (Strings.isNullOrEmpty(name)) {
      return;
    }

    document.setOption(name, val);
  }

  class OptionAttrListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      DelphiOptionElement opt = (DelphiOptionElement) event.getTarget();
      assert opt != null;

      if (opt.suppressingUpdates) {
        return;
      }

      String attr = event.getKey();
      AttributeAction action = event.getAction();

      if (!attr.equals(Attributes.NAME) && !attr.equals(Attributes.VALUE)) {
        return;
      }

      if (action == AttributeAction.ADD || action == AttributeAction.SET) {
        String name = opt.getName();
        String value = opt.getValue();

        if (Strings.isNullOrEmpty(name)) {
          return;
        }

        try {
          opt.suppressingUpdates = true;
          document.setOption(name, value);
        } finally {
          opt.suppressingUpdates = false;
        }

        return;
      }

      // action == remove
      String name;

      if (attr.equals(Attributes.NAME)) {
        name = event.getPreviousValue();
      } else {
        name = opt.getAttribute(Attributes.NAME);
      }

      if (Strings.isNullOrEmpty(name) || opt.suppressingUpdates) {
        return;
      }

      try {
        opt.suppressingUpdates = true;
        document.setOption(name, null);
      } finally {
        opt.suppressingUpdates = false;
      }
    }
  }

  class OptionSetListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      DelphiHeaderElement header = document.getHeader();
      if (header == null) {
        return;
      }

      String key = event.getKey();
      String value = event.getNewValue();

      SelectorList selectorList = new SelectorList(2);
      TagNameSelector optNameSelector = new TagNameSelector(TagNames.OPTION);
      AttributeSelector nameAttrSelector = new AttributeSelector(
          Attributes.NAME,
          AttributeOperation.EQUALS,
          key
      );

      selectorList.add(optNameSelector);
      selectorList.add(nameAttrSelector);
      selectorList.setType(ListType.AND);

      DelphiOptionElement found = (DelphiOptionElement) header.matchFirst(header, selectorList);

      if (Strings.isNullOrEmpty(value)) {
        if (found == null || found.suppressingUpdates) {
          return;
        }

        header.removeChild(found);
        return;
      }

      if (found == null) {
        found = (DelphiOptionElement) document.createElement(TagNames.OPTION);
        found.setAttribute(Attributes.NAME, key);
        found.suppressingUpdates = true;

        header.appendChild(found);
      } else {
        if (found.suppressingUpdates) {
          return;
        }

        found.suppressingUpdates = true;
      }

      found.setAttribute(Attributes.VALUE, value);

      found.suppressingUpdates = false;
    }
  }
}
