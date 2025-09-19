package com.juliewoolie.delphiplugin.command;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

public final class Permissions {
  private Permissions() {}

  public static final String COMMANDS = "delphi.commands";
  public static final String DEBUG = "delphi.commands.debug";
  public static final String DEVTOOLS = "delphi.commands.devtools";
  public static final String PLAYERS = "delphi.commands.players";
  public static final String UPDATE_NOTICE = "delphi.updatenotice";

  public static void registerAll() {
    register(COMMANDS);
    register(DEBUG);
    register(DEVTOOLS);
    register(PLAYERS);
  }

  private static void register(String name) {
    PluginManager pl = Bukkit.getPluginManager();
    Permission permission = pl.getPermission(name);

    if (permission == null) {
      permission = new Permission(name);
      pl.addPermission(permission);
    }
  }
}
