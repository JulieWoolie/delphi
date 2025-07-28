package com.juliewoolie.delphitest.menu

import com.juliewoolie.dom.Document
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*

fun getEntity(document: Document): LivingEntity? {
  val path = document.view.path
  val entityId = path.getQuery("entity-id") ?: return null

  val uuid: UUID

  try {
    uuid = UUID.fromString(entityId)
  } catch (e: IllegalArgumentException) {
    return null;
  }

  val entity = Bukkit.getEntity(uuid) ?: return null
  if (entity !is LivingEntity) {
    return null
  }

  return entity
}