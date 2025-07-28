package com.juliewoolie.delphidom.system;

import com.juliewoolie.delphidom.DelphiCanvasElement;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;

public class CanvasElementSystem extends ElementTrackingSystem<DelphiCanvasElement> {

  final DimensionsListener listener;

  public CanvasElementSystem() {
    super(DelphiCanvasElement.class);
    this.listener = new DimensionsListener();
  }

  @Override
  protected void onAppend(DelphiCanvasElement el) {
    el.addEventListener(EventTypes.MODIFY_ATTR, listener);
  }

  @Override
  protected void onRemove(DelphiCanvasElement el) {
    el.removeEventListener(EventTypes.MODIFY_ATTR, listener);
  }

  class DimensionsListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      String key = event.getKey();
      DelphiCanvasElement target = (DelphiCanvasElement) event.getTarget();

      if (!key.equals(Attributes.WIDTH) && !key.equals(Attributes.HEIGHT)) {
        return;
      }

      int cw = target.canvas.getWidth();
      int ch = target.canvas.getHeight();

      int nw = target.getWidth();
      int nh = target.getHeight();

      if (nw == cw && nh == ch) {
        return;
      }

      target.canvas.setSize(nw, nh);

      if (view != null) {
        view.canvasSizeChanged(target);
      }
    }
  }
}
