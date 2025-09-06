package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translate;
import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.Properties;
import com.juliewoolie.chimera.Property;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraException;
import com.juliewoolie.chimera.parse.ChimeraParser;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.parse.Interpreter;
import com.juliewoolie.chimera.parse.Location;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.dom.ButtonElement;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.InputEvent;
import com.juliewoolie.dom.event.MouseEvent;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.slf4j.event.Level;

public class StylesTab extends DevToolTab {

  int page = 0;

  public StylesTab(Devtools devtools) {
    super(devtools);
  }

  List<Rule> getRules() {
    Element selected = devtools.getSelectedElement();
    DelphiDocument doc = (DelphiDocument) devtools.getTarget().getDocument();

    if (selected == null) {
      return null;
    }

    return doc.getStyles()
        .getRules()
        .stream()
        .filter(rule -> rule.getSelectorObject().test(selected))
        .sorted(Comparator.reverseOrder())
        .toList();
  }

  static boolean isDefaultRule(Rule rule) {
    return (rule.getStylesheet().getFlags() & ChimeraStylesheet.FLAG_DEFAULT_STYLE) != 0;
  }

  @Override
  public void onOpen() {
    List<Rule> rules = getRules();
    Locale l = devtools.getLocale();

    Element out = devtools.getContentEl();

    Element titleDiv = createTitlePart(page, rules);
    out.appendChild(titleDiv);

    if (rules == null) {
      Element p = document.createElement("p");
      p.setClassName("style-props-title");
      p.setTextContent(
          translateToString(l, "delphi.devtools.styles.noneSelected.body")
      );

      out.appendChild(p);
      return;
    }

    Rule rule = rules.get(page);
    createProperties(out, rule, l);
    createMetadata(out, rule, l);
    createForceState(out, l);
  }

  void createForceState(Element out, Locale l) {
    Element div = document.createElement("div");
    div.setClassName("style-forcestate");

    Element title = document.createElement("p");
    title.setClassName("style-forcestate-title");
    title.setTextContent(translateToString(l, "delphi.devtools.styles.forcestate.title"));

    Element container = document.createElement("div");
    container.setClassName("style-forcestate-flex");

    String[] labels = {":hover", ":active"};
    NodeFlag[] flags = {NodeFlag.HOVERED, NodeFlag.CLICKED};

    DelphiElement selected = (DelphiElement) devtools.getSelectedElement();

    for (int i = 0; i < labels.length; i++) {
      String label = labels[i];
      NodeFlag flag = flags[i];

      Element span = document.createElement("span");

      Element checkbox = document.createElement("span");
      checkbox.setClassName("forcestate-checkbox");
      checkbox.onClick(new ToggleForceState(selected, flag, devtools));

      if ((selected.forcedFlags & flag.mask) == flag.mask) {
        checkbox.setAttribute("checked", "yes");
      }

      Element labelSpan = document.createElement("span");
      labelSpan.setTextContent(label);
      labelSpan.setClassName("forcestate-text");

      span.appendChild(checkbox);
      span.appendChild(labelSpan);

      container.appendChild(span);
    }

    div.appendChild(title);
    div.appendChild(container);

    out.appendChild(div);
  }

  void createMetadata(Element out, Rule rule, Locale l) {
    String[] fields = {
        "delphi.devtools.styles.meta.spec",
        "delphi.devtools.styles.meta.properties"
    };
    String[] values = {
        rule.getSpec().toString(),
        String.valueOf(rule.getPropertySet().size())
    };

    Element div = document.createElement("div");
    div.setClassName("style-meta");

    Element title = document.createElement("p");
    title.setClassName("style-meta-title");
    title.setTextContent(translateToString(l, "delphi.devtools.styles.meta.title"));
    div.appendChild(title);

    for (int i = 0; i < fields.length; i++) {
      String field = translateToString(l, fields[i]);
      String value = values[i];

      Element fieldDiv = document.createElement("div");
      Element fieldName = document.createElement("span");
      Element fieldValue = document.createElement("span");

      fieldName.setClassName("style-meta-field");
      fieldName.setTextContent(field);
      fieldValue.setTextContent(value);

      fieldDiv.appendChild(fieldName);
      fieldDiv.appendChild(fieldValue);

      div.appendChild(fieldDiv);
    }

    out.appendChild(div);
  }

