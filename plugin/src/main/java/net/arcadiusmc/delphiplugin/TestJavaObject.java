package net.arcadiusmc.delphiplugin;

import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventTypes;
import org.bukkit.entity.Player;

public class TestJavaObject {

  public static void onDomInitialize(Document document) {
    Loggers.getLogger().debug("Was invoked");
    document.addEventListener(EventTypes.DOM_LOADED, TestJavaObject::postDomInit);
  }

  public static void postDomInit(Event event) {
    Document document = event.getDocument();

    Element bigButton = document.getElementById("big-button");
    if (bigButton != null) {
      bigButton.addEventListener(EventTypes.CLICK, event1 -> {
        Player player = event1.getDocument().getView().getPlayer();
        player.sendRichMessage("<b><yellow>Hello, world!");
      });
    }
  }
}
