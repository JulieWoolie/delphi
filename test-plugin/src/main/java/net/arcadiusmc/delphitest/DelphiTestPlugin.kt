package net.arcadiusmc.delphitest

import net.arcadiusmc.delphi.DelphiProvider
import net.arcadiusmc.delphi.resource.JarResourceModule
import org.bukkit.plugin.java.JavaPlugin

class DelphiTestPlugin: JavaPlugin() {

  override fun onEnable() {
    val delphi = DelphiProvider.get()
    //delphi.resources.registerModule("entity-editor", EntityEditorModule())

    val jarMod = JarResourceModule(classLoader, "entity-editor")
    jarMod.filePaths = listOf("equipment.xml")
    delphi.resources.registerModule("entity-editor", jarMod)

    val pl = server.pluginManager
    pl.registerEvents(EntityEditorListener(), this)
  }

  override fun onDisable() {

  }
}