package net.arcadiusmc.delphi;

import org.joml.Vector3f;

public interface Screen {

  float getWidth();

  float getHeight();

  Vector3f getNormal();

  Vector3f getCenter();
}
