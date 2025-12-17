package com.juliewoolie.delphiplugin.gimbal;

import com.juliewoolie.delphi.gimbal.GizmoAbility;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.NotNull;

enum Part implements Translatable {

  MOVE_X ("delphi.gimbal.hint.moveX", GizmoAbility.MOVE),
  MOVE_Y ("delphi.gimbal.hint.moveY", GizmoAbility.MOVE),
  MOVE_Z ("delphi.gimbal.hint.moveZ", GizmoAbility.MOVE),

  MOVE_XZ ("delphi.gimbal.hint.moveXZ", GizmoAbility.MOVE),
  MOVE_XY ("delphi.gimbal.hint.moveXY", GizmoAbility.MOVE),
  MOVE_ZY ("delphi.gimbal.hint.moveZY", GizmoAbility.MOVE),

  MOVE_GLOBAL ("delphi.gimbal.hint.moveGlobal", GizmoAbility.MOVE),

  SCALE_X ("delphi.gimbal.hint.scaleX", GizmoAbility.SCALE),
  SCALE_Y ("delphi.gimbal.hint.scaleY", GizmoAbility.SCALE),
  SCALE_Z ("delphi.gimbal.hint.scaleZ", GizmoAbility.SCALE),
//
//  ROTATE_X ("delphi.gimbal.hint.rotateX", GimbalAbility.ROTATE),
//  ROTATE_Y ("delphi.gimbal.hint.rotateY", GimbalAbility.ROTATE),
//  ROTATE_Z ("delphi.gimbal.hint.rotateZ", GimbalAbility.ROTATE),
  ;

  static final Part[] VALUES = values();
  static final int PARTS = VALUES.length;

  private final String hintKey;
  final GizmoAbility ability;

  Part(String hint, GizmoAbility ability) {
    this.hintKey = hint;
    this.ability = ability;
  }

  @Override
  public @NotNull String translationKey() {
    return hintKey;
  }
}
