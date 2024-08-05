package net.arcadiusmc.delphiplugin.render;

import com.google.common.base.Strings;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class StringContent extends TextContent {

  private final String string;
  private final Component baseText;

  public StringContent(String string) {
    this.string = Strings.nullToEmpty(string);

    if (Strings.isNullOrEmpty(string)) {
      baseText = Component.empty();
    } else {
      baseText = Component.text(string);
    }
  }
}
