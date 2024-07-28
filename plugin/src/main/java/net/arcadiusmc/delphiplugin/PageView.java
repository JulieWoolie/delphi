package net.arcadiusmc.delphiplugin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.ExtendedView;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.DelphiNode;
import net.arcadiusmc.delphi.dom.Text;
import net.arcadiusmc.delphi.dom.event.EventListenerList;
import net.arcadiusmc.delphi.dom.scss.DocumentStyles.ChangeSet;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.delphiplugin.render.ElementRenderObject;
import net.arcadiusmc.delphiplugin.render.RenderObject;
import net.arcadiusmc.delphiplugin.render.TextRenderObject;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PageView implements ExtendedView {

  @Getter
  private final Screen screen = new Screen();

  public final Vector2f cursorScreen = new Vector2f();
  public final Vector3f cursorWorld = new Vector3f();

  @Getter
  private final Player player;
  @Setter @Getter
  private PlayerSession session;

  @Getter
  private final ResourcePath path;

  @Getter @Setter
  private PageResources resources;

  @Getter
  private DelphiDocument document;

  private final Map<DelphiNode, RenderObject> renderTree = new Object2ObjectOpenHashMap<>();
  private final List<Display> entities = new ArrayList<>();

  public PageView(Player player, ResourcePath path) {
    this.player = player;
    this.path = path;
  }

  public void spawn() {
    for (RenderObject value : renderTree.values()) {
      value.spawn();
    }
  }

  public void kill() {
    for (Display entity : entities) {
      entity.remove();
    }

    entities.clear();
  }

  public void initializeDocument(DelphiDocument document) {
    this.document = document;

    if (document == null) {
      return;
    }

    EventListenerList g = document.getGlobalTarget();
    MutationListener listener = new MutationListener();

    g.addEventListener(EventTypes.APPEND_CHILD, listener);
    g.addEventListener(EventTypes.REMOVE_CHILD, listener);

    if (document.getBody() != null) {
      initRenderTree(document.getBody());
    }
  }

  private RenderObject initRenderTree(DelphiNode node) {
    RenderObject obj;

    if (node instanceof Text text) {
      obj = new TextRenderObject(text, screen);
    } else {
      DelphiElement el = (DelphiElement) node;
      ElementRenderObject ro = new ElementRenderObject(el, screen);

      for (DelphiNode delphiNode : el.childList()) {
        ro.getChildren().add(initRenderTree(delphiNode));
      }

      obj = ro;
    }

    renderTree.put(node, obj);
    return obj;
  }

  public RenderObject getRenderObject(DelphiNode node) {
    return renderTree.get(node);
  }

  @Override
  public void styleChanged(int dirtyBits, DelphiNode element) {

  }

  @Override
  public void styleUpdated(DelphiNode node, ChangeSet set) {

  }

  @Override
  public void sheetAdded(ChangeSet changed) {

  }

  @Override
  public void textChanged(Text text) {

  }

  @Override
  public Vector2f getCursorScreenPosition() {
    return new Vector2f(cursorScreen);
  }

  @Override
  public Vector3f getCursorWorldPosition() {
    return new Vector3f(cursorWorld);
  }

  @Override
  public Map<String, Object> getStyleVariables() {
    if (resources == null) {
      return new HashMap<>();
    }

    return resources.getStyleVariables();
  }

  @Override
  public void close() {
    if (session != null) {
      session.removeView(this);
    } else {
      kill();
    }
  }

  class MutationListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {

    }
  }
}
