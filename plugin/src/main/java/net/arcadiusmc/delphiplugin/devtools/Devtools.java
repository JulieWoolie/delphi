package net.arcadiusmc.delphiplugin.devtools;

import java.util.Objects;
import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphiplugin.PageInputSystem;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MouseEvent;

@Getter @Setter
public class Devtools {

  private final DocumentView target;
  private Element selectedElement;

  private final Document document;
  private final Element contentEl;

  private DevToolTab tab;

  public Devtools(DocumentView targetView, Document devtoolsDocument) {
    this.target = targetView;
    this.document = devtoolsDocument;

    contentEl = document.getElementById("content");
    Objects.requireNonNull(contentEl, "Null content element");

    registerListeners();
  }

  public void registerListeners() {
    Document targetDoc = target.getDocument();
    EventTarget g = targetDoc.getGlobalTarget();

    // Closing the window that this devtools is attached to
    // should close the devtools too
    targetDoc.addEventListener(EventTypes.DOM_CLOSING, event -> {
      document.getView().close();
    });

    document.addEventListener(EventTypes.DOM_CLOSING, event -> onClose());

    Element navbar = document.getElementById("navbar");
    if (navbar != null) {
      EventListener.Typed<MouseEvent> listener = this::onNavbarClick;
      navbar.addEventListener(EventTypes.CLICK, listener);
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
      }

      case "styles" -> {

      }

      case "boxmodel" -> {

      }

      case "docinfo" -> {

      }

      case null, default -> {
        // No op
      }
    }
  }
}
