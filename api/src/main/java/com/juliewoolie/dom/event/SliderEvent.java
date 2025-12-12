package com.juliewoolie.dom.event;

import com.juliewoolie.dom.SliderElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SliderEvent extends Event {

  Double getNewValue();

  Double getOldValue();

  @Override
  SliderElement getTarget();

  @Nullable
  Player getPlayer();
}
