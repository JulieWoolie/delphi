package com.juliewoolie.delphiplugin;

import org.bukkit.configuration.ConfigurationSection;

public class DelphiConfig {

  public boolean autoUpdatePlugin = true;

  void load(ConfigurationSection sect) {
    autoUpdatePlugin = sect.getBoolean("enable-auto-updater", true);
  }
}
