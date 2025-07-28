package com.juliewoolie.delphidom.system;

import com.google.common.base.Strings;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphi.resource.ViewResources.ComponentFormat;
import com.juliewoolie.delphidom.ChatElement;
import com.juliewoolie.delphidom.ContentSource;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.dom.Attributes;

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
