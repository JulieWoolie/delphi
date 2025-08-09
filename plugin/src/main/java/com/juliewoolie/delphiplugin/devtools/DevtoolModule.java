package com.juliewoolie.delphiplugin.devtools;

import com.juliewoolie.delphi.Delphi;
import com.juliewoolie.delphi.DelphiProvider;
import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.delphi.resource.ApiModule;
import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphi.resource.DocumentContext;
import com.juliewoolie.delphi.resource.ResourceModule;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.delphiplugin.math.Screen;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.style.Stylesheet;
import com.juliewoolie.dom.style.StylesheetBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DevtoolModule implements ApiModule {

  static final float DEVTOOLS_DIST_FROM_TARGET = 0.25f;
  static final float DEVTOOLS_VIEW_ANGLE = Math.toRadians(12.0f);
  static final float DEVTOOLS_WIDTH = 3.0f;
  static final float DEVTOOLS_HEIGHT = 2.1f;

  public static void applyTransforms(DocumentView targetView, DocumentView devtoolsView) {
    Vector3f globalUp = new Vector3f(0, -1, 0);
    Vector3f up = new Vector3f();
    Vector3f right = new Vector3f();

    Screen screen = (Screen) targetView.getScreen();
    Vector3f normal = screen.normal();

    normal.cross(globalUp, right);
    right.normalize();
    normal.cross(right, up);

    Vector3f left = new Vector3f(right).mul(-1);

    Vector3f pos = new Vector3f();
    pos.x = screen.loLeft.x + (left.x * DEVTOOLS_DIST_FROM_TARGET);
    pos.y = screen.loLeft.y + (left.y * DEVTOOLS_DIST_FROM_TARGET);
    pos.z = screen.loLeft.z + (left.z * DEVTOOLS_DIST_FROM_TARGET);

    left.mul(DEVTOOLS_WIDTH * 0.5f);
    Quaternionf quat = new Quaternionf();
    quat.rotateAxis(DEVTOOLS_VIEW_ANGLE, up);

    quat.transform(left);

    pos.add(left);
    pos.x += up.x * (DEVTOOLS_HEIGHT * 0.5f);
    pos.y += up.y * (DEVTOOLS_HEIGHT * 0.5f);
    pos.z += up.z * (DEVTOOLS_HEIGHT * 0.5f);

    Quaternionf lrot = new Quaternionf(screen.leftRotation).mul(quat);
    Quaternionf rrot = new Quaternionf(screen.rightRotation);
    Vector3f scale = new Vector3f(screen.scale);

    devtoolsView.setScreenTransform(new Transformation(pos, lrot, scale, rrot));
  }

  public static Result<DocumentView, DelphiException> openDevtoolsFor(Player player, DocumentView targetView) {
    Delphi delphi = DelphiProvider.get();

    String playerName = player.getName().toLowerCase();
    String targetName = targetView.getInstanceName();
    String instName = String.format("devtools-%s-%s", playerName, targetName);

    ResourcePath path = ResourcePath.create("devtools").setQuery("target", targetName);

    return delphi.newViewBuilder()
        .setPlayer(player)
        .setPath(path)
        .setInstanceName(instName)
        .open()
        .ifSuccess(devtools -> {
          applyTransforms(targetView, devtools);
        });
  }

  @Override
  public Result<Document, String> loadDocument(
      @NotNull ResourcePath path,
      @NotNull DocumentContext context
  ) {
    String targetInstName = path.getQuery("target");
    if (Strings.isNullOrEmpty(targetInstName)) {
      return Result.err("No 'target' query param set");
    }

    Delphi delphi = DelphiProvider.get();
    Optional<DocumentView> viewOpt = delphi.getByInstanceName(targetInstName);

    if (viewOpt.isEmpty()) {
      return Result.err("No view with instance-name '" + targetInstName + "' found");
    }

    DocumentView view = viewOpt.get();
    ResourceModule targetModule = view.getResources().getModule();

    if (targetModule instanceof DevtoolModule) {
      return Result.err("Cannot open devtools on devtools view");
    }

    String domSource = loadResource("devtools/index.xml");
    String scssSource = loadResource("devtools/devtools.scss");

    Stylesheet stylesheet = context.parseStylesheet(scssSource);
    Stylesheet indents = generateIndentsSheet(context);

    Document dom = context.parseDocument(domSource);
    dom.addStylesheet(stylesheet);
    dom.addStylesheet(indents);

    Devtools devtools = new Devtools(view, dom);
    devtools.switchTo(Tabs.INSPECT_ELEMENT);

    return Result.ok(dom);
  }

  private Stylesheet generateIndentsSheet(DocumentContext ctx) {
    StylesheetBuilder builder = ctx.newStylesheet();
    final int indentLevels = 25;

    for (int i = 1; i <= indentLevels; i++) {
      float indentLevel = ((float) i) * 0.5f;

      builder.addRule(".indent-" + i + " span:first-of-type", prop -> {
        prop.setMarginInlineStart(indentLevel + "ch");
      });
    }

    return builder.build();
  }

  private String loadResource(String uri) {
    InputStream stream = getClass().getClassLoader().getResourceAsStream(uri);
    if (stream == null) {
      return "";
    }

    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    StringWriter writer = new StringWriter();

    try {
      reader.transferTo(writer);
      stream.close();
    } catch (IOException exc) {
      throw new RuntimeException(exc);
    }

    return writer.toString();
  }

  @Override
  public @NotNull Collection<String> getModulePaths(@NotNull ResourcePath pathSoFar) {
    return List.of();
  }
}
