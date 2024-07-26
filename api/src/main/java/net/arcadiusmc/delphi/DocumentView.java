package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.resource.PagePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.dom.Document;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

public interface DocumentView {

  Document getDocument();

  Screen getScreen();

  Vector2f getCursorScreenPosition();

  Vector3f getCursorWorldPosition();

  PagePath getPath();

  ViewResources getResources();

  Player getPlayer();
}
