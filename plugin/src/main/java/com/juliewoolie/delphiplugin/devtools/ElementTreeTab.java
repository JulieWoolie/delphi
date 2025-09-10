package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translate;
import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.google.common.base.Strings;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.dom.ButtonElement;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.TextNode;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.InputEvent;
import com.juliewoolie.dom.event.MouseEvent;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.event.ClickCallback.Options;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

public class ElementTreeTab extends DevToolTab {

  static final int LINES_PER_PAGE = 23;

  private static final Logger LOGGER = Loggers.getLogger();

  static final String SPAN = "span";
  static final String INPUT = "input";

  static final String CROSS = "❌";
  static final String PLUS = "+";

  int page = 0;

  public ElementTreeTab(Devtools devtools) {
    super(devtools);
  }

  @Override
  public void onOpen() {
    Element content = devtools.getContentEl();
    Element targetRoot = devtools.getTarget().getDocument().getDocumentElement();

    DomBuilder builder = new DomBuilder();
    createElementLines(targetRoot, document, builder);
    builder.linebreak();

    int maxPages = builder.lineContainer.size() / LINES_PER_PAGE
        + (builder.lineContainer.size() % LINES_PER_PAGE == 0 ? 0 : 1);

    if (page > maxPages) {
      page = maxPages;
    }

    int startIdx = page * LINES_PER_PAGE;
    int endIdx = Math.min(startIdx + LINES_PER_PAGE, builder.lineContainer.size());

    Element head = createHeader(maxPages);
    content.appendChild(head);

    for (Element element : builder.lineContainer.subList(startIdx, endIdx)) {
      content.appendChild(element);
    }
  }

  Element createHeader(int pageCount) {
    Element div = document.createElement("div");
    div.setClassName("element-tree-page-select");

    ButtonElement backwardBtn = (ButtonElement) document.createElement("button");
    ButtonElement forwardBtn = (ButtonElement) document.createElement("button");

    backwardBtn.setClassName("style-page-btn");
    forwardBtn.setClassName("style-page-btn");

    backwardBtn.onClick(new MovePage(this, -1, pageCount));
    forwardBtn.onClick(new MovePage(this, 1, pageCount));

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
    pgNumDiv.setTextContent(String.format("%s / %s", (page + 1), pageCount));

    if (page == 0) {
      backwardBtn.setEnabled(false);
    }
    if ((page + 1) >= pageCount) {
      forwardBtn.setEnabled(false);
    }

    div.appendChild(backwardBtn);
    div.appendChild(pageNumber);
    div.appendChild(forwardBtn);

    return div;
  }

  Element createCross(Document document) {
    Element element = document.createElement(SPAN);
    element.setTextContent(CROSS);
    element.setClassName("cross");
    return element;
  }

