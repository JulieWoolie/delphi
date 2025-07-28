package com.juliewoolie.delphiplugin.devtools;

import java.util.stream.Collectors;
import com.juliewoolie.delphi.PlayerSet;
import com.juliewoolie.delphi.Screen;
import com.juliewoolie.delphi.resource.ApiModule;
import com.juliewoolie.delphi.resource.DirectoryModule;
import com.juliewoolie.delphi.resource.IoModule;
import com.juliewoolie.delphi.resource.JarResourceModule;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphi.resource.ZipModule;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import org.bukkit.entity.Player;

public class DocInfoTab implements DevToolTab {

  @Override
  public void onOpen(Devtools devtools) {
    Document d = devtools.getDocument();
    PageView view = (PageView) devtools.getTarget();

    Element el = d.createElement("div");
    el.setClassName("docinfo");

    PlayerSet set = view.getPlayers();
    ViewResources resources = view.getResources();
    Screen screen = view.getScreen();

    String playerListType;

    if (set.isServerPlayerSet()) {
      playerListType = "all";
    } else if (set.isEmpty()) {
      playerListType = "none";
    } else {
      playerListType = set.stream().map(Player::getName).collect(Collectors.joining(", "));
    }

    String moduleType = switch (resources.getModule()) {
      case ApiModule apiModule -> "api-module";
      case ZipModule zip -> "zip(" + zip.getZipFile() + ")";
      case DirectoryModule dir -> "directory(" + dir.getDirectory() + ")";
      case JarResourceModule jarRes -> "jar-resource";
      case IoModule io -> "io-module";
      case null -> "unknown";
    };

    int renderObjects = view.getRenderer().getRenderElements().size();

    el.appendChild(createField(d, "Players", playerListType));
    el.appendChild(createField(d, "Instance Name", view.getInstanceName()));
    el.appendChild(createField(d, "Module Name", resources.getModuleName()));
    el.appendChild(createField(d, "Resource Path", view.getPath()));
    el.appendChild(createField(d, "Module Type", moduleType));
    el.appendChild(createField(d, "Screen Height", screen.getHeight()));
    el.appendChild(createField(d, "Screen Width", screen.getWidth()));
    el.appendChild(createField(d, "Render Objects", renderObjects));
    el.appendChild(createField(d, "Entities", view.getRenderer().getEntities().size()));

    devtools.getContentEl().appendChild(el);
  }

  private Element createField(Document d, String field, Object o) {
    Element div = d.createElement("div");
    div.setClassName("docinfo-property");

    Element fieldEl = d.createElement("span");
    fieldEl.setClassName("docinfo-field");
    fieldEl.setTextContent(field + ": ");

    Element valueEl = d.createElement("span");
    valueEl.setClassName("docinfo-value");
    valueEl.setTextContent(String.valueOf(o));

    div.appendChild(fieldEl);
    div.appendChild(valueEl);

    return div;
  }

  @Override
  public void onClose(Devtools devtools) {

  }
}