  void createProperties(Element out, Rule rule, Locale l) {
    Element p = document.createElement("p");
    p.setClassName("style-props-title");
    p.setTextContent(
        translateToString(l, "delphi.devtools.styles.properties.title")
    );

    StylePropertiesReadonly properties = rule.getProperties();
    List<String> propertyNames = properties.getProperties().stream().sorted().toList();

    Element div = document.createElement("div");
    div.setClassName("style-props");

    String namePrompt = translateToString(l, "delphi.devtools.styles.properties.prompt");
    String valuePrompt = translateToString(l, "delphi.devtools.styles.value.prompt");

    DelphiDocument targetDoc = (DelphiDocument) devtools.getTarget().getDocument();
    PropertySet propertySet = rule.getPropertySet();

    for (String propertyName : propertyNames) {
      String propertyValue = properties.getPropertyValue(propertyName);

      Element line = document.createElement("div");
      line.setClassName("style-props-line");

      Element checkbox = document.createElement("span");
      InputElement nameSpan = (InputElement) document.createElement("input");
      InputElement valueSpan = (InputElement) document.createElement("input");

      checkbox.setClassName("style-prop-checkbox");
      nameSpan.setClassName("style-prop");
      valueSpan.setClassName("style-value");

      if (isDefaultRule(rule)) {
        nameSpan.setDisabled(true);
        valueSpan.setDisabled(true);
      } else {
        PropertyRef ref = new PropertyRef();
        ref.propertyName = propertyName;
        ref.property = Properties.getByKey(propertyName);

        nameSpan.onInput(new RenameProperty(ref, propertySet, targetDoc));
        valueSpan.onInput(new ChangeValue(ref, propertySet, targetDoc));

        nameSpan.setPrompt(namePrompt);
        valueSpan.setPrompt(valuePrompt);

        Value<Object> val = propertySet.get(ref.property);
        if (!val.isEnabled()) {
          line.setAttribute("disabled", "yes");
        }

        checkbox.onClick(new ToggleDisabledState(ref, propertySet, line, targetDoc));
      }

      nameSpan.setValue(propertyName);
      valueSpan.setValue(propertyValue);

      line.appendChild(checkbox);
      line.appendChild(nameSpan);
      line.appendChild(valueSpan);

      div.appendChild(line);
    }

    if (!isDefaultRule(rule)) {
      Element plusDiv = document.createElement("div");
      Element plusSpan = document.createElement("span");
      plusSpan.setTextContent("+");
      plusSpan.setClassName("add-extra-style");
      plusSpan.onClick(new AddProperty(propertySet, targetDoc, devtools));
      plusDiv.appendChild(plusSpan);
      div.appendChild(plusDiv);
    }

    out.appendChild(p);
    out.appendChild(div);
  }

  Element createTitlePart(int idx, List<Rule> rules) {
    Element div = document.createElement("div");
    div.setClassName("style-page-select");

    ButtonElement backwardBtn = (ButtonElement) document.createElement("button");
    ButtonElement forwardBtn = (ButtonElement) document.createElement("button");

    Locale l = devtools.getLocale();
    backwardBtn.setTextContent(
        translateToString(l, "delphi.devtools.styles.pageButton.backward")
    );
    forwardBtn.setTextContent(
        translateToString(l, "delphi.devtools.styles.pageButton.forward")
    );

    Element pageNumber = document.createElement("span");
    Element pgNumDiv = document.createElement("div");
    pageNumber.appendChild(pgNumDiv);
    pageNumber.setClassName("pagenum");

    if (rules != null) {
      pgNumDiv.setTextContent((idx + 1) + "/" + rules.size());
    } else {
      pgNumDiv.setTextContent("0/0");
    }

    backwardBtn.setClassName("style-page-btn");
    forwardBtn.setClassName("style-page-btn");

    if (rules == null) {
      backwardBtn.setEnabled(false);
      forwardBtn.setEnabled(false);
    } else {
      if (idx == 0) {
        backwardBtn.setEnabled(false);
      }
      if (idx == (rules.size() - 1)) {
        forwardBtn.setEnabled(false);
      }
    }

    if (forwardBtn.isEnabled()) {
      forwardBtn.onClick(new MovePage(page + 1, this));
    }
    if (backwardBtn.isEnabled()) {
      backwardBtn.onClick(new MovePage(page - 1, this));
    }

    Element styleName = document.createElement("span");
    styleName.setClassName("style-name");

    if (rules == null) {
      Element i = document.createElement("i");

      i.setTextContent(
          translateToString(l, "delphi.devtools.styles.noneSelected")
      );

      styleName.appendChild(i);
    } else {
      Rule r = rules.get(idx);
      String source = r.getStylesheet().getSource();

      styleName.setTextContent(r.getSelector());

      Element i = document.createElement("i");
      i.setClassName("style-src");
      i.setTextContent("(" + DocInfoTab.translateSheetSource(l, source) + ")");

      styleName.appendChild(i);
    }

    div.appendChild(backwardBtn);
    div.appendChild(pageNumber);
    div.appendChild(forwardBtn);
    div.appendChild(styleName);

    return div;
  }

