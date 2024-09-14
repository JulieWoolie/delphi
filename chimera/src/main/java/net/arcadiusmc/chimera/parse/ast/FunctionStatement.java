package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FunctionStatement extends Statement {

  private Identifier functionName;
  private final List<FuncParameterStatement> parameters = new ArrayList<>();
  private Block body;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return null;
  }


  @Getter @Setter
  public static class FuncParameterStatement extends Statement {

    private Identifier name;
    private boolean varargs;
    private Expression defaultValue;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return null;
    }
  }
}