  void createElementLines(Node node, Document document, DomBuilder builder) {
    HighlightElement highlight = new HighlightElement(devtools, node);

    if (node instanceof TextNode txt) {
      String noWhiteSpace = StringUtils.deleteWhitespace(txt.getTextContent());
      if (Strings.isNullOrEmpty(noWhiteSpace)) {
        return;
      }

      Element removeNodeCross = createCross(document);
      removeNodeCross.onClick(new RemoveNode(node));
      builder.append(removeNodeCross);

      String[] contentArr = txt.getTextContent().split("\\n+");

      for (int i = 0; i < contentArr.length; i++) {
        String content = contentArr[i];
        Element txtNode = document.createElement(SPAN);
        txtNode.setTextContent(content);
        txtNode.setClassName("xml-text");
        txtNode.onMouseEnter(highlight);
        builder.append(txtNode);

        if (i != contentArr.length - 1) {
          builder.linebreak();
        }
      }

      return;
    }

    Element removeNodeCross = createCross(document);
    removeNodeCross.onClick(new RemoveNode(node));
    builder.append(removeNodeCross);

    Element element = (Element) node;

    SelectElement selectListener = new SelectElement(devtools, element);

    String prefix = "<" + element.getTagName();
    Element prefixEl = document.createElement(SPAN);
    prefixEl.setTextContent(prefix);
    prefixEl.setClassName("xml-tag");
    prefixEl.onClick(selectListener);
    prefixEl.onMouseEnter(highlight);
    builder.append(prefixEl);

    if (Objects.equals(devtools.getSelectedElement(), element)) {
      builder.setLineSelected();
    }

    Set<Entry<String, String>> attrs = element.getAttributeEntries();

    for (Entry<String, String> attr : attrs) {
      InputElement attrName = (InputElement) document.createElement(INPUT);
      Element attrSep = document.createElement(SPAN);
      InputElement attrValue = (InputElement) document.createElement(INPUT);
      Element attrSuffix = document.createElement(SPAN);

      String attrKey = attr.getKey();

      attrName.setValue(attrKey);
      attrSep.setTextContent("=\"");
      attrValue.setValue(attr.getValue());
      attrSuffix.setTextContent("\"");

      attrName.setClassName("xml-attr-name");
      attrSep.setClassName("xml-attr-sep");
      attrValue.setClassName("xml-attr-value");
      attrSuffix.setClassName("xml-attr-sep");

      attrName.onInput(new RenameAttr(element, attrKey));
      attrValue.onInput(new ChangeAttrValue(element, attrKey));

      Element removeAttrCross = createCross(document);
      removeAttrCross.getClassList().add("attr-remove-cross");
      removeAttrCross.onClick(new RemoveAttr(element, attrKey));

      builder.append(removeAttrCross);
      builder.append(attrName);

      if (!Strings.isNullOrEmpty(attr.getValue())) {
        builder.append(attrSep);
        builder.append(attrValue);
        builder.append(attrSuffix);
      }
    }

    Element addAttrBtn = document.createElement(SPAN);
    addAttrBtn.setTextContent(PLUS);
    addAttrBtn.setClassName("add-attr");
    addAttrBtn.onClick(new AddAttr(element));
    builder.append(addAttrBtn);

    if (!element.canHaveChildren() || !element.hasChildren()) {
      Element suffix = document.createElement(SPAN);
      suffix.setTextContent("/>");
      suffix.setClassName("xml-end-tag");
      suffix.onClick(selectListener);
      suffix.onMouseEnter(highlight);
      builder.append(suffix);
      return;
    }

    Element suffix = document.createElement(SPAN);
    suffix.setTextContent(">");
    suffix.setClassName("xml-end-tag");
    suffix.onClick(selectListener);
    suffix.onMouseEnter(highlight);
    builder.append(suffix);

    boolean inlineChildren = inlineChildren(element);

    if (!inlineChildren) {
      builder.indent++;
      builder.linebreak();
    }

    for (Node child : element.getChildren()) {
      createElementLines(child, document, builder);

      if (!inlineChildren) {
        builder.linebreak();
      }
    }

    if (!inlineChildren) {
      builder.indent--;
    }

    Element endTag = document.createElement(SPAN);
    endTag.setTextContent("</" + element.getTagName() + ">");
    endTag.setClassName("xml-end-tag");
    endTag.onClick(selectListener);
    endTag.onMouseEnter(highlight);

    builder.append(endTag);
  }

  boolean inlineChildren(Element el) {
    int descendantCount = countDescendants(el);
    if (descendantCount > 1) {
      return false;
    }

    if (!el.hasChildren()) {
      return true;
    }

    Node child = el.firstChild();
    if (!(child instanceof TextNode txt)) {
      return false;
    }

    String content = txt.getTextContent();
    if (Strings.isNullOrEmpty(content)) {
      return true;
    }

    return !(content.contains("\n") || content.contains("\r"));
  }

  int countDescendants(Element el) {
    int desc = el.getChildCount();

    for (Node child : el.getChildren()) {
      if (!(child instanceof Element e)) {
        continue;
      }

      desc += countDescendants(e);
    }

    return desc;
  }

