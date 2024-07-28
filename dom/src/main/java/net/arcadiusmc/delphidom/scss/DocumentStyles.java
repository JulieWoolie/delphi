package net.arcadiusmc.delphidom.scss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.scss.Property.StyleFunction;
import net.arcadiusmc.delphidom.scss.PropertySet.RuleIterator;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;

public class DocumentStyles {

  public final List<Sheet> stylesheets = new ArrayList<>();
  private final List<Rule> rules = new ArrayList<>();

  private final DelphiDocument document;

  public DocumentStyles(DelphiDocument document) {
    this.document = document;
  }

  public void addSheet(Sheet sheet) {
    stylesheets.add(sheet);

    for (int i = 0; i < sheet.getLength(); i++) {
      Rule r = sheet.getRule(i);
      rules.add(r);
    }

    rules.sort(Comparator.naturalOrder());

    ChangeSet changed = new ChangeSet();
    if (document.getBody() != null) {
      updateStyles(document.getBody(), changed);
    }

    if (document.getView() != null) {
      document.getView().sheetAdded(changed);
    }
  }

  public void init() {
    EventListenerList target = document.getGlobalTarget();
    StyleUpdateListener listener = new StyleUpdateListener();

    target.addEventListener(EventTypes.MODIFY_ATTR, listener);
    target.addEventListener(EventTypes.APPEND_CHILD, listener);

    target.addEventListener(EventTypes.MOUSE_DOWN, listener);
    target.addEventListener(EventTypes.CLICK_EXPIRE, listener);
    target.addEventListener(EventTypes.MOUSE_ENTER, listener);
    target.addEventListener(EventTypes.MOUSE_LEAVE, listener);
  }

  public void updateStyles(DelphiNode node, ChangeSet changes) {
    PropertySet set = node.styleSet;
    PropertySet newSet = new PropertySet();
    PropertySet old = new PropertySet();
    old.putAll(set);

    applyCascading(newSet, node);

    if (node instanceof DelphiElement el) {
      for (Rule rule : rules) {
        if (!rule.getSelectorObj().test(el)) {
          continue;
        }

        newSet.putAll(rule.getPropertySet());
      }

      newSet.putAll(el.inlineStyle.getBacking());
    }

    set.clear();
    int dirtyBits = set.putAll(newSet);

    if (dirtyBits != 0) {
      if (document.getView() != null) {
        document.getView().styleChanged(dirtyBits, node);
      }

      applyRules(node, newSet);
    }

    if (changes != null) {
      changes.changes |= dirtyBits;
    }

    if (!(node instanceof DelphiElement el)) {
      return;
    }

    for (DelphiNode delphiNode : el.childList()) {
      updateStyles(delphiNode, changes);
    }
  }

  private void applyCascading(PropertySet target, DelphiNode node) {
    DelphiElement parent = node.getParent();

    if (parent == null) {
      return;
    }

    PropertySet parentSet = parent.styleSet;
    RuleIterator it = parentSet.iterator();

    while (it.hasNext()) {
      it.next();

      Property<Object> rule = it.property();

      if (!rule.isCascading()) {
        continue;
      }

      Object o = it.value();

      if (target.has(rule)) {
        continue;
      }

      target.set(rule, o);
    }
  }

  private void applyRules(DelphiNode el, PropertySet set) {
    Screen screen;
    ComputedStyle s = el.style;

    if (document.getView() == null) {
      screen = null;
    } else {
      screen = document.getView().getScreen();
    }

    for (int i = 0; i < Properties.count(); i++) {
      Property<Object> prop = Properties.getById(i);
      StyleFunction<Object> func = prop.getApplicator();

      if (func == null) {
        continue;
      }

      func.apply(s, screen, set.get(prop));
    }
  }

  class StyleUpdateListener implements EventListener {

    @Override
    public void onEvent(Event event) {
      ChangeSet set = new ChangeSet();
      DelphiNode node = (DelphiNode) event.getTarget();
      updateStyles(node, set);

      if (document.getView() != null) {
        document.getView().styleUpdated(node, set);
      }
    }
  }

  public static class ChangeSet {
    private int changes = 0;

    boolean contains(DirtyBit change) {
      return (changes & change.mask) == change.mask;
    }

    void add(DirtyBit change) {
      changes |= change.mask;
    }

    boolean isEmpty() {
      return changes == 0;
    }
  }
}
