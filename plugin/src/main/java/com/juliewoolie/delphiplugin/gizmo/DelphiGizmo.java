package com.juliewoolie.delphiplugin.gizmo;

import com.juliewoolie.delphi.gimbal.Gizmo;
import com.juliewoolie.delphi.gimbal.GizmoAbility;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphirender.object.RenderObject;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector4d;

public class DelphiGizmo implements Gizmo {

  public static final float SHORT_SIZE = 0.05f;
  public static final float MED_SIZE = 0.2f;
  public static final float LONG_SIZE = 0.5f;
  public static final float NEAR_FLAT = 0.0001f;
  public static final float SHORT_OFF = SHORT_SIZE * -0.5f;
  public static final float PART_DIST = SHORT_SIZE;

  public static final byte GS_NONE = 0;
  public static final byte GS_SELECTED = 1;
  public static final byte GS_ACTIVE = 2;

  final Vector3d position = new Vector3d(0);
  private World world;

  private boolean active = false;

  final Transformation baseTransform = RenderObject.newTransform();

  private final EnumSet<GizmoAbility> abilities = EnumSet.allOf(GizmoAbility.class);
  private final Transformation appliedTrans = RenderObject.newTransform();

  private Player player = null;

  private final DelphiPlugin plugin;

  private final GizmoPart[] parts = new GizmoPart[Part.PARTS];
  private GizmoPart hoveredPart;
  private GizmoPart selectedPart;
  private byte gizmoState = GS_NONE;

  private final GizmoHint hint;

  public DelphiGizmo(DelphiPlugin plugin) {
    this.plugin = plugin;
    this.hint = new GizmoHint(this);

    for (int i = 0; i < parts.length; i++) {
      parts[i] = new GizmoPart(this);
    }

    setBlockTypes();
    setupPartSizes();
    updatePartOffsets();
  }

  public void onClick() {
    if (hoveredPart == null) {
      return;
    }

    if (selectedPart == null) {
      selectedPart = hoveredPart;
      selectedPart.setGlowColor(Color.RED);
      gizmoState = GS_ACTIVE;
    } else {
      selectedPart.setGlowColor(null);
      selectedPart = null;
      gizmoState = GS_SELECTED;
    }
  }

  public boolean isSelected() {
    return hoveredPart != null;
  }

  public void onNotHit() {
    if (hoveredPart != null) {
      hoveredPart.setGlowing(false);
      hoveredPart = null;
    }

    gizmoState = GS_NONE;

    hint.kill();
  }

  void onPartHit(Vector3d hitPos, Part part) {
    GizmoPart hitPart = getPart(part);

    if (hoveredPart != null && hoveredPart != hitPart) {
      hoveredPart.setGlowing(false);
      hoveredPart = null;
    }

    hitPart.setGlowing(true);
    hoveredPart = hitPart;
    gizmoState = GS_SELECTED;

    hint.setHint(part);
  }

  void setBlockTypes() {
    getPart(Part.MOVE_X).setBlockType(Material.RED_CONCRETE);
    getPart(Part.MOVE_Y).setBlockType(Material.GREEN_CONCRETE);
    getPart(Part.MOVE_Z).setBlockType(Material.BLUE_CONCRETE);

    getPart(Part.MOVE_XY).setBlockType(Material.BLUE_CONCRETE);
    getPart(Part.MOVE_XZ).setBlockType(Material.GREEN_CONCRETE);
    getPart(Part.MOVE_ZY).setBlockType(Material.RED_CONCRETE);

    getPart(Part.MOVE_GLOBAL).setBlockType(Material.WHITE_CONCRETE);

    getPart(Part.SCALE_X).setBlockType(Material.RED_CONCRETE);
    getPart(Part.SCALE_Y).setBlockType(Material.GREEN_CONCRETE);
    getPart(Part.SCALE_Z).setBlockType(Material.BLUE_CONCRETE);

//    getPart(Part.ROTATE_X).setBlockType(Material.RED_CONCRETE);
//    getPart(Part.ROTATE_Y).setBlockType(Material.GREEN_CONCRETE);
//    getPart(Part.ROTATE_Z).setBlockType(Material.BLUE_CONCRETE);
  }

  void relativizePlayerPosition(Vector3d out, double px, double py, double pz) {
    Matrix4d mat = GizmoManager.transformationToMatrix(baseTransform, position);
    mat.invert();

    Vector4d v4 = new Vector4d();
    v4.x = px;
    v4.y = py;
    v4.z = pz;
    v4.w = 1.0d;

    out.set(mat.transform(v4));
  }

  void updatePartOffsets() {
    Vector3d pOff = new Vector3d();
    if (player != null) {
      relativizePlayerPosition(
          pOff,
          player.getX(),
          player.getY() + player.getEyeHeight(),
          player.getZ()
      );
    }

    GizmoPart gmove = getPart(Part.MOVE_GLOBAL);
    gmove.moveTo(SHORT_OFF, SHORT_OFF, SHORT_OFF);

    GizmoPart xmove = getPart(Part.MOVE_X);
    GizmoPart ymove = getPart(Part.MOVE_Y);
    GizmoPart zmove = getPart(Part.MOVE_Z);

    GizmoPart xzmove = getPart(Part.MOVE_XZ);
    GizmoPart xymove = getPart(Part.MOVE_XY);
    GizmoPart zymove = getPart(Part.MOVE_ZY);

    if (this.abilities.contains(GizmoAbility.MOVE)) {
      xmove.moveTo(movePartOffset(pOff.x, LONG_SIZE), SHORT_OFF, SHORT_OFF);
      ymove.moveTo(SHORT_OFF, movePartOffset(pOff.y, LONG_SIZE), SHORT_OFF);
      zmove.moveTo(SHORT_OFF, SHORT_OFF, movePartOffset(pOff.z, LONG_SIZE));

      xzmove.moveTo(movePartOffset(pOff.x, MED_SIZE), 0, movePartOffset(pOff.z, MED_SIZE));
      xymove.moveTo(movePartOffset(pOff.x, MED_SIZE), movePartOffset(pOff.y, MED_SIZE), 0);
      zymove.moveTo(0, movePartOffset(pOff.y, MED_SIZE), movePartOffset(pOff.z, MED_SIZE));
    } else {
      xmove.moveTo(0, 0, 0);
      ymove.moveTo(0, 0, 0);
      zmove.moveTo(0, 0, 0);

      xzmove.moveTo(0, 0, 0);
      xymove.moveTo(0, 0, 0);
      zymove.moveTo(0, 0, 0);
    }

    GizmoPart xscale = getPart(Part.SCALE_X);
    GizmoPart yscale = getPart(Part.SCALE_Y);
    GizmoPart zscale = getPart(Part.SCALE_Z);

    if (this.abilities.contains(GizmoAbility.SCALE)) {
      float size = MED_SIZE * 0.5f;
      float hs = size * 0.5f;

      xscale.moveTo(scalePartOffset(pOff.x, size), -hs, -hs);
      yscale.moveTo(-hs, scalePartOffset(pOff.y, size), -hs);
      zscale.moveTo(-hs, -hs, scalePartOffset(pOff.z, size));
    } else {
      xscale.moveTo(0, 0, 0);
      yscale.moveTo(0, 0, 0);
      zscale.moveTo(0, 0, 0);
    }
//
//    GimbalPart xrotate = getPart(Part.ROTATE_X);
//    GimbalPart yrotate = getPart(Part.ROTATE_Y);
//    GimbalPart zrotate = getPart(Part.ROTATE_Z);
//
//    if (this.abilities.contains(GimbalAbility.ROTATE)) {
//      xrotate.moveTo(-10000000, 0, 0);
//      yrotate.moveTo(-10000000, 0, 0);
//      zrotate.moveTo(-10000000, 0, 0);
//    } else {
//      xrotate.moveTo(0, 0, 0);
//      yrotate.moveTo(0, 0, 0);
//      zrotate.moveTo(0, 0, 0);
//    }
  }

  static float scalePartOffset(double pc, float size) {
    float off = movePartOffset(pc, LONG_SIZE);
    if (pc < 0) {
      return off - (PART_DIST + size);
    }
    return off + PART_DIST + LONG_SIZE;
  }

  static float movePartOffset(double pc, float s) {
    if (pc < 0) {
      return -((SHORT_SIZE * 0.5f) + PART_DIST + s);
    }
    return (SHORT_SIZE * 0.5f) + PART_DIST;
  }

  void setupPartSizes() {
    GizmoPart gmove = getPart(Part.MOVE_GLOBAL);
    gmove.size.set(SHORT_SIZE);

    GizmoPart xmove = getPart(Part.MOVE_X);
    xmove.size.x = LONG_SIZE;
    xmove.size.y = SHORT_SIZE;
    xmove.size.z = SHORT_SIZE;

    GizmoPart ymove = getPart(Part.MOVE_Y);
    ymove.size.x = SHORT_SIZE;
    ymove.size.y = LONG_SIZE;
    ymove.size.z = SHORT_SIZE;

    GizmoPart zmove = getPart(Part.MOVE_Z);
    zmove.size.x = SHORT_SIZE;
    zmove.size.y = SHORT_SIZE;
    zmove.size.z = LONG_SIZE;

    GizmoPart xymove = getPart(Part.MOVE_XY);
    xymove.size.x = MED_SIZE;
    xymove.size.y = MED_SIZE;
    xymove.size.z = NEAR_FLAT;

    GizmoPart zymove = getPart(Part.MOVE_ZY);
    zymove.size.x = NEAR_FLAT;
    zymove.size.y = MED_SIZE;
    zymove.size.z = MED_SIZE;

    GizmoPart xzmove = getPart(Part.MOVE_XZ);
    xzmove.size.x = MED_SIZE;
    xzmove.size.y = NEAR_FLAT;
    xzmove.size.z = MED_SIZE;

    for (int i = Part.SCALE_X.ordinal(); i <= Part.SCALE_Z.ordinal(); i++) {
      GizmoPart p = parts[i];
      p.size.set(MED_SIZE * 0.5f);
    }
  }

  GizmoPart getPart(Part part) {
    return parts[part.ordinal()];
  }

  @Override
  public @NotNull Vector3d getPosition() {
    return new Vector3d(position);
  }

  @Override
  public void moveTo(double x, double y, double z) throws IllegalArgumentException {
    validateValidCord(x, y, z);

    position.set(x, y, z);

    if (world == null) {
      return;
    }

    updatePartOffsets();

    if (!active) {
      return;
    }

    for (GizmoPart part : parts) {
      part.spawn();
    }
  }

  @Override
  public void moveTo(@NotNull Vector3dc pos) throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(pos, "Null position");
    moveTo(pos.x(), pos.y(), pos.z());
  }

  @Override
  public void moveTo(@NotNull Location location)
      throws IllegalArgumentException, NullPointerException
  {
    Objects.requireNonNull(location, "Null location");
    Objects.requireNonNull(location.getWorld(), "Null world");
    validateValidCord(location.getX(), location.getY(), location.getZ());

    position.x = location.getX();
    position.y = location.getY();
    position.z = location.getZ();

    world = location.getWorld();

    updatePartOffsets();

    if (!active) {
      return;
    }

    for (int i = 0; i < parts.length; i++) {
      parts[i].spawn();
    }
  }

  void validateValidCord(double x, double y, double z) {
    if (Double.isNaN(x)) {
      throw new IllegalArgumentException("X coordinate is NaN");
    }
    if (Double.isNaN(y)) {
      throw new IllegalArgumentException("Y coordinate is NaN");
    }
    if (Double.isNaN(z)) {
      throw new IllegalArgumentException("Z coordinate is NaN");
    }
  }

  @Override
  public @NotNull World getWorld() {
    return world;
  }

  @Override
  public @NotNull Transformation getBaseTransform() {
    return new Transformation(
        this.baseTransform.getTranslation(),
        this.baseTransform.getLeftRotation(),
        this.baseTransform.getScale(),
        this.baseTransform.getRightRotation()
    );
  }

  @Override
  public @NotNull Player getPlayer() {
    return player;
  }

  @Override
  public void setPlayer(@NotNull Player player) throws NullPointerException {
    Objects.requireNonNull(player, "Null player");
    this.player = player;
  }

  @Override
  public void kill() {
    for (int i = 0; i < parts.length; i++) {
      GizmoPart part = parts[i];
      if (part == null) {
        continue;
      }
      part.kill();
    }

    active = false;
    hint.kill();
  }

  @Override
  public void spawn() {
    if (active) {
      return;
    }

    for (int i = 0; i < parts.length; i++) {
      Part p = Part.VALUES[i];
      GizmoPart part = parts[i];

      if (!this.abilities.contains(p.ability)) {
        part.kill();
      } else {
        part.spawn();
      }
    }

    active = true;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setBaseTransform(@Nullable Transformation trans) {
    if (trans == null) {
      identity(this.baseTransform);
    } else {
      copyTransform(trans, this.baseTransform);
    }

    if (!isActive()) {
      return;
    }

    for (GizmoPart part : parts) {
      if (!part.isAlive()) {
        continue;
      }

      part.updateTransform();
    }
  }

  @Override
  public @NotNull Transformation getAppliedTransform() {
    return new Transformation(
        new Vector3f(appliedTrans.getTranslation()),
        new Quaternionf(appliedTrans.getLeftRotation()),
        new Vector3f(appliedTrans.getScale()),
        new Quaternionf(appliedTrans.getRightRotation())
    );
  }

  @Override
  public void resetAppliedTransform() {
    setAppliedTransform(null);
  }

  void identity(Transformation transform) {
    transform.getTranslation().set(0.0f);
    transform.getLeftRotation().identity();
    transform.getScale().set(1.0f);
    transform.getRightRotation().identity();
  }

  void copyTransform(Transformation from, Transformation to) {
    to.getTranslation().set(from.getTranslation());
    to.getLeftRotation().set(from.getLeftRotation());
    to.getScale().set(from.getScale());
    to.getRightRotation().set(from.getRightRotation());
  }

  @Override
  public void setAppliedTransform(@Nullable Transformation transform) {
    if (transform == null) {
      identity(appliedTrans);
      return;
    }

    copyTransform(transform, appliedTrans);
  }

  @Override
  public Set<GizmoAbility> getAbilities() {
    return Collections.unmodifiableSet(abilities);
  }

  @Override
  public void setAbilities(@NotNull Collection<GizmoAbility> abilities)
      throws IllegalArgumentException, NullPointerException
  {
    Objects.requireNonNull(abilities, "Null abilities");
    if (abilities.isEmpty()) {
      throw new IllegalArgumentException("Empty abilities set");
    }

    this.abilities.clear();
    this.abilities.addAll(abilities);

    if (!active) {
      return;
    }

    for (int i = 0; i < parts.length; i++) {
      Part p = Part.VALUES[i];
      GizmoPart part = parts[i];

      if (this.abilities.contains(p.ability)) {
        part.kill();
      } else {
        part.spawn();
      }
    }

    updatePartOffsets();
  }
}
