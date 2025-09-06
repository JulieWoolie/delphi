package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.juliewoolie.delphi.PlayerSet;
import com.juliewoolie.delphi.Screen;
import com.juliewoolie.delphi.resource.ApiModule;
import com.juliewoolie.delphi.resource.DirectoryModule;
import com.juliewoolie.delphi.resource.IoModule;
import com.juliewoolie.delphi.resource.JarResourceModule;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphi.resource.ZipModule;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.style.Stylesheet;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;

public class DocInfoTab extends DevToolTab {

  public DocInfoTab(Devtools devtools) {
    super(devtools);
  }

  @Override
  public void onOpen() {
    Element el = document.createElement("div");
    el.setClassName("docinfo");

    PlayerSet set = targetView.getPlayers();
    ViewResources resources = targetView.getResources();
    Screen screen = targetView.getScreen();

    String playerListType;
    Component playerListField = Component.translatable(
        "delphi.devtools.meta.players",
        Component.text(set.size())
    );

    Locale l = devtools.getLocale();

    if (set.isServerPlayerSet()) {
      playerListType = translateToString(l, "delphi.devtools.meta.players.all");
    } else if (set.isEmpty()) {
      playerListType = translateToString(l, "delphi.devtools.meta.none");
    } else {
      playerListType = set.stream().map(Player::getName).collect(Collectors.joining(", "));
    }

    String moduleType = switch (resources.getModule()) {
      case ApiModule apiModule -> {
        yield translateToString(l, "delphi.devtools.meta.moduleType.api");
      }
      case ZipModule zip -> {
        yield translateToString(
            l,
            "delphi.devtools.meta.moduleType.zip",
            path(zip.getZipFile())
        );
      }
      case DirectoryModule dir -> {
        yield translateToString(
            l,
            "delphi.devtools.meta.moduleType.dir",
            path(dir.getDirectory())
        );
      }
      case JarResourceModule jarRes -> {
        yield translateToString(l, "delphi.devtools.meta.moduleType.jar");
      }
      case IoModule io -> translateToString(l, "delphi.devtools.meta.moduleType.io");
      case null -> translateToString(l, "delphi.devtools.meta.moduleType.unknown");
    };

    int renderObjects = targetView.getRenderer().getRenderElements().size();
    int entityCount = targetView.getRenderer().getEntities().size();

    el.appendChild(createField( playerListField, playerListType));
    el.appendChild(createField("delphi.devtools.meta.instName", targetView.getInstanceName()));
    el.appendChild(createField("delphi.devtools.meta.moduleName", resources.getModuleName()));
    el.appendChild(createField("delphi.devtools.meta.path", targetView.getPath()));
    el.appendChild(createField("delphi.devtools.meta.moduleType", moduleType));
    el.appendChild(createField("delphi.devtools.meta.height", screen.getHeight()));
    el.appendChild(createField("delphi.devtools.meta.width", screen.getWidth()));
    el.appendChild(createField("delphi.devtools.meta.renderObjects", renderObjects));
    el.appendChild(createField("delphi.devtools.meta.entities", entityCount));

    List<Stylesheet> stylesheets = targetView.getDocument().getStylesheets();
    Element stylesheetsDiv;
    if (stylesheets.isEmpty()) {
      stylesheetsDiv = createField(
          "delphi.devtools.meta.stylesheets",
          translateToString(l, "delphi.devtools.meta.none")
      );
    } else {
      Element stylesheetsEl = document.createElement("ul");

      for (Stylesheet stylesheet : stylesheets) {
        String source = stylesheet.getSource();
        String txt = translateSheetSource(l, source);

        String suffix = translateToString(
            l,
            "delphi.devtools.meta.stylesheets.rules",
            Component.text(stylesheet.getLength())
        );

        Element line = document.createElement("div");
        line.setTextContent("- " + txt + " " + suffix);
        stylesheetsEl.appendChild(line);
      }
      stylesheetsDiv = createField("delphi.devtools.meta.stylesheets", stylesheetsEl);
    }

    el.appendChild(stylesheetsDiv);

    devtools.getContentEl().appendChild(el);
  }

  public static String translateSheetSource(Locale l, String source) {
    return switch (source) {
      case "default-stylesheet" -> translateToString(l, "delphi.stylesheets.default");
      case "inline" -> translateToString(l, "delphi.stylesheets.inline");
      case "programmatic" -> translateToString(l, "delphi.stylesheets.programmatic");
      default -> source;
    };
  }

  private Element createField(Object field, Object o) {
    Element div = document.createElement("div");
    div.setClassName("docinfo-property");

    String fieldName;
    if (field instanceof Component c) {
      fieldName = PlainTextComponentSerializer.plainText()
          .serialize(GlobalTranslator.render(c, devtools.getLocale()));
    } else {
      fieldName = translateToString(devtools.getLocale(), String.valueOf(field));
    }

    Element fieldEl = document.createElement("span");
    fieldEl.setClassName("docinfo-field");
    fieldEl.setTextContent(fieldName + ": ");

    Element valueEl = document.createElement("span");
    valueEl.setClassName("docinfo-value");

    if (o instanceof Node n) {
      valueEl.appendChild(n);
    } else {
      valueEl.setTextContent(String.valueOf(o));
    }

    div.appendChild(fieldEl);
    div.appendChild(valueEl);

    return div;
  }

  static Component path(Path p) {
    return Component.text(p.toString().replace("\\", "/"));
  }
}
