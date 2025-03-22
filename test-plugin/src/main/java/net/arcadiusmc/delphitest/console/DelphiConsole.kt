package net.arcadiusmc.delphitest.console

import com.google.common.base.Strings
import net.arcadiusmc.delphitest.DelphiTestPlugin
import net.arcadiusmc.dom.ButtonElement
import net.arcadiusmc.dom.Document
import net.arcadiusmc.dom.Element
import net.arcadiusmc.dom.InputElement
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


const val MAX_LINES = 25

class DelphiConsole(
  val doc: Document,
  val outputDiv: Element,
  val inputEl: InputElement,
  val submitBtn: ButtonElement,
) {

  var appender: LogListener? = null

  fun setup() {
    registerListener()

    submitBtn.onClick {
      onSubmit()
    }

    doc.onClosing {
      removeListener()
    }
  }

  fun onSubmit() {
    val cmd = inputEl.value
    if (Strings.isNullOrEmpty(cmd)) {
      return
    }

    inputEl.value = null
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd!!)
  }

  fun registerListener() {
    appender = LogListener(toString(), this)

    val ctx = LoggerContext.getContext(false)
    val cfg = ctx.configuration
    val root = cfg.rootLogger

    root.addAppender(appender!!, Level.INFO, null)
    ctx.updateLoggers()
  }

  fun removeListener() {
    val ctx = LoggerContext.getContext(false)
    val cfg = ctx.configuration
    val root = cfg.rootLogger

    root.removeAppender(appender!!.name)
    ctx.updateLoggers()

    appender = null
  }

  fun appendLogLine(line: String, color: String = "") {
    if (outputDiv.childCount >= MAX_LINES) {
      outputDiv.removeChild(0)
    }

    val div = doc.createElement("div")
    div.textContent = line

    if (!Strings.isNullOrEmpty(color)) {
      div.inlineStyle.color = color
    }

    outputDiv.appendChild(div)
  }
}

val TIME_FORMAT = SimpleDateFormat("HH:mm:ss")
val shortNameFormats = listOf(
  "",
  "net.minecraft.",
  "Minecraft",
  "com.mojang.",
  "com.sk89q.",
  "ru.tehkode.",
  "Minecraft.AWE"
)
const val ANSI_ESCAPE = "\u001B"
val ANSI_REGEX = Pattern.compile("$ANSI_ESCAPE\\[([0-9]+)m")

class LogListener(name: String, val console: DelphiConsole):
  AbstractAppender(name, null, null, false, null)
{

  init {
    setStarted()
  }

  private fun removeColorCodes(msg: String): String {
    if (!msg.contains(ANSI_ESCAPE)) {
      return msg
    }

    val builder = StringBuilder()
    val matcher = ANSI_REGEX.matcher(msg)

    while (matcher.find()) {
      matcher.appendReplacement(builder, "")
    }
    matcher.appendTail(builder)

    return builder.toString()
  }

  override fun append(event: LogEvent) {
    val date = Date(event.instant.epochMillisecond)
    val timeFormat = TIME_FORMAT.format(date)
    val levelName = event.level.name().padStart(5)
    val loggerName = event.loggerName
    val message = removeColorCodes(event.message.formattedMessage)

    var usesShortFormat = false
    for (str in shortNameFormats) {
      if (str == loggerName || loggerName.contains(str)) {
        usesShortFormat = true
        break
      }
    }

    val color = when (event.level) {
      Level.ERROR -> "red"
      Level.WARN -> "yellow"
      else -> ""
    }

    val scheduler = Bukkit.getScheduler()
    val pl = JavaPlugin.getPlugin(DelphiTestPlugin::class.java)

    if (!pl.isEnabled) {
      return
    }

    val builder = StringBuilder()
    builder.append("[$timeFormat $levelName] ")

    if (!usesShortFormat) {
      builder.append("[${loggerName}] ")
    }

    builder.append(message)

    scheduler.runTask(
      pl,
      Runnable { console.appendLogLine(builder.toString(), color) }
    )

  }
}