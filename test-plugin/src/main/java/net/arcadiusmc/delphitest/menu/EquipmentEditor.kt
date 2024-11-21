package net.arcadiusmc.delphitest.menu

import net.arcadiusmc.delphitest.DelphiTestPlugin
import net.arcadiusmc.delphitest.MoveToEntityTask
import net.arcadiusmc.dom.Document
import net.arcadiusmc.dom.Element
import net.arcadiusmc.dom.ItemElement
import net.arcadiusmc.dom.event.EventListener
import net.arcadiusmc.dom.event.EventTypes
import net.arcadiusmc.dom.event.MouseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

private val SLOTS: Map<String, EquipmentSlot> = mapOf(
  "helmet" to EquipmentSlot.HEAD,
  "chestplate" to EquipmentSlot.CHEST,
  "leggings" to EquipmentSlot.LEGS,
  "boots" to EquipmentSlot.FEET
)

fun onDomInitialize(document: Document) {
  val entity = getEntity(document)
  val state = MenuState(entity)

  document.addEventListener(EventTypes.DOM_LOADED) {
    onDomLoaded(it.document, state)
  }
}

fun onDomLoaded(document: Document, state: MenuState) {
  val entity = state.entity

  val entityTypeEl = document.getElementById("entity-type")
  val entityIdEl = document.getElementById("entity-id")

  if (entity != null) {
    if (entityTypeEl != null) {
      entityTypeEl.textContent = entity.type.key.value()
    }
    if (entityIdEl != null) {
      entityIdEl.textContent = entity.uniqueId.toString()
    }
  }

  for (slot in SLOTS) {
    val itemEl: Element? = document.getElementById("${slot.key}-slot")
    val removeBtn: Element? = document.getElementById("${slot.key}-btn-remove")
    val setBtn: Element? = document.getElementById("${slot.key}-btn-set")

    val controller = SlotController(state, slot.value)
    controller.itemEl = itemEl as ItemElement?
    controller.removeButton = removeBtn
    controller.setButton = setBtn

    controller.initialize()
  }

  if (entity != null) {
    val task = MoveToEntityTask(entity, document.view)
    val plugin = JavaPlugin.getPlugin(DelphiTestPlugin::class.java)
    plugin.server.scheduler.runTaskTimer(plugin, task, 1, 1)
  }
}

class SlotController(val state: MenuState, val eqSlot: EquipmentSlot) {
  var itemEl: ItemElement? = null
  var removeButton: Element? = null
  var setButton: Element? = null

  fun initialize() {
    val entity = state.entity
    val slotItem = entity?.equipment?.getItem(eqSlot)

    if (itemEl != null && slotItem != null) {
      itemEl!!.itemStack = slotItem
    }

    if (removeButton != null) {
      removeButton!!.addEventListener(EventTypes.CLICK) {
        onRemoveClick()
      }
    }
    if (setButton != null) {
      val listener: EventListener.Typed<MouseEvent> = EventListener.Typed {
        val player = it.player
        onSetClick(player)
      }

      setButton!!.addEventListener(EventTypes.CLICK, listener)
    }
  }

  private fun onRemoveClick() {
    if (itemEl != null) {
      itemEl!!.itemStack = null
    }

    val entity = state.entity ?: return
    val equipment = entity.equipment ?: return

    equipment.setItem(eqSlot, null)
  }

  private fun onSetClick(player: Player) {
    val inventory = player.inventory
    val heldItem = inventory.itemInMainHand

    if (heldItem.isEmpty) {
      player.sendRichMessage("<red>You must be holding an item.")
      return
    }

    if (itemEl != null) {
      itemEl!!.itemStack = heldItem.clone()
    }

    val entity = state.entity ?: return
    val equipment = entity.equipment ?: return

    equipment.setItem(eqSlot, heldItem.clone())
  }
}