package net.arcadiusmc.delphidom;

import java.util.Objects;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.InputElement;
import net.arcadiusmc.dom.TagNames;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiInputElement extends DelphiElement implements InputElement {

  private String value;

  public DelphiInputElement(DelphiDocument document) {
    super(document, TagNames.INPUT);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public @Nullable String getValue() {
    return value;
  }

  @Override
  public void setValue(@Nullable String value) {
    changeValue(value, null);
  }

  @Override
  public void setValue(@Nullable String value, @NotNull Player player) {
    Objects.requireNonNull(player, "Null player");
    changeValue(value, player);
  }

  private void changeValue(String value, Player player) {
    String previousValue = this.value;
    this.value = value;

    document.valueChanged(this, value, previousValue, player);
  }

  @Override
  public @NotNull String getPlaceholder() {
    String attr = getAttribute(Attributes.PLACEHOLDER);
    if (attr == null) {
      return DEFAULT_PLACEHOLDER;
    }
    return attr;
  }

  @Override
  public void setPlaceholder(@Nullable String placeholder) {
    setAttribute(Attributes.PLACEHOLDER, placeholder);
  }
}
