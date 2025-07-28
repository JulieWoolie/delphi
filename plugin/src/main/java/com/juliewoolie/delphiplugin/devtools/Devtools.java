package com.juliewoolie.delphiplugin.devtools;

import java.util.Objects;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.delphiplugin.PageInputSystem;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTarget;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MouseEvent;

@Getter @Setter
public class Devtools {

  private final DocumentView target;
  private Element selectedElement;

  private final Document document;
  private final Element contentEl;

  private DevToolTab tab;

  private final EventListener rerender;
  private final EventListener onTargetClose;

  public Devtools(DocumentView targetView, Document devtoolsDocument) {
    this.target = targetView;
    this.document = devtoolsDocument;

    contentEl = document.getElementById("content");
    Objects.requireNonNull(contentEl, "Null content element");

    this.rerender = event -> {
      contentEl.clearChildren();
      tab.onOpen(this);
    };
    this.onTargetClose = event -> {
      document.getView().close();
    };

    registerListeners();
  }

  public void registerListeners() {
    Document targetDoc = target.getDocument();
    EventTarget g = targetDoc.getGlobalTarget();

    // Closing the window that this devtools is attached to
    // should close the devtools too
    targetDoc.addEventListener(EventTypes.DOM_CLOSING, onTargetClose);
    document.addEventListener(EventTypes.DOM_CLOSING, event -> onClose());

    g.addEventListener(EventTypes.MODIFY_ATTR, rerender);
    g.addEventListener(EventTypes.APPEND_CHILD, rerender);
    g.addEventListener(EventTypes.REMOVE_CHILD, rerender);

    Element navbar = document.getElementById("navbar");
    if (navbar != null) {
      EventListener.Typed<MouseEvent> listener = this::onNavbarClick;
      navbar.addEventListener(EventTypes.CLICK, listener);

      Element domNav = navbar.querySelector("[nav=\"dom\"]");
      if (domNav != null) {
        domNav.setAttribute("active", "true");
      }
    }
  }

  public void switchTo(DevToolTab tab) {
    Objects.requireNonNull(tab, "Null tab");

    if (this.tab == tab) {
      return;
    }

    this.tab = tab;
    contentEl.clearChildren();
    tab.onOpen(this);
  }

  private void onClose() {
    Document targetDoc = target.getDocument();
    EventTarget g = targetDoc.getGlobalTarget();

    g.removeEventListener(EventTypes.MODIFY_ATTR, rerender);
    g.removeEventListener(EventTypes.APPEND_CHILD, rerender);
    g.removeEventListener(EventTypes.REMOVE_CHILD, rerender);

    targetDoc.removeEventListener(EventTypes.DOM_CLOSING, onTargetClose);

    tab.onClose(this);
  }

  private void onNavbarClick(MouseEvent event) {
    Element target = event.getTarget();
    assert target != null;

    String nav = target.getAttribute("nav");
    if (Strings.isNullOrEmpty(nav)) {
      return;
    }

    event.getPlayer().playSound(PageInputSystem.CLICK_SOUND);

    switch(nav) {
      case "close" -> {
        event.preventDefault();
        event.stopPropagation();
        event.getDocument().getView().close();
      }

      case "dom" -> {
        switchTo(Tabs.INSPECT_ELEMENT);
        setActive(target);
      }

      case "styles" -> {

      }

      case "boxmodel" -> {

      }

      case "actions" -> {
        switchTo(Tabs.ACTIONS);
        setActive(target);
      }

      case "docinfo" -> {
        switchTo(Tabs.DOC_INFO);
        setActive(target);
      }

      case null, default -> {
        // No op
      }
    }
  }

  private void setActive(Element el) {
    Element parent = el.getParent();
    for (Node child : parent.getChildren()) {
      if (!(child instanceof Element element)) {
        continue;
      }

      if (Objects.equals(el, element)) {
        element.setAttribute("active", "true");
      } else {
        element.setAttribute("active", null);
      }
    }
  }
}
