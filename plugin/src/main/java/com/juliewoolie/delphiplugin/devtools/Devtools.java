package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import java.util.Locale;
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

  private Locale locale;

  public Devtools(DocumentView targetView, Document devtoolsDocument, Locale locale) {
    this.target = targetView;
    this.document = devtoolsDocument;
    this.locale = locale;

    contentEl = document.getElementById("content");
    Objects.requireNonNull(contentEl, "Null content element");

    this.rerender = event -> this.rerender();
    this.onTargetClose = event -> document.getView().close();

    translateTabs();
    registerListeners();
  }

  public void rerender() {
    contentEl.clearChildren();
    tab.onOpen();
  }

  public void translateTabs() {
    translateTab("elements", "delphi.devtools.tab.elements");
    translateTab("styles", "delphi.devtools.tab.styles");
    translateTab("box", "delphi.devtools.tab.box");
    translateTab("act", "delphi.devtools.tab.act");
    translateTab("meta", "delphi.devtools.tab.meta");
  }

  private void translateTab(String selector, String transKey) {
    Element element = document.querySelector("[nav=\"" + selector + "\"]");
    if (element == null) {
      return;
    }

    String txt = translateToString(locale, transKey);
    element.setTextContent(txt);
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
    g.addEventListener(EventTypes.CONTENT_CHANGED, rerender);

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
    tab.onOpen();
  }

  private void onClose() {
    Document targetDoc = target.getDocument();
    EventTarget g = targetDoc.getGlobalTarget();

    g.removeEventListener(EventTypes.MODIFY_ATTR, rerender);
    g.removeEventListener(EventTypes.APPEND_CHILD, rerender);
    g.removeEventListener(EventTypes.REMOVE_CHILD, rerender);

    targetDoc.removeEventListener(EventTypes.DOM_CLOSING, onTargetClose);

    tab.onClose();
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

      case "elements" -> {
        switchTo(new ElementTreeTab(this));
        setActive(target);
      }

      case "styles" -> {
        switchTo(new StylesTab(this));
        setActive(target);
      }

      case "box" -> {

      }

      case "act" -> {
        switchTo(new ActionsTab(this));
        setActive(target);
      }

      case "meta" -> {
        switchTo(new DocInfoTab(this));
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
