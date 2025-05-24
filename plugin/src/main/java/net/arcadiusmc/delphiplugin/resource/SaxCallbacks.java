package net.arcadiusmc.delphiplugin.resource;

import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MISSING_PLUGINS;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_OLD_GAME_VERSION;

import com.google.common.base.Strings;
import java.util.StringJoiner;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.parser.SaxParserCallbacks;
import net.arcadiusmc.delphiplugin.SemanticVersions;
import net.arcadiusmc.dom.OptionElement;
import net.arcadiusmc.dom.Options;
import net.arcadiusmc.hephaestus.ScriptElementSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;

public class SaxCallbacks implements SaxParserCallbacks {

  private static final Logger LOGGER = Loggers.getLogger();

  private final PluginResources pluginResources;

  public SaxCallbacks(PluginResources pluginResources) {
    this.pluginResources = pluginResources;
  }

  @Override
  public void onDocumentCreated(DelphiDocument document) {
    if (pluginResources.isScriptingEnabled()) {
      document.addSystem(new ScriptElementSystem());
    }
  }

  @Override
  public void validateOptionDeclaration(OptionElement opt) {
    String name = opt.getName();
    String val = opt.getValue();

    if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(val)) {
      return;
    }

    name = name.toLowerCase();

    switch (name) {
      case Options.REQUIRED_PLUGINS -> {
        String[] tokens = val.split("\\s+");
        PluginManager manager = Bukkit.getPluginManager();

        StringJoiner missingNames = new StringJoiner(", ");
        int missing = 0;

        for (String token : tokens) {
          if (manager.isPluginEnabled(token)) {
            continue;
          }

          missingNames.add(token);
          missing++;
        }

        if (missing < 1) {
          return;
        }

        throw new DelphiException(ERR_MISSING_PLUGINS, missingNames.toString());
      }

      case Options.MINIMUM_GAME_VERSION -> {
        String gameVersionStr = Bukkit.getMinecraftVersion();
        int cmp;

        try {
          int[] minVersion = SemanticVersions.decompose(val);
          int[] gVersion = SemanticVersions.decompose(gameVersionStr);

          cmp = SemanticVersions.compare(minVersion, gVersion);
        } catch (NumberFormatException exc) {
          cmp = val.compareTo(gameVersionStr);
        }

        if (cmp > 0) {
          throw new DelphiException(ERR_OLD_GAME_VERSION, val);
        }
      }

      default -> {

      }
    }
  }
}
