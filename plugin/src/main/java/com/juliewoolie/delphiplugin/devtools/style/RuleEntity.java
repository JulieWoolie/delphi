package com.juliewoolie.delphiplugin.devtools.style;

import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.delphiplugin.devtools.DocInfoTab;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public record RuleEntity(Rule r) implements StyleEntity {

  @Override
  public @Nullable String getSelector() {
    return r.getSelector();
  }

  @Override
  public String getSource(Locale l) {
    return DocInfoTab.translateSheetSource(l, r.getStylesheet().getSource());
  }

  @Override
  public StylePropertiesReadonly getProperties() {
    return r.getProperties();
  }

  @Override
  public PropertySet getPropertySet() {
    return r.getPropertySet();
  }

  @Override
  public void postUpdateCallback() {

  }

  @Override
  public boolean isModifiable() {
    return (r.getStylesheet().getFlags() & ChimeraStylesheet.FLAG_DEFAULT_STYLE) == 0;
  }

  @Override
  public @Nullable String getSpec() {
    return r.getSpec().toString();
  }
}
