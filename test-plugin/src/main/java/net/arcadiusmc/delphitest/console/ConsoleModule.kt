package net.arcadiusmc.delphitest.console

import net.arcadiusmc.delphi.resource.ApiModule
import net.arcadiusmc.delphi.resource.DocumentContext
import net.arcadiusmc.delphi.resource.ResourcePath
import net.arcadiusmc.delphi.util.Result
import net.arcadiusmc.dom.ButtonElement
import net.arcadiusmc.dom.Document
import net.arcadiusmc.dom.InputElement
import java.util.*

class ConsoleModule: ApiModule {
  override fun getModulePaths(pathSoFar: ResourcePath): MutableCollection<String> {
    return Collections.emptySet()
  }

  override fun loadDocument(path: ResourcePath, ctx: DocumentContext): Result<Document, String> {
    val doc = ctx.newDocument()
    val body = doc.body

    val outputDiv = doc.createElement("div")
    outputDiv.className = "contentbox"

    val inputBox = doc.createElement("div")
    inputBox.className = "inputbox"

    val cmdInput = doc.createElement("input") as InputElement
    cmdInput.setPlaceholder("Enter command...")
    cmdInput.className = "cmdinput"

    val submitBtn = doc.createElement("button") as ButtonElement
    submitBtn.className = "submitbtn"

    val sendSpan = doc.createElement("span")
    sendSpan.textContent = "Send"

    submitBtn.appendChild(sendSpan)

    inputBox.appendChild(cmdInput)
    inputBox.appendChild(submitBtn)

    body.appendChild(outputDiv)
    body.appendChild(inputBox)

    val console = DelphiConsole(doc, outputDiv, cmdInput, submitBtn)
    console.setup()

    val sheet = ctx.parseStylesheet("""
.contentbox {
  font-size: 30%;
  height: 88%;
  box-sizing: border-box;
}
.inputbox {
  box-sizing: border-box;
  height: 12%;
  margin-left: auto;
  margin-right: auto;
  width: auto;
}
.cmdinput {
  display: inline;
  width: 80%;
  box-sizing: border-box;
}
.submitbtn {
  display: inline;
  width: 17%;
  box-sizing: border-box;
  span {
    display: block;
    margin-left: auto;
    margin-right: auto;
  }
}""")

    doc.addStylesheet(sheet)

    return Result.ok(doc)
  }
}