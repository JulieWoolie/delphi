package net.arcadiusmc.delphitest

import net.arcadiusmc.delphi.DelphiProvider
import net.arcadiusmc.delphi.resource.JarResourceModule
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class DelphiTestPlugin: JavaPlugin() {

  private var tickTask: BukkitTask? = null

  override fun onEnable() {
    val delphi = DelphiProvider.get()
    val jarMod = JarResourceModule(classLoader, "entity-editor")
    jarMod.filePaths = listOf("equipment.xml")
    delphi.resources.registerModule("entity-editor", jarMod)

    val pl = server.pluginManager
    pl.registerEvents(EntityEditorListener(), this)
  }

  override fun onDisable() {

  }

  private fun startTicking() {
    stopTicking()
    tickTask = server.scheduler.runTaskTimer(this, this::tick, 1, 1)
  }

  private fun tick() {

  }

  private fun stopTicking() {
    if (tickTask == null || tickTask!!.isCancelled) {
      return
    }

    tickTask!!.cancel()
    tickTask = null
  }
}