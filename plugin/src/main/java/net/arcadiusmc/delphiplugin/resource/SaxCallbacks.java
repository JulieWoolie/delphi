package net.arcadiusmc.delphiplugin.resource;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphidom.parser.PluginMissingException;
import net.arcadiusmc.delphidom.parser.SaxParserCallbacks;
import net.arcadiusmc.dom.OptionElement;
import net.arcadiusmc.dom.Options;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class SaxCallbacks implements SaxParserCallbacks {

  @Override
  public void validateOptionDeclaration(OptionElement opt) throws PluginMissingException {
    String name = opt.getName();
    String val = opt.getValue();

    if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(val)) {
      return;
    }

    name = name.toLowerCase();

    switch (name) {
      case Options.REQUIRED_PLUGINS -> {
        String[] tokens = val.split("\\s+");
        List<String> missing = new ArrayList<>();
        PluginManager manager = Bukkit.getPluginManager();

        for (String token : tokens) {
          if (manager.isPluginEnabled(token)) {
            continue;
          }

          missing.add(token);
        }

        if (missing.isEmpty()) {
          return;
        }

        throw new PluginMissingException(missing);
      }

      default -> {

      }
    }
  }
}
