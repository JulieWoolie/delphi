package com.juliewoolie.delphidom.system;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphidom.ContentSource;
import com.juliewoolie.delphidom.DelphiStyleElement;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.dom.Element;

public class StyleElementSystem extends ParsedDataElementSystem<DelphiStyleElement> {

  public StyleElementSystem() {
    super(DelphiStyleElement.class);
  }

  @Override
  protected ContentSource getSource(DelphiStyleElement element) {
    return element.source;
  }

  @Override
  protected void setSource(DelphiStyleElement element, ContentSource source) {
    element.source = source;
  }

  @Override
  protected boolean filterContainer(Element element) {
    return isInHeader(document, element);
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    super.onViewAttach(view);

    for (DelphiStyleElement styleElement : elements) {
      if (styleElement.source != ContentSource.SRC_ATTR) {
        continue;
      }

      loadFromSrc(styleElement, styleElement.getSource());
    }
  }

  @Override
  protected void onAppend(DelphiStyleElement style) {
    super.onAppend(style);

    if (style.stylesheet != null) {
      document.getStyles().addStylesheet(style.stylesheet);
      return;
    }

    String src = style.getSource();
    String text = style.getTextContent();

    if (!Strings.isNullOrEmpty(src)) {
      style.source = ContentSource.SRC_ATTR;
      loadFromSrc(style, src);
    } else if (!Strings.isNullOrEmpty(text)) {
      parseFromContent(style);
    }
  }

  @Override
  protected void onRemove(DelphiStyleElement style) {
    super.onRemove(style);

    if (style.stylesheet != null) {
      document.getStyles().removeStylesheet(style.stylesheet);
    }
  }

  @Override
  protected void loadFromSrc(DelphiStyleElement element, String uri) {
    if (view == null) {
      return;
    }

    ViewResources resources = view.getResources();

    resources.loadStylesheet(uri)
        .mapError(e -> "Failed to load stylesheet from path " + uri + ": " + e.getMessage())
        .ifError(s -> Loggers.getDocumentLogger().error(s))
        .ifSuccess(stylesheet1 -> {
          ChimeraStylesheet c = ((ChimeraStylesheet) stylesheet1);
          document.getStyles().replaceStylesheet(element.stylesheet, c);
          element.stylesheet = c;
          element.source = ContentSource.SRC_ATTR;
        });
  }

  @Override
  protected void parseFromContent(DelphiStyleElement element) {
    String txtContent = element.getTextContent();
    if (Strings.isNullOrEmpty(txtContent)) {
      return;
    }

    ChimeraStylesheet old = element.stylesheet;
    element.stylesheet = Chimera.parseSheet(new StringBuffer(txtContent), "<style #text>");
    element.stylesheet.setSource("inline");
    document.getStyles().replaceStylesheet(old, element.stylesheet);
  }

  @Override
  protected void onUnset(DelphiStyleElement target) {
    if (target.stylesheet == null) {
      return;
    }

    document.getStyles().removeStylesheet(target.stylesheet);
    target.stylesheet = null;
  }
}
