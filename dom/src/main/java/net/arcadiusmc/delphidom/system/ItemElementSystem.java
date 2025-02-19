package net.arcadiusmc.delphidom.system;

import com.google.common.base.Strings;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphidom.ContentSource;
import net.arcadiusmc.delphidom.DelphiItemElement;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.Attributes;

public class ItemElementSystem extends ParsedDataElementSystem<DelphiItemElement> {

  public ItemElementSystem() {
    super(DelphiItemElement.class);
  }

  @Override
  protected ContentSource getSource(DelphiItemElement element) {
    return element.source;
  }

  @Override
  protected void setSource(DelphiItemElement element, ContentSource source) {
    element.source = source;
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    super.onViewAttach(view);

    for (DelphiItemElement element : elements) {
      if (element.source == ContentSource.SRC_ATTR) {
        loadFromSrc(element, element.getAttribute(Attributes.SOURCE));
      } else if (element.source == ContentSource.TEXT_CONTENT) {
        parseFromContent(element);
      }
    }
  }

  @Override
  protected void onAppend(DelphiItemElement item) {
    super.onAppend(item);

    String src = item.getAttribute(Attributes.SOURCE);
    String text = item.getTextContent();

    if (!Strings.isNullOrEmpty(src)) {
      item.source = ContentSource.SRC_ATTR;
      loadFromSrc(item, src);
    } else if (!Strings.isNullOrEmpty(text)) {
      parseFromContent(item);
    }
  }

  @Override
  protected void loadFromSrc(DelphiItemElement element, String uri) {
    if (view == null) {
      return;
    }

    ViewResources resources = view.getResources();

    resources.loadItemStack(uri)
        .mapError(e -> "Failed to load itemstack from path " + uri + ": " + e.getMessage())
        .ifError(Loggers.getDocumentLogger()::error)
        .ifSuccess(itemStack -> {
          element.setItemStack0(itemStack);
          element.source = ContentSource.SRC_ATTR;
        });
  }

  @Override
  protected void parseFromContent(DelphiItemElement element) {
    String txt = element.getTextContent();
    if (view == null || Strings.isNullOrEmpty(txt)) {
      return;
    }

    ViewResources resources = view.getResources();

    resources.parseItemStack(txt)
        .mapError(e -> "Failed to parse item JSON data: " + e.getMessage())
        .ifError(Loggers.getDocumentLogger()::error)
        .ifSuccess(itemStack -> {
          element.setItemStack0(itemStack);
          element.source = ContentSource.TEXT_CONTENT;
        });
  }
}
