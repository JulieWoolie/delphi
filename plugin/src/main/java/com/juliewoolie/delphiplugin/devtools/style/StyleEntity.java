package com.juliewoolie.delphiplugin.devtools.style;

import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import java.util.Locale;
import javax.annotation.Nullable;

public interface StyleEntity {

  @Nullable
  String getSelector();

  String getSource(Locale l);

  StylePropertiesReadonly getProperties();

  PropertySet getPropertySet();

  void postUpdateCallback();

  boolean isModifiable();

  @Nullable
  String getSpec();
}
