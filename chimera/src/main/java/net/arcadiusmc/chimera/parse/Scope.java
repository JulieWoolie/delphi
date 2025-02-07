package net.arcadiusmc.chimera.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.function.ScssFunction;
import net.arcadiusmc.chimera.function.ScssFunctions;
import net.arcadiusmc.delphi.util.Nothing;

@Getter @Setter
public class Scope {

  private final Scope parent;

  private Map<String, Object> variableMap = new HashMap<>();

  private final Map<String, ScssFunction> functionMap = new HashMap<>();
  private final Map<String, Scope> namespaces = new HashMap<>();

  private PropertySet propertyOutput;
  private Object controlFlowValue;
  private ControlFlow controlFlow;

  private ChimeraSheetBuilder sheetBuilder;

  public Scope(Scope parent) {
    this.parent = parent;
  }

  public Scope() {
    this(null);
  }

  public static Scope createTopLevel() {
    Scope scope = new Scope();

    scope.putFunction("rgb", ScssFunctions.RGB);
    scope.putFunction("rgba", ScssFunctions.RGB);
    scope.putFunction("hsl", ScssFunctions.HSL);
    scope.putFunction("hsla", ScssFunctions.HSL);
    scope.putFunction("lighten", ScssFunctions.BRIGHTEN);
    scope.putFunction("darken", ScssFunctions.DARKEN);

    scope.putFunction("sqrt", ScssFunctions.SQRT);
    scope.putFunction("sin", ScssFunctions.SIN);
    scope.putFunction("cos", ScssFunctions.COS);
    scope.putFunction("tan", ScssFunctions.TAN);
    scope.putFunction("sign", ScssFunctions.SIGN);
    scope.putFunction("exp", ScssFunctions.EXP);
    scope.putFunction("atan", ScssFunctions.ATAN);
    scope.putFunction("asin", ScssFunctions.ASIN);
    scope.putFunction("acos", ScssFunctions.ACOS);
    scope.putFunction("abs", ScssFunctions.ABS);

    scope.putFunction("max", ScssFunctions.MAX);
    scope.putFunction("min", ScssFunctions.MIN);
    scope.putFunction("atan2", ScssFunctions.ATAN2);
    scope.putFunction("clamp", ScssFunctions.CLAMP);

    scope.putFunction("get-property", ScssFunctions.GET_PROPERTY);
    scope.putFunction("set-property", ScssFunctions.SET_PROPERTY);

    return scope;
  }

  public Scope pushFrame() {
    return new Scope(this);
  }

  public boolean controlFlowBroken() {
    return controlFlow != null;
  }

  public void clearReturnValue() {
    this.controlFlow = null;
    this.controlFlowValue = null;
  }

  public void setReturnValue(Object value) {
    this.controlFlowValue = Objects.requireNonNullElse(value, Nothing.INSTANCE);
    this.controlFlow = ControlFlow.RETURN;
  }

  public void putFunction(String functionName, ScssFunction function) {
    Objects.requireNonNull(functionName, "Null name");
    Objects.requireNonNull(functionName, "Null function");

    functionMap.put(functionName, function);
  }

  public Object getVariable(String variableName) {
    Object o = variableMap.get(variableName);
    if (o != null || parent == null) {
      return o;
    }
    return parent.getVariable(variableName);
  }

  public void putVariable(String variableName, Object value) {
    Objects.requireNonNull(variableName, "Null variable name");
    variableMap.put(variableName, value);
  }

  public ScssFunction getFunction(String functionName) {
    ScssFunction o = functionMap.get(functionName);
    if (o != null || parent == null) {
      return o;
    }
    return parent.getFunction(functionName);
  }

  public void putNamespace(String key, Scope scope) {
    Objects.requireNonNull(key, "Null key");
    this.namespaces.put(key, scope);
  }

  public Scope getNamespaced(String namespace) {
    return namespaces.get(namespace);
  }
}
