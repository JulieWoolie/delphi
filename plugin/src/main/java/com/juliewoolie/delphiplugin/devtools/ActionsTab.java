package com.juliewoolie.delphiplugin.devtools;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.TagNames;

public class ActionsTab extends DevToolTab {

  public ActionsTab(Devtools devtools) {
    super(devtools);
  }

  @Override
  public void onOpen() {
    Document document = devtools.getDocument();
    Element outp = devtools.getContentEl();

    DelphiNode body = (DelphiNode) devtools.getTarget().getDocument().getBody();

    Element forceRealign = document.createElement(TagNames.BUTTON);
    forceRealign.onClick(event -> {
      PageView target = (PageView) devtools.getTarget();
      target.getRenderer().triggerRealign(body);
    });
    forceRealign.setTextContent("Force re-layout");

    Element forceReRender = document.createElement(TagNames.BUTTON);
    forceReRender.onClick(event -> {
      PageView target = (PageView) devtools.getTarget();
      target.getRenderer().triggerRedraw(body);
    });
    forceReRender.setTextContent("Force re-render");

    Element forceStyleRecalculation = document.createElement(TagNames.BUTTON);
    forceStyleRecalculation.onClick(event -> {
      DelphiDocument targetDoc = (DelphiDocument) devtools.getTarget().getDocument();
      targetDoc.getStyles().updateDomStyle(targetDoc.getDocumentElement());
    });
    forceStyleRecalculation.setTextContent("Force style update");

    outp.appendChild(forceRealign);
    outp.appendChild(forceReRender);
    outp.appendChild(forceStyleRecalculation);
  }
}
