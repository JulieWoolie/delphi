package net.arcadiusmc.delphiplugin.render;

import net.arcadiusmc.delphi.dom.scss.ComputedStyle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.joml.Vector2f;

public interface ElementContent {

  Display createEntity(World world, Location location);

  void applyContentTo(Display entity, ComputedStyle set);

  Class<? extends Display> getEntityClass();

  void measureContent(Vector2f out, ComputedStyle set);

  boolean isEmpty();

  //void configureInitial(Layer layer, RenderObject element);
}
