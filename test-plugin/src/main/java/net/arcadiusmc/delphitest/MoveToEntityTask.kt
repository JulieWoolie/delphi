package net.arcadiusmc.delphitest

import net.arcadiusmc.delphi.DocumentView
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import org.joml.Vector3f
import java.util.function.Consumer

class MoveToEntityTask(val entity: Entity, val view: DocumentView): Consumer<BukkitTask> {

  private val moveDir: Vector3f = Vector3f()
  private val entityPos: Vector3f = Vector3f()

  override fun accept(t: BukkitTask) {
    if (view.isClosed || entity.isDead) {
      t.cancel()
      return
    }

    val player = view.players.iterator().next()

    entityPos.x = entity.x.toFloat()
    entityPos.y = entity.y.toFloat()
    entityPos.z = entity.z.toFloat()

    getRelativeDirection(player, entityPos)

    val up = Vector3f(0f, 1f, 0f)
    val right = Vector3f()

    up.cross(moveDir, right)

    val width = view.screen.worldWidth
    right.normalize().mul((width * 0.5f) + entity.width.toFloat())

    entityPos.add(right)
    entityPos.y += 0.05f

    val location = Location(
      entity.world,
      entityPos.x.toDouble(),
      entityPos.y.toDouble(),
      entityPos.z.toDouble()
    )

    //getRelativeDirection(player, entityPos)

    location.direction = Vector(
      moveDir.x.toDouble(),
      moveDir.y.toDouble(),
      moveDir.z.toDouble()
    )

    view.moveTo(location)
  }

  private fun getRelativeDirection(player: Player, targetPos: Vector3f) {
    moveDir.x = player.x.toFloat() - targetPos.x
    moveDir.y = 0f
    moveDir.z = player.z.toFloat() - targetPos.z
    moveDir.normalize()

    val directions: Array<BlockFace> = arrayOf(
      BlockFace.NORTH,
      BlockFace.EAST,
      BlockFace.SOUTH,
      BlockFace.WEST,
    )

    val mod = Vector3f()

    var closestSq: Float = Float.MAX_VALUE
    val closest = Vector3f()

    for (direction in directions) {
      mod.x = direction.modX.toFloat()
      mod.z = direction.modZ.toFloat()
      mod.normalize()

      val distSq = mod.distanceSquared(moveDir)

      if (distSq >= closestSq) {
        continue
      }

      closest.set(mod)
      closestSq = distSq
    }

    moveDir.set(mod)
    moveDir.mul(-1f)
  }
}