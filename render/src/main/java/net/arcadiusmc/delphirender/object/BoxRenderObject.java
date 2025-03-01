package net.arcadiusmc.delphirender.object;

import static net.arcadiusmc.delphirender.Consts.BLOCK_OFFSET_X;
import static net.arcadiusmc.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_X;
import static net.arcadiusmc.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_Y;

import java.util.Objects;
import net.arcadiusmc.delphirender.Consts;
import net.arcadiusmc.delphirender.RenderSystem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class BoxRenderObject extends SingleEntityRenderObject<TextDisplay> {

  public static final Color NIL_COLOR = Color.fromARGB(0);

  public Color color;

  public BoxRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected TextDisplay spawnEntity(World w, Location l) {
    return w.spawn(l, TextDisplay.class, td -> {
      td.text(Consts.EMPTY_CONTENT);
      td.setTextOpacity(Consts.EMPTY_TEXT_OPACITY);
    });
  }

  @Override
  protected void configure(TextDisplay entity, Transformation trans) {
    entity.setBackgroundColor(Objects.requireNonNullElse(color, NIL_COLOR));

    Vector3f scale = trans.getScale();
    Vector3f offset = trans.getTranslation();

    scale.x = EMPTY_TD_BLOCK_SIZE_X * size.x;
    scale.y = EMPTY_TD_BLOCK_SIZE_Y * size.y;

    offset.x -= visualCenterOffset(scale.x);
  }

  public static float visualCenterOffset(float scaleX) {
    return BLOCK_OFFSET_X * (scaleX / EMPTY_TD_BLOCK_SIZE_X);
  }
}
