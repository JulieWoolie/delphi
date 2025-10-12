package com.juliewoolie.delphiplugin;

import org.bukkit.configuration.ConfigurationSection;

public class DelphiConfig {

  public boolean autoUpdatePlugin = false;

  void load(ConfigurationSection sect) {
    autoUpdatePlugin = sect.getBoolean("enable-auto-updater", false);
  }
}
