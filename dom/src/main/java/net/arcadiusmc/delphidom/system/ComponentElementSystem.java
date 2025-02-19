package net.arcadiusmc.delphidom.system;

import com.google.common.base.Strings;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.resource.ViewResources.ComponentFormat;
import net.arcadiusmc.delphidom.ChatElement;
import net.arcadiusmc.delphidom.ContentSource;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.Attributes;

public class ComponentElementSystem extends ParsedDataElementSystem<ChatElement> {

  public ComponentElementSystem() {
    super(ChatElement.class);
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    super.onViewAttach(view);

    for (ChatElement element : elements) {
      if (element.source == ContentSource.SRC_ATTR) {
        loadFromSrc(element, element.getAttribute(Attributes.SOURCE));
      } else if (element.source == ContentSource.TEXT_CONTENT) {
        parseFromContent(element);
      }
    }
  }

  @Override
  protected void onAppend(ChatElement element) {
    super.onAppend(element);

    String src = element.getAttribute(Attributes.SOURCE);
    String text = element.getTextContent();

    if (!Strings.isNullOrEmpty(src)) {
      element.source = ContentSource.SRC_ATTR;
      loadFromSrc(element, src);
    } else if (!Strings.isNullOrEmpty(text)) {
      parseFromContent(element);
    }
  }

  @Override
  protected void parseFromContent(ChatElement element) {
    if (view == null) {
      return;
    }

    ViewResources resources = view.getResources();
    String source = element.getTextContent();

    resources.parseComponent(source, getFormat(element))
        .mapError(e -> "Failed to parse component data: " + e.getMessage())
        .ifError(Loggers.getDocumentLogger()::error)
        .ifSuccess(component -> {
          element.setContent(component);
          element.source = ContentSource.TEXT_CONTENT;
        });
  }

  @Override
  protected void loadFromSrc(ChatElement element, String uri) {
    if (view == null) {
      return;
    }

    ViewResources resources = view.getResources();

    resources.loadComponent(uri, getFormat(element))
        .mapError(e -> "Failed to load component from " + uri + ": " + e.getMessage())
        .ifError(Loggers.getDocumentLogger()::error)
        .ifSuccess(component -> {
          element.setContent(component);
          element.source = ContentSource.SRC_ATTR;
        });
  }

  @Override
  protected ContentSource getSource(ChatElement element) {
    return element.source;
  }

  @Override
  protected void setSource(ChatElement element, ContentSource source) {
    element.source = source;
  }

  public ComponentFormat getFormat(ChatElement element) {
    String attr = element.getAttribute(Attributes.TYPE);
    return switch (attr) {
      case "minimessage" -> ComponentFormat.MINIMESSAGE;
      case null, default -> ComponentFormat.JSON;
    };
  }
}