  static Value<Object> tryParsePropertyValue(String pval, Property<Object> prop, Player player) {
    ChimeraParser parser = new ChimeraParser(pval);
    CompilerErrors errors = parser.getErrors();
    errors.setListener(error -> {
      if (error.getLevel() == Level.ERROR) {
        throw new ChimeraException(error);
      }

      Loggers.getDocumentLogger()
          .atLevel(error.getLevel())
          .log(error.getFormattedError());
    });

    try {
      Expression expr = parser.expr();
      Location l = expr.getStart();

      Interpreter interp = new Interpreter(parser.createContext(), Scope.createTopLevel());
      Object value = expr.visit(interp);

      Value<Object> objectValue
          = Chimera.coerceCssValue(pval, false, prop, value, errors, l);

      return objectValue;
    } catch (ChimeraException exc) {
      Loggers.getDocumentLogger().error(exc.getError().getFormattedError());

      if (player != null) {
        player.sendMessage(
            Component.translatable(
                "delphi.devtools.styles.error.parseError",
                NamedTextColor.RED,
                Component.text(exc.getError().getMessage())
            )
        );
      }

      return null;
    }
  }

  record MovePage(int newpage, StylesTab tab) implements EventListener.Typed<MouseEvent> {
    @Override
    public void handleEvent(MouseEvent event) {
      tab.page = newpage;
      tab.devtools.rerender();
    }
  }

  record ToggleForceState(DelphiElement target, NodeFlag flag, Devtools devtools)
      implements EventListener.Typed<MouseEvent>
  {

    @Override
    public void handleEvent(MouseEvent event) {
      if ((target.forcedFlags & flag.mask) == flag.mask) {
        target.undoStateForce(flag);
      } else {
        target.forceState(flag);
      }

      target.getDocument().getStyles().updateFromRoot();
      devtools.rerender();
    }
  }

  static class PropertyRef {
    Property<Object> property;
    String propertyName;
  }

  static class ToggleDisabledState implements EventListener.Typed<MouseEvent> {

    private final PropertyRef ref;
    private final PropertySet set;
    private final Element lineElement;
    private final DelphiDocument targetDoc;

    public ToggleDisabledState(
        PropertyRef ref,
        PropertySet set,
        Element lineElement,
        DelphiDocument targetDoc
    ) {
      this.ref = ref;
      this.set = set;
      this.lineElement = lineElement;
      this.targetDoc = targetDoc;
    }

    @Override
    public void handleEvent(MouseEvent event) {
      Value<Object> val = set.get(ref.property);
      if (val == null) {
        return;
      }

      boolean newState = !val.isEnabled();
      val.setEnabled(newState);

      targetDoc.getStyles().updateFromRoot();

      if (newState) {
        lineElement.removeAttribute("disabled");
      } else {
        lineElement.setAttribute("disabled", "yes");
      }
    }
  }

  @RequiredArgsConstructor
  static class ChangeValue implements EventListener.Typed<InputEvent> {

    final PropertyRef current;
    final PropertySet set;
    final DelphiDocument targetDoc;

