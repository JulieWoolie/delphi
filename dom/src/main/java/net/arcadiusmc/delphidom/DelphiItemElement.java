package net.arcadiusmc.delphidom;

import com.google.common.base.Strings;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.ItemElement;
import net.arcadiusmc.dom.Options;
import net.arcadiusmc.dom.TagNames;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DelphiItemElement extends DelphiElement implements ItemElement {

  private ItemStack item;

  @Getter
  private DelphiElement itemTooltip;

  public DelphiItemElement(DelphiDocument document) {
    super(document, TagNames.ITEM);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public boolean getTooltipHidden() {
    return Attributes.boolAttribute(getAttribute(Attributes.ITEM_TOOLTIP_HIDE), false);
  }

  @Override
  public void setTooltipHidden(boolean tooltipShown) {
    setAttribute(Attributes.ITEM_TOOLTIP_HIDE, String.valueOf(tooltipShown));
  }

  @Override
  public boolean getAdvancedTooltip() {
    String parseValue;

    parseValue = getAttribute(Attributes.ADVANCED_ITEM_TOOLTIPS);
    if (Strings.isNullOrEmpty(parseValue)) {
      parseValue = document.getOption(Options.ADVANCED_ITEM_TOOLTIPS);
    }

    return Attributes.boolAttribute(parseValue, false);
  }

  @Override
  public void setAdvancedTooltip(boolean advancedTooltip) {
    setAttribute(Attributes.ADVANCED_ITEM_TOOLTIPS, String.valueOf(advancedTooltip));
  }

  @Override
  public @Nullable DelphiNode getTooltip() {
    if (getTooltipHidden() || itemTooltip == null) {
      return super.getTooltip();
    }

    return itemTooltip;
  }

  @Override
  public @Nullable ItemStack getItemStack() {
    return item;
  }

  @Override
  public void setItemStack(@Nullable ItemStack stack) {
    this.item = stack;
    updateTooltip();

    if (document.getView() != null) {
      document.getView().contentChanged(this);
    }
  }

  private void updateTooltip() {
    if (document.getView() != null && itemTooltip != null) {
      document.getView().removeRenderElement(itemTooltip);
    }

    if (item == null || item.getType().isAir() || item.getAmount() < 1) {
      itemTooltip = null;
      return;
    }

    DelphiElement container = document.createElement(TagNames.ITEM_TOOLTIP);

    List<Component> components = new ArrayList<>(
        item.computeTooltipLines(TooltipContext.create(getAdvancedTooltip(), false), null)
    );

    if (!components.isEmpty()) {
      Component name = components.removeFirst();

      DelphiElement el = document.createElement(TagNames.ITEM_TOOLTIP_NAME);
      el.appendChild(document.createComponent(name));

      container.appendChild(el);
    }

    for (Component component : components) {
      if (Objects.equals(component, Component.empty())) {
        component = Component.space();
      }

      DelphiElement line = document.createElement(TagNames.ITEM_TOOLTIP_LINE);
      line.appendChild(document.createComponent(component));

      container.appendChild(line);
    }

    container.setDepth(getDepth() + 1);
    document.styles.updateStyles(container);

    this.itemTooltip = container;
  }
}
