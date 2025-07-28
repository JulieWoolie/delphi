package com.juliewoolie.chimera.parse;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.ast.Block;

@Getter @Setter
public class MixinObject {

  private Block body;
  private Scope scope;
}
