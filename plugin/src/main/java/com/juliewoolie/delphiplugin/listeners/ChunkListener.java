package com.juliewoolie.delphiplugin.listeners;

import com.juliewoolie.delphidom.DelphiBodyElement;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphiplugin.PageView;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.joml.Vector3f;

public class ChunkListener implements Listener {

  private final DelphiPlugin plugin;

  public ChunkListener(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onChunkUnload(ChunkUnloadEvent event) {
    Chunk chunk = event.getChunk();

    int cx = chunk.getX();
    int cz = chunk.getZ();

    int cx0 = cx << 4;
    int cz0 = cz << 4;
    int cx1 = cx0 + 16;
    int cz1 = cz0 + 16;

    for (PageView openView : plugin.getViewManager().getOpenViews()) {
      Vector3f center = openView.getScreen().center;

      if (!inChunk(center, cx0, cz0, cx1, cz1)) {
        continue;
      }

      openView.getRenderer().kill();
    }
  }

  boolean inChunk(Vector3f p, int cx0, int cz0, int cx1, int cz1) {
    return (p.x >= cx0 && p.x <= cx1)
        && (p.z >= cz0 && p.z <= cz1);
  }


  @EventHandler(ignoreCancelled = true)
  public void onChunkLoad(ChunkLoadEvent event) {
    Chunk chunk = event.getChunk();

    int cx = chunk.getX();
    int cz = chunk.getZ();

    int cx0 = cx << 4;
    int cz0 = cz << 4;
    int cx1 = cx0 + 16;
    int cz1 = cz0 + 16;

    for (PageView openView : plugin.getViewManager().getOpenViews()) {
      Vector3f center = openView.getScreen().center;

      if (!inChunk(center, cx0, cz0, cx1, cz1)) {
        continue;
      }

      DelphiDocument doc = openView.getDocument();
      if (doc == null) {
        continue;
      }

      DelphiBodyElement body = doc.getBody();
      if (body == null) {
        continue;
      }

      openView.getRenderer().triggerRedraw(body);
    }
  }

}
