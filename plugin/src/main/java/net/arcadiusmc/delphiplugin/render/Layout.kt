package net.arcadiusmc.delphiplugin.render

import net.arcadiusmc.delphiplugin.math.Rectangle
import net.arcadiusmc.dom.style.DisplayType
import org.joml.Vector2f
import kotlin.math.max
import kotlin.math.min

class RenderLine {
  val objects: MutableList<RenderObject> = ArrayList()
  val size: Vector2f = Vector2f()
  var bottomMargin: Float = 0.0f
}

fun layout(el: ElementRenderObject) {
  if (el.childObjects.isEmpty()) {
    return
  }

  for (childObject in el.childObjects) {
    if (childObject !is ElementRenderObject || childObject.isHidden) {
      continue
    }

    layout(childObject)
  }

  if (el.style.display == DisplayType.FLEX) {
    layoutFlex(el)
  } else {
    layoutFlow(el)
  }

  postAlign(el)
}

fun postAlign(el: ElementRenderObject) {
  if (el.childObjects.isEmpty()) {
    return
  }

  val bottomRight = Vector2f(Float.MIN_VALUE, Float.MAX_VALUE)
  val childMax = Vector2f()

  val rectangle = Rectangle()

  for (child in el.childObjects) {
    child.getBounds(rectangle)

    childMax.x = rectangle.getPosition().x + rectangle.getSize().x
    childMax.y = rectangle.getPosition().y

    bottomRight.x = max(childMax.x, bottomRight.x)
    bottomRight.y = min(childMax.y, bottomRight.y)
  }

  val contentStart = Vector2f()
  el.getContentStart(contentStart)

  val difX = max(bottomRight.x - contentStart.x, 0.0f)
  val difY = max(contentStart.y - bottomRight.y, 0.0f)

  el.contentSize.set(difX, difY)
  el.spawn()
}

fun layoutFlow(el: ElementRenderObject) {
  val lines: List<RenderLine> = splitIntoLines(el)

  val start: Vector2f = Vector2f()
  val off: Vector2f = Vector2f()
  val childSize: Vector2f = Vector2f()
  val childPosition: Vector2f = Vector2f()

  el.getContentStart(start)

  for (line in lines) {
    for (obj in line.objects) {
      obj.getElementSize(childSize)

      val margin = obj.style.margin

      childPosition.set(start)
      childPosition.x += off.x
      childPosition.x += margin.left

      val heightOff = line.size.y - childSize.y
      childPosition.y -= heightOff
      childPosition.y -= off.y

      obj.moveTo(childPosition)

      off.x += childSize.x + margin.left + margin.right
    }

    off.x = 0.0f
    off.y += (line.bottomMargin + line.size.y)
  }
}

fun layoutFlex(el: ElementRenderObject) {
  el.sortChildren()

  val maxSize: Vector2f = Vector2f()
  maxSize(el, maxSize)


}

fun splitIntoLines(el: ElementRenderObject): List<RenderLine> {
  val lines: MutableList<RenderLine> = ArrayList()
  var line: RenderLine? = null

  val childSize = Vector2f()

  val maxSize = Vector2f()
  maxSize(el, maxSize)

  for (child in el.childObjects) {
    if (child.isHidden) {
      continue
    }
    if (line == null) {
      line = RenderLine()
    }

    child.getElementSize(childSize)
    val style = child.style

    val newWidth = (childSize.x + line.size.x + style.margin.left + style.margin.right)
    val display = child.style.display

    if (newWidth > maxSize.x || (display != DisplayType.INLINE && !child.ignoreDisplay())) {
      if (line.objects.isNotEmpty()) {
        lines.add(line)
      }

      line = RenderLine()

      if (display == DisplayType.BLOCK || display == DisplayType.FLEX) {
        line.objects.add(child)
        line.size.set(childSize)
        line.bottomMargin = style.margin.bottom
        lines.add(line)

        line = null
        continue
      }
    }

    line.size.x += childSize.x + style.margin.left + style.margin.right
    line.size.y = max(line.size.y, childSize.y)
    line.bottomMargin = max(line.bottomMargin, style.margin.bottom)

    line.objects.add(child)
  }

  if (line != null) {
    lines.add(line)
  }

  return lines
}

fun maxSize(el: ElementRenderObject, out: Vector2f) {
  if (el.parent != null) {
    maxSize(el.parent, out)
  } else {
    el.screen.getDimensions(out)
  }

  val xAdd = el.boxWidthIncrease()
  val yAdd = el.boxHeightIncrease()

  out.x -= xAdd
  out.y -= yAdd

  el.clamp(out)
}