package com.juliewoolie.delphirender;

import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.dom.TooltipBehaviour;

public class FloatingWindow {

  static final byte FWT_REGULAR = 0;
  static final byte FWT_TOOLTIP = 1;

  DelphiNode domNode;
  RenderObject ro;

  byte type = FWT_REGULAR;

  FloatingWindow parent;
  FloatingWindow child;

  boolean held = false;
  long appearDelay = -1;
  TooltipBehaviour toolTipbehaviour = null;
}
