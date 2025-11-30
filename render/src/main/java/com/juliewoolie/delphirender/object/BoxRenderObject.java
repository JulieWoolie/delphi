package com.juliewoolie.delphirender.object;

import static com.juliewoolie.delphirender.Consts.BLOCK_OFFSET_X;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_X;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_Y;
import static com.juliewoolie.delphirender.Consts.MICRO_LAYER_DEPTH;

import com.juliewoolie.delphirender.Consts;
import com.juliewoolie.delphirender.RenderSystem;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.VoxelShape;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class BoxRenderObject extends SingleEntityRenderObject<Display> {

  public static final Color NIL_COLOR = Color.fromARGB(0);
  public static final float BLOCK_Z_SCALE = MICRO_LAYER_DEPTH * 0.5f;

  public Color color;
  public BlockData blockData;

  private final Vector3d blockSize = new Vector3d(0.0f);

  public BoxRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected Display spawnEntity(World w, Location l) {
    if (blockData != null) {
      return w.spawn(l, BlockDisplay.class);
    }

    return w.spawn(l, TextDisplay.class, td -> {
      td.text(Consts.EMPTY_CONTENT);
      td.setTextOpacity(Consts.EMPTY_TEXT_OPACITY);
    });
  }

  @Override
  protected void configure(Display entity, Transformation trans) {
    Vector3f scale = trans.getScale();
    Vector3f offset = trans.getTranslation();

    if (entity instanceof TextDisplay td) {
      td.setBackgroundColor(Objects.requireNonNullElse(color, NIL_COLOR));

      scale.x = EMPTY_TD_BLOCK_SIZE_X * size.x;
      scale.y = EMPTY_TD_BLOCK_SIZE_Y * size.y;

      offset.x -= visualCenterOffset(scale.x);

      return;
    }

    BlockDisplay bd = (BlockDisplay) entity;
    bd.setBlock(blockData);

    scale.z = 0.0f;

    VoxelShape shape = blockData.getCollisionShape(entity.getLocation());
    boundsFromShape(shape);

    scale.x = (float) (size.x / blockSize.x);
    scale.y = (float) (size.y / blockSize.y);
    scale.z = BLOCK_Z_SCALE;

    offset.x = 0.0f;
  }

  void boundsFromShape(VoxelShape shape) {
    Collection<BoundingBox> boundingBoxes = shape.getBoundingBoxes();

    BoundingBox bbox = new BoundingBox();
    Iterator<BoundingBox> it = boundingBoxes.iterator();
    boolean first = true;

    while (it.hasNext()) {
      BoundingBox n = it.next();

      if (first) {
        bbox.copy(n);
        first = false;
        continue;
      }

      bbox.union(n);
    }

    blockSize.x = bbox.getWidthX();
    blockSize.y = bbox.getHeight();
    blockSize.z = bbox.getWidthZ();
  }

  /**
   * <a href="https://juliewoolie.com/delphi/element-rendering/#51-translation">https://juliewoolie.com/delphi/element-rendering/#51-translation</a>
   * @param scaleX
   * @return
   */
  public static float visualCenterOffset(float scaleX) {
    return BLOCK_OFFSET_X * (scaleX / EMPTY_TD_BLOCK_SIZE_X);
  }
}
