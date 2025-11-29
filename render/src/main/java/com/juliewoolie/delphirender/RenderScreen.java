package com.juliewoolie.delphirender;

import com.juliewoolie.delphi.Screen;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

public interface RenderScreen extends Screen {
  Quaternionf getLeftRotation();

  Quaternionf getRightRotation();

  Vector2d getScreenScale();

  Vector3f getScale();

  void getDimensions(Vector2f out);

  void appendInfo(StringBuilder builder, int indent);
}
