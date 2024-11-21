package net.arcadiusmc.delphiplugin.render

import net.arcadiusmc.chimera.ComputedStyleSet
import net.arcadiusmc.chimera.ValueOrAuto
import net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE
import net.arcadiusmc.delphidom.Consts.LEN0_PX
import net.arcadiusmc.delphidom.Loggers
import net.arcadiusmc.delphidom.Rect
import net.arcadiusmc.delphiplugin.math.Rectangle
import net.arcadiusmc.dom.style.*
import org.joml.Vector2f
import java.util.*
import kotlin.math.max
import kotlin.math.min

class RenderLine {
  val objects: MutableList<RenderObject> = ArrayList()
  val size: Vector2f = Vector2f()
  var bottomMargin: Float = 0.0f
}

val LOGGER = Loggers.getDocumentLogger()

fun measure(el: ElementRenderObject) {
  val queue: Deque<RenderObject> = ArrayDeque()
  queue.addLast(el)
  addToQueue(queue, el)

  val parentSize: Vector2f = Vector2f()
  val screenSize: Vector2f = el.system.view.screen.dimensions

  fun computeRect(
    rect: Rect,
    t: ValueOrAuto,
    r: ValueOrAuto,
    b: ValueOrAuto,
    l: ValueOrAuto
  ) {
    rect.top = computePrimitive(t, parentSize.y, screenSize) ?: 0f
    rect.right = computePrimitive(r, parentSize.x, screenSize) ?: 0f
    rect.bottom = computePrimitive(b, parentSize.y, screenSize) ?: 0f
    rect.left = computePrimitive(l, parentSize.x, screenSize) ?: 0f
  }

  while (queue.isNotEmpty()) {
    val e: RenderObject = queue.pollFirst()

    if (e.parent == null) {
      parentSize.set(screenSize)
    } else {
      val p = e.parent
      p.measureContent(parentSize)
      p.clamp(parentSize)
    }

    val out: FullStyle = e.style
    val set: ComputedStyleSet = e.styleSet

    applyStandardProperties(out, set)

    computeRect(out.margin, set.marginTop, set.marginRight, set.marginBottom, set.marginLeft)
    computeRect(out.padding, set.paddingTop, set.paddingRight, set.paddingBottom, set.paddingLeft)
    computeRect(out.border, set.borderTop, set.borderRight, set.borderBottom, set.borderLeft)
    computeRect(out.outline, set.outlineTop, set.outlineRight, set.outlineBottom, set.outlineLeft)

    out.maxSize.x = computePrimitive(set.maxWidth, parentSize.x, screenSize) ?: Float.MAX_VALUE
    out.maxSize.y = computePrimitive(set.maxHeight, parentSize.y, screenSize) ?: Float.MAX_VALUE
    out.minSize.x = computePrimitive(set.minWidth, parentSize.x, screenSize) ?: Float.MIN_VALUE
    out.minSize.y = computePrimitive(set.minHeight, parentSize.y, screenSize) ?: Float.MIN_VALUE
    out.setSize.x = computePrimitive(set.width, parentSize.x, screenSize) ?: 0f
    out.setSize.y = computePrimitive(set.height, parentSize.y, screenSize) ?: 0f
    out.scale.x = computePrimitive(set.scaleX, parentSize.x, screenSize) ?: 1f
    out.scale.y = computePrimitive(set.scaleY, parentSize.y, screenSize) ?: 1f
  }
}

fun computePrimitive(v: ValueOrAuto, parentSize: Float, screenSize: Vector2f): Float? {
  if (v.isAuto) {
    return null
  }

  val prim: Primitive = v.primitive
  val value: Float = prim.value
  val percent: Float = value * 0.01f;

  return when (prim.unit) {
    Primitive.Unit.NONE -> value
    Primitive.Unit.PX -> value * CHAR_PX_SIZE
    Primitive.Unit.CH -> value * LEN0_PX
    Primitive.Unit.VW -> (percent) * screenSize.x
    Primitive.Unit.VH -> (percent) * screenSize.y
    Primitive.Unit.M -> value
    Primitive.Unit.CM -> percent
    Primitive.Unit.PERCENT -> percent * parentSize
    else -> null
  }
}

fun applyStandardProperties(out: FullStyle, set: ComputedStyleSet) {
  // Colors
  out.textColor = FullStyle.toTextColor(set.color)
  out.backgroundColor = FullStyle.toBukkitColor(set.backgroundColor)
  out.borderColor = FullStyle.toBukkitColor(set.borderColor)
  out.outlineColor = FullStyle.toBukkitColor(set.outlineColor)

  // Text options
  out.textShadowed = set.textShadow
  out.bold = set.bold
  out.italic = set.italic
  out.underlined = set.underlined
  out.strikethrough = set.strikethrough
  out.obfuscated = set.obfuscated

  out.display = set.display
  out.zindex = set.zindex
  out.alignItems = set.alignItems
  out.flexDirection = set.flexDirection
  out.flexWrap = set.flexWrap
  out.justify = set.justifyContent
  out.order = set.order
}

fun addToQueue(queue: Deque<RenderObject>, el: ElementRenderObject) {
  for (childObject in el.childObjects) {
    queue.addLast(childObject)
  }

  for (childObject in el.childObjects) {
    if (childObject !is ElementRenderObject) {
      continue
    }

    addToQueue(queue, childObject)
  }
}

fun layout(el: ElementRenderObject) {
  measure(el)
  layoutInternal(el)
}

private fun layoutInternal(el: ElementRenderObject) {
  if (el.childObjects.isEmpty()) {
    return
  }

  for (childObject in el.childObjects) {
    if (childObject !is ElementRenderObject || childObject.isHidden) {
      continue
    }

    layoutInternal(childObject)
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

    childMax.x = rectangle.position.x + rectangle.size.x
    childMax.y = rectangle.position.y

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

  val style = el.style

  // Content alignment along the Flex box's axis (column or row)
  val justify = style.justify

  // Content alignment along the Flex box's cross axis
  val alignItems = style.alignItems

  // Direction the content is aligned in,
  val flexDirection: FlexDirection = style.flexDirection
  val wrap: FlexWrap = style.flexWrap

  // Notes:
  // For now, ignore margins, implement them later, just get one or two of the alignment modes
  // functioning.
  //
  //
  //

  val direction: Vector2f = Vector2f()
  val crossDirection: Vector2f = Vector2f()

  // TODO: Add support for column direction
  if (flexDirection != FlexDirection.ROW && flexDirection != FlexDirection.ROW_REVERSE) {
    layoutFlow(el)
    return
  }

  // TODO
  if (justify != JustifyContent.CENTER
      && justify != JustifyContent.FLEX_END
      && justify != JustifyContent.FLEX_START
  ) {
    layoutFlow(el)
    return
  }

  if (alignItems != AlignItems.FLEX_START) {
    layoutFlow(el)
    return
  }

  val lines: MutableList<RenderLine>

  if (wrap == FlexWrap.NOWRAP) {
    lines = ArrayList()

    val line = RenderLine()
    val childSize = Vector2f()
    line.objects.addAll(el.childObjects)

    lines.add(line)

    for (childObject in el.childObjects) {
      val style = childObject.style
      line.bottomMargin = max(style.margin.bottom, line.bottomMargin)

      childObject.getElementSize(childSize)
      line.size.x += style.margin.left + style.margin.right + childSize.x
      line.size.y = max(line.size.y, childSize.y + style.margin.top)
    }
  } else {
    lines = splitIntoLines(el)

    if (wrap == FlexWrap.WRAP_REVERSE) {
      lines.reverse()
    }
  }

  if (flexDirection == FlexDirection.ROW_REVERSE) {
    for (line in lines) {
      line.objects.reverse()
    }
  }

  val start: Vector2f = Vector2f()
  val childPos: Vector2f = Vector2f()
  val childSize: Vector2f = Vector2f()
  val advance: Vector2f = Vector2f()

  el.getContentStart(start)

  for (line in lines) {
    val xDif = maxSize.x - line.size.x - el.style.padding.left

    if (justify == JustifyContent.CENTER) {
      advance.x = xDif * 0.5f;
    } else if (justify == JustifyContent.FLEX_END) {
      advance.x = xDif;
    } else {
      advance.x = 0.0f
    }

    var firstOnLine = true

    for (obj in line.objects) {
      obj.getElementSize(childSize)

      val leftMargin: Float

      if (firstOnLine) {
        leftMargin = 0.0f
      } else {
        leftMargin = obj.style.margin.left
      }

      firstOnLine = false

      childPos.set(start)
      childPos.x += advance.x
      childPos.x += leftMargin

      childPos.y -= advance.y

      obj.moveTo(childPos)

      advance.x += leftMargin + obj.style.margin.right + childSize.x
    }

    advance.y += line.size.y + line.bottomMargin
  }
}

fun splitIntoLines(el: ElementRenderObject): MutableList<RenderLine> {
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
        line.size.y += style.margin.top
        line.bottomMargin = style.margin.bottom
        lines.add(line)

        line = null
        continue
      }
    }

    line.size.x += childSize.x + style.margin.left + style.margin.right
    line.size.y = max(line.size.y, childSize.y + style.margin.top)
    line.bottomMargin = max(line.bottomMargin, style.margin.bottom)

    line.objects.add(child)
  }

  if (line != null) {
    lines.add(line)
  }

  return lines
}

fun maxSize(el: RenderObject, out: Vector2f) {
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