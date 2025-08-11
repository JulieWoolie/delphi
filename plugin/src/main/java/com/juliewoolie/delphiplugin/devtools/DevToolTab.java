package com.juliewoolie.delphiplugin.devtools;

import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.dom.Document;

public abstract class DevToolTab {

  protected final Devtools devtools;
  protected final Document document;
  protected final PageView targetView;

  public DevToolTab(Devtools devtools) {
    this.devtools = devtools;
    this.document = devtools.getDocument();
    this.targetView = (PageView) devtools.getTarget();
  }

  public abstract void onOpen();

  public void onClose() {

  }
}