    @Override
    public void handleEvent(InputEvent event) {
      String newValue = event.getNewValue();
      Player player = event.getPlayer();

      Value<Object> parsed = tryParsePropertyValue(newValue, current.property, player);
      if (parsed == null) {
        event.preventDefault();
        return;
      }

      set.setValue(current.property, parsed);
      targetDoc.getStyles().updateFromRoot();
    }
  }

  @RequiredArgsConstructor
  static class RenameProperty implements EventListener.Typed<InputEvent> {

    final PropertyRef current;
    final PropertySet set;
    final DelphiDocument targetDoc;

    @Override
    public void handleEvent(InputEvent event) {
      String newName = event.getNewValue();

      if (Strings.isNullOrEmpty(newName)) {
        event.preventDefault();
        event.stopPropagation();

        set.remove(current.property);

        // Remove the <div> the <input> is inside of from it's parent
        // aka, remove the line
        Element parent = event.getTarget().getParent();
        parent.getParent().removeChild(parent);

        targetDoc.getStyles().updateFromRoot();
        return;
      }

      Property<Object> property = Properties.getByKey(newName);
      Player player = event.getPlayer();

      if (property == null) {
        if (player != null) {
          player.sendMessage(
              Component.translatable(
                  "delphi.devtools.styles.error.unknownProperty",
                  NamedTextColor.RED,
                  Component.text(newName)
              )
          );
        }

        event.preventDefault();
        return;
      }

      if (current.property == property) {
        return;
      }

      if (property.getType() != current.property.getType()) {
        if (player != null) {
          player.sendMessage(
              Component.translatable(
                  "delphi.devtools.styles.error.incompatibleProperty",
                  NamedTextColor.RED,
                  Component.text(current.propertyName),
                  Component.text(newName)
              )
          );
        }

        event.preventDefault();
        return;
      }

      Value<Object> oldPropValue = set.get(current.property);
      set.remove(current.property);
      set.setValue(property, oldPropValue);

      targetDoc.getStyles().updateFromRoot();

      current.property = property;
      current.propertyName = newName;
    }
  }

  record AddProperty(PropertySet set, DelphiDocument targetDoc, Devtools devtools)
      implements EventListener.Typed<MouseEvent>
  {

    @Override
    public void handleEvent(MouseEvent event) {
      Player player = event.getPlayer();
      Dialog dialog = createExtraPropertyDialog(player);

      player.showDialog(dialog);
    }

    void handlePropertyInput(Player p, String name, String value) {
      Property<Object> property = Properties.getByKey(name);
      if (property == null) {
        p.sendMessage(
            Component.translatable(
                "delphi.devtools.styles.error.unknownProperty",
                NamedTextColor.RED,
                Component.text(name)
            )
        );
        return;
      }

      Value<Object> parsed = tryParsePropertyValue(value, property, p);
      if (parsed == null) {
        return;
      }

      set.setValue(property, parsed);
      targetDoc.getStyles().updateFromRoot();

      devtools.rerender();
    }

    Dialog createExtraPropertyDialog(Player p) {
      return Dialog.create(f -> {
        DialogRegistryEntry.Builder builder = f.empty();

        builder.type(
            DialogType.confirmation(
                ActionButton.builder(translate(p, "delphi.input.yes"))
                    .action(
                        DialogAction.customClick(
                            (response, audience) -> {
                              String propertyName = response.getText("property_name");
                              String propertyValue = response.getText("property_value");
                              handlePropertyInput(p, propertyName, propertyValue);
                            },
                            Options.builder().build()
                        )
                    )
                    .build(),
                ActionButton.builder(translate(p, "delphi.input.no"))
                    .build()
            )
        );

        builder.base(
            DialogBase.builder(translate(p, "delphi.devtools.styles.addProperty.title"))
                .inputs(
                    List.of(
                        DialogInput.text(
                                "property_name",
                                translate(p, "delphi.devtools.styles.properties.prompt")
                            )
                            .maxLength(Integer.MAX_VALUE)
                            .initial("")
                            .width(300)
                            .build(),

                        DialogInput.text(
                                "property_value",
                                translate(p, "delphi.devtools.styles.value.prompt")
                            )
                            .maxLength(Integer.MAX_VALUE)
                            .initial("")
                            .width(300)
                            .build()
                    )
                )
                .canCloseWithEscape(true)
                .build()
        );
      });
    }
  }
}
