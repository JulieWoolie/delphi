package net.arcadiusmc.delphitest

import net.arcadiusmc.delphi.DelphiProvider
import net.arcadiusmc.delphi.DocumentView
import net.arcadiusmc.delphi.resource.DelphiException
import net.arcadiusmc.delphi.util.Result
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class EntityEditorListener: Listener {

  private val logger: Logger = LoggerFactory.getLogger("EntityEditorListener")
  private val cooldownMap: MutableMap<UUID, Long> = HashMap()

  @EventHandler
  fun onEntityInteract(event: PlayerInteractEntityEvent) {
    val player = event.player
    val target = event.rightClicked

    val heldItem = player.inventory.itemInMainHand

    if (target !is LivingEntity) {
      return
    }
    if (heldItem.isEmpty) {
      return
    }
    if (heldItem.type != Material.STICK) {
      return
    }
    if (isOnCooldown(player)) {
      return
    }

    putOnCooldown(player)

    val delphi = DelphiProvider.get()
    val result: Result<DocumentView, DelphiException> = delphi.openDocument(
      "entity-editor:equipment.xml?entity-id=${target.uniqueId}",
      player
    )

    result.ifError {
      logger.error("Failed to open entity-editor:", it)
    }
  }

  private fun putOnCooldown(player: Player) {
    val cdEnd = System.currentTimeMillis() + 500
    cooldownMap[player.uniqueId] = cdEnd
  }

  private fun isOnCooldown(player: Player): Boolean {
    val l = cooldownMap[player.uniqueId] ?: return false
    return l >= System.currentTimeMillis()
  }
}