package net.arcadiusmc.chimera.parse;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ast.Block;

@Getter @Setter
public class MixinObject {

  private Block body;
  private Scope scope;
}
