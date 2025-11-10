package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.juliewoolie.delphidom.DelphiBodyElement;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.dom.ButtonElement;
import com.juliewoolie.dom.Element;
import java.util.Locale;
import java.util.function.Consumer;

public class ActionsTab extends DevToolTab {

  public ActionsTab(Devtools devtools) {
    super(devtools);
  }

  @Override
  public void onOpen() {
    Locale l = devtools.getLocale();

    RenderSystem renderer = targetView.getRenderer();

    DelphiBodyElement body = targetView.getDocument().getBody();

    Element qa = createSection("delphi.devtools.actions.quickActions", element -> {
      ButtonElement rerender
          = createButton(translateToString(l, "delphi.devtools.actions.rerender"));
      ButtonElement relayout
          = createButton(translateToString(l, "delphi.devtools.actions.relayout"));
      ButtonElement style
          = createButton(translateToString(l, "delphi.devtools.actions.styleUpdate"));
      ButtonElement close
          = createButton(translateToString(l, "delphi.devtools.actions.close"));

      rerender.onClick(event -> {
        if (body == null) {
          return;
        }
        renderer.triggerRedraw(body);
      });
      relayout.onClick(event -> {
        if (body == null) {
          return;
        }
        renderer.triggerRealign(body);
      });
      style.onClick(event -> {
        targetView.getDocument().getStyles().updateFromRoot();
      });
      close.onClick(event -> {
        targetView.close();
      });

      element.appendChild(rerender);
      element.appendChild(relayout);
      element.appendChild(style);
      element.appendChild(close);
    });

    Element container = document.createElement("div");
    container.setClassName("actions-container");
    container.appendChild(qa);

    devtools.getContentEl().appendChild(container);
  }

  private ButtonElement createButton(String transKey) {
    ButtonElement btn = document.createElement("button");
    btn.setClassName("style-page-btn");
    btn.setTextContent(translateToString(devtools.getLocale(), transKey));
    return btn;
  }

  private Element createSection(String titleTransKey, Consumer<Element> factory) {
    Element div = document.createElement("div");
    div.setClassName("actions-box");

    Element title = document.createElement("p");
    title.setClassName("actions-title");
    title.setTextContent(translateToString(devtools.getLocale(), titleTransKey));

    Element container = document.createElement("div");
    container.setClassName("actions-buttons");
    if (factory != null) {
      factory.accept(container);
    }

    div.appendChild(title);
    div.appendChild(container);

    return div;
  }
}
