package com.juliewoolie.delphiplugin.listeners;

import static com.juliewoolie.delphiplugin.command.DelphiCommand.prefixTranslatable;

import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphiplugin.PluginUpdater;
import com.juliewoolie.delphiplugin.PluginUpdater.PluginVersion;
import com.juliewoolie.delphiplugin.ViewManager;
import com.juliewoolie.delphiplugin.ViewManager.ViewEntry;
import com.juliewoolie.delphiplugin.command.Permissions;
import com.juliewoolie.delphiplugin.gimbal.DelphiGizmo;
import com.juliewoolie.delphiplugin.gimbal.GizmoManager;
import com.juliewoolie.dom.event.MouseButton;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;

public class PlayerListener implements Listener {

  private static final Logger LOGGER = Loggers.getLogger();

  private final DelphiPlugin plugin;

  ClickEvent silentlyUpdate;
  boolean downloaded = false;

  public PlayerListener(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    PluginVersion latest = plugin.getFoundLatest();
    if (latest == null) {
      return;
    }

    Player player = event.getPlayer();
    if (!player.hasPermission(Permissions.UPDATE_NOTICE)) {
      return;
    }
    if (plugin.getDelphiConfig().autoUpdatePlugin) {
      return;
    }

    if (silentlyUpdate == null) {
      silentlyUpdate = ClickEvent.callback(
          audience -> {
            if (downloaded) {
              audience.sendMessage(Component.translatable("delphi.update.alreadyDownloaded"));
              return;
            }

            PluginUpdater.downloadUpdate(plugin.getFoundLatest());
            downloaded = true;

            audience.sendMessage(
                Component.translatable("delphi.update.downloaded",
                    Component.text(plugin.getFoundLatest().version())
                )
            );
          },
          Options.builder()
              .lifetime(Duration.ofDays(365))
              .uses(-1)
              .build()
      );
    }

    player.sendMessage(
        prefixTranslatable(
            "delphi.update.notice",
            NamedTextColor.GRAY,

            Component.text(latest.version()),

            Component.translatable("delphi.update.openUrl")
                .clickEvent(ClickEvent.openUrl(latest.downloadUrl()))
                .hoverEvent(Component.text(latest.downloadUrl())),

            Component.translatable("delphi.update.download")
                .clickEvent(silentlyUpdate)
                .hoverEvent(Component.translatable("delphi.update.download.hover"))
        )
    );
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    ViewManager views = plugin.getViewManager();
    ViewEntry entry = views.getByPlayer().remove(player);

    if (entry == null) {
      return;
    }

    List<PageView> viewList = new ArrayList<>(entry.views);
    for (PageView pageView : viewList) {
      pageView.getPlayers().remove(player);
    }
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    tryInteract(event.getPlayer(), event, MouseButton.RIGHT);
  }

  @EventHandler
  public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
    if (!(event.getRightClicked() instanceof ArmorStand)) {
      return;
    }

    onPlayerInteractEntity(event);
  }

  @EventHandler
  public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event) {
    tryInteract(event.getPlayer(), event, MouseButton.LEFT);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action act = event.getAction();

    // Dumb but it stops actions we don't care about from affecting
    // page interaction
    switch (act) {
      case LEFT_CLICK_BLOCK:
      case LEFT_CLICK_AIR:
      case RIGHT_CLICK_AIR:
      case RIGHT_CLICK_BLOCK:
        break;

      default:
        return;
    }

    MouseButton button = act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK
        ? MouseButton.LEFT
        : MouseButton.RIGHT;

    tryInteract(player, event, button);
  }

  private void tryInteract(Player player, Cancellable event, MouseButton button) {
    if (button == MouseButton.LEFT) {
      GizmoManager gizmos = plugin.getGizmoManager();
      DelphiGizmo gizmo = gizmos.getSelectedByPlayer(player);

      if (gizmo != null) {
        gizmo.onClick();
        event.setCancelled(true);
        return;
      }
    }

    ViewManager views = plugin.getViewManager();
    ViewEntry entry = views.getByPlayer().get(player);

    if (entry == null) {
      return;
    }
    PageView selected = entry.selected;
    if (selected == null) {
      return;
    }

    boolean shift = player.isSneaking();

    selected.onInteract(player, button, shift);
    event.setCancelled(true);
  }
}
