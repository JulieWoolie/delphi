package net.arcadiusmc.delphidom.scss;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.scss.Property.StyleFunction;
import net.arcadiusmc.delphidom.scss.PropertySet.RuleIterator;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import org.slf4j.Logger;

public class DocumentStyles {

  private static final Logger LOGGER = Loggers.getLogger();

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

    if (document.getBody() != null) {
      updateStyles(document.getBody());
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

  public void updateStyles(DelphiNode node) {
    PropertySet set = node.styleSet;
    PropertySet newSet = new PropertySet();
    PropertySet old = new PropertySet();
    old.putAll(set);

    applyCascading(newSet, node);

    if (node instanceof DelphiElement el) {
      for (Rule rule : rules) {
        if (!rule.getSelectorObj().test(null, el)) {
          continue;
        }

        newSet.putAll(rule.getPropertySet());
      }

      newSet.putAll(el.inlineStyle.getBacking());
    }

    set.clear();
    int dirtyBits = set.putAll(newSet);

    if (dirtyBits != 0) {
      applyRules(node, newSet);

      if (document.getView() != null) {
        ChangeSet changeSet = new ChangeSet();
        changeSet.changes |= dirtyBits;
        document.getView().styleUpdated(node, dirtyBits);
      }
    }

    if (!(node instanceof DelphiElement el)) {
      return;
    }

    for (DelphiNode delphiNode : el.childList()) {
      updateStyles(delphiNode);
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
      Loggers.getDocumentLogger().debug("style update listener called");
      DelphiNode node = (DelphiNode) event.getTarget();
      updateStyles(node);
    }
  }

  public static class ChangeSet {
    private int changes = 0;

    public boolean contains(DirtyBit change) {
      return (changes & change.mask) == change.mask;
    }

    public void add(DirtyBit change) {
      changes |= change.mask;
    }

    public boolean isEmpty() {
      return changes == 0;
    }
  }
}