  record MovePage(ElementTreeTab tab, int dir, int max) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      int npage = tab.page + dir;
      if (npage < 0 || npage >= max) {
        return;
      }
      tab.page = npage;
      tab.devtools.rerender();
    }
  }

  record HighlightElement(Devtools devtools, Node node) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      if (event.getType().equals(EventTypes.MOUSE_ENTER)) {
        devtools.getHighlighter().highlight(node);
      } else {
        devtools.getHighlighter().highlight(null);
      }
    }
  }

  record RemoveNode(Node node) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      Element parent = node.getParent();
      if (parent == null) {
        return;
      }

      parent.removeChild(node);
    }
  }

  record ChangeAttrValue(Element el, String attrName) implements EventListener.Typed<InputEvent> {

    @Override
    public void handleEvent(InputEvent event) {
      String newValue = Strings.nullToEmpty(event.getNewValue());
      el.setAttribute(attrName, newValue);
    }
  }

  record RenameAttr(Element el, String attrName) implements EventListener.Typed<InputEvent> {

    @Override
    public void handleEvent(InputEvent event) {
      String newName = event.getNewValue();
      String value = el.removeAttribute(attrName);

      if (Strings.isNullOrEmpty(newName)) {
        return;
      }

      el.setAttribute(newName, value);
    }
  }

  record RemoveAttr(Element el, String attrName) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      el.removeAttribute(attrName);
    }
  }

  record AddAttr(Element el) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      Player player = event.getPlayer();
      Dialog dialog = createDialog(player, el);
      player.showDialog(dialog);
    }

    static Dialog createDialog(Player player, Element el) {
      return Dialog.create(f -> {
        DialogRegistryEntry.Builder builder = f.empty();
        builder.type(
            DialogType.confirmation(
                ActionButton.builder(translate(player, "delphi.input.yes"))
                    .action(
                        DialogAction.customClick(
                            (response, audience) -> {
                              String name = response.getText("name");
                              String value = response.getText("value");

                              if (Strings.isNullOrEmpty(name)) {
                                return;
                              }

                              el.setAttribute(name, value);
                            },
                            Options.builder().build()
                        )
                    )
                    .build(),
                ActionButton.builder(translate(player, "delphi.input.no"))
                    .build()
            )
        );

        builder.base(
            DialogBase.builder(translate(player, "delphi.devtools.addAttr.title"))
                .inputs(
                    List.of(
                        DialogInput.text(
                            "name",
                            translate(player, "delphi.devtools.addAttr.nameLabel")
                        )
                            .maxLength(Integer.MAX_VALUE)
                            .initial("")
                            .width(300)
                            .build(),

                        DialogInput.text(
                            "value",
                            translate(player, "delphi.devtools.addAttr.valueLabel")
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

  record SelectElement(
      Devtools devtools,
      Element targetElement
  ) implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      if (Objects.equals(devtools.getSelectedElement(), targetElement)) {
        return;
      }

      devtools.setSelectedElement(targetElement);
      event.stopPropagation();
      devtools.rerender();
    }
  }

  class DomBuilder {
    private final List<Element> lineContainer;
    private int lineCount = 1;
    private int indent = 0;
    private Element line;

    public DomBuilder() {
      this.lineContainer = new ArrayList<>();
    }

    public void append(Node n) {
      if (line == null) {
        line = document.createElement("div");
        line.getClassList().add("xml-line");
        line.setAttribute("line-num", String.valueOf(lineCount));

        if (indent > 0) {
          line.getClassList().add("indent-" + indent);
        }

        Element lineEl = document.createElement("line");
        lineEl.setClassName("lineno");
        lineEl.setTextContent(String.valueOf(lineCount));

        line.appendChild(lineEl);
      }

      line.appendChild(n);
    }

    public void linebreak() {
      if (line == null) {
        return;
      }

      lineCount++;
      lineContainer.addLast(line);

      line = null;
    }

    public void setLineSelected() {
      if (line == null) {
        return;
      }

      line.setAttribute("selected", "true");
    }
  }
}
