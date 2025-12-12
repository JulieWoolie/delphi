package com.juliewoolie.delphiplugin;

import com.juliewoolie.delphidom.DelphiSliderElement;
import com.juliewoolie.dom.RenderBounds;
import com.juliewoolie.dom.SliderElement.SliderOrient;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
import org.joml.Vector2f;

public class SliderInputListener implements EventListener.Typed<MouseEvent> {

  @Override
  public void handleEvent(MouseEvent event) {
    if (!(event.getTarget() instanceof DelphiSliderElement slider)) {
      return;
    }
    if (event.getButton() != MouseButton.LEFT) {
      return;
    }

    Vector2f screenPosition = event.getScreenPosition();
    RenderBounds innerArea = slider.getInnerRenderingBounds();

    screenPosition.x -= innerArea.getMinimum().x;
    screenPosition.y -= innerArea.getMinimum().y;

    screenPosition.x /= innerArea.getSize().x;
    screenPosition.y /= innerArea.getSize().y;

    double ratio;

    if (slider.getOrient() == SliderOrient.HORIZONTAL) {
      ratio = screenPosition.x;
    } else {
      ratio = 1.0d - screenPosition.y;
    }

    double min = slider.getMin();
    double max = slider.getMax();

    if (max <= min) {
      return;
    }

    Double step = slider.getStep();

    if (step != null) {
      double dif = max - min;
      double stepRatio = step / dif;

      ratio -= ratio % stepRatio;
    }

    slider.setRatio(ratio, event.getPlayer());
  }
}
