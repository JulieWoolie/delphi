type GenerationType = "getimpl" | "get" | "set" | "setimpl"

const INDENT_STR = "  "
const GENERATE_IMPL = false

interface StylePropertyDefinition {
  javaType: string | "rect"
}

type TypeTable = {[key: string]: StylePropertyDefinition; }

const PROPERTIES: TypeTable = {
  Color: {
    javaType: "Color",
  },
  BackgroundColor: {
    javaType: "Color",
  },
  BorderColor: {
    javaType: "Color",
  },
  OutlineColor: {
    javaType: "Color",
  },
  TextShadow: {
    javaType: "Boolean",
  },
  Bold: {
    javaType: "Boolean",
  },
  Italic: {
    javaType: "Boolean",
  },
  Underlined: {
    javaType: "Boolean",
  },
  Strikethrough: {
    javaType: "Boolean",
  },
  Obfuscated: {
    javaType: "Boolean"
  },
  Display: {
    javaType: "DisplayType",
  },
  Scale: {
    javaType: "Primitive",
  },
  MaxWidth: {
    javaType: "Primitive",
  },
  MinWidth: {
    javaType: "Primitive",
  },
  MaxHeight: {
    javaType: "Primitive",
  },
  MinHeight: {
    javaType: "Primitive",
  },
  Padding: {
    javaType: "rect",
  },
  Outline: {
    javaType: "rect",
  },
  Border: {
    javaType: "rect",
  },
  Margin: {
    javaType: "rect",
  },
  ZIndex: {
    javaType: "Integer"
  },
  Scale: {
    javaType: "Float"
  },
  AlignItems: {
    javaType: "AlignItems"
  },
  FlexDirection: {
    javaType: "FlexDirection"
  },
  FlexWrap: {
    javaType: "FlexWrap"
  },
  JustifyContent: {
    javaType: "JustifyContent"
  },
  Order: {
    javaType: "Integer"
  }
}

const rectangleSides: string[] = [
  "Top",
  "Right",
  "Bottom",
  "Left"
]

class StringBuilder {
  private out: string = ""

  lineCount: number = 0
  indent: number = 0

  constructor() {

  }

  nlAppend(input: any): StringBuilder {
    return this.nlIndent().append(input)
  }

  nlIndent(): StringBuilder {
    this.out += "\n"
    this.out += INDENT_STR.repeat(this.indent)
    this.lineCount++
    return this
  }

  nl(): StringBuilder {
    this.out += "\n"
    this.lineCount++
    return this
  }

  append(input: any): StringBuilder {
    this.out += String(input)
    return this
  }

  toString(): string {
    return this.out
  }
}

let builder = new StringBuilder()

builder.nlAppend("import javax.annotation.Nullable;")
builder.nlAppend("import net.arcadiusmc.dom.ParserException;")
builder.nl()
builder.nlAppend("public ")

let className: string

if (GENERATE_IMPL) {
  className = "PropertiesMap"
  builder.append("class ")
  builder.append(className)
  builder.append(" extends ReadonlyProperties implements StyleProperties")
} else {
  className = "StyleProperties"
  builder.append("interface ")
  builder.append(className)
  builder.append(" extends StylePropertiesReadonly")
}

builder.append(" {")
builder.indent++

appendProperties()

builder.indent--
builder.nlIndent()
builder.append("}\n\n")

console.log(builder.toString())
console.log(`Generated ${builder.lineCount} lines of code`)

function appendProperties(): void {
  for (let key in PROPERTIES) {
    let typeDef: StylePropertyDefinition = PROPERTIES[key]!
    appendTypeDef(key, typeDef, "String", true)

    let jType: string = typeDef.javaType
    if (jType == "String") {
      continue
    }

    if (jType == "rect") {
      //appendTypeDef(key, typeDef, "Primitive")
      appendRectTypes(key, typeDef)
    } else {
      appendTypeDef(key, typeDef, jType)
    }
  }
}

function appendRectTypes(key: string, typeDef: StylePropertyDefinition): void {
  for (let argCount = 1; argCount < 5; argCount++) {
    let name = toPropertyName(key)

    builder.nl()

    if (!GENERATE_IMPL) {
      builder
        .nlAppend("/**")
        .nlAppend(` * Set the {@code ${name.cssKey}} property.`)
    }
    
    let argumentNames: string[]

    if (argCount == 1) {
      argumentNames = ["value"]
    } else if (argCount == 2) {
      argumentNames = ["x", "y"]
    } else if (argCount == 3) {
      argumentNames = ["top", "x", "bottom"]
    } else {
      argumentNames = ["top", "right", "bottom", "left"]
    }

    if (!GENERATE_IMPL) {
      argumentNames.forEach(arg => {
        builder.nlAppend(` * @param ${arg} ${arg} ${name.cssKey}`)
      })

      builder
        .nlAppend(" * @return {@code this}")
        .nlAppend(` */`)
    }

    builder.nlIndent()

    if (GENERATE_IMPL) {
      builder.append("@Override")
      builder.nlAppend("public ")
    }

    builder.append(`${className} set${key}(`)

    let count = 0
    argumentNames.forEach(arg => {
      if (count > 0) {
        builder.append(", ")
      }

      builder.append(`Primitive ${arg}`)
      count++
    })

    builder.append(")")

    if (GENERATE_IMPL) {
      builder.append(" {")
      builder.indent++

      let joined = argumentNames.join(", ")
      
      builder.nlAppend(`set(Properties.${name.constKey}, PrimitiveRect.create(${joined}));`)
      builder.nlAppend("return triggerChange();")

      builder.indent--
      builder.nlAppend("}")
    } else {
      builder.append(";")
    }
  }

  rectangleSides.forEach(side => {
    appendRectSide(key, typeDef, side, true)
    appendRectSide(key, typeDef, side, false)
  })
}

function appendRectSide(
  key: string, 
  typeDef: StylePropertyDefinition, 
  side: string,
  stringType: boolean
): void {
  let str: string
  if (stringType) {
    str = "String"
  } else {
    str = "Primitive"
  }

  let def: StylePropertyDefinition = {
    javaType: str
  }

  appendTypeDef(key + side, def, str, stringType)
}


function appendTypeDef(
  key: string, 
  typeDef: StylePropertyDefinition, 
  paramType: string, 
  includeParserWarning?: boolean
): void {
  builder.nl()
  let name = toPropertyName(key)

  if (!GENERATE_IMPL) {
    builder
      .nlAppend("/**")
      .nlAppend(` * Set the {@code ${name.cssKey}} property.`)
      .nlAppend(` * @param value Value`)
      .nlAppend(` * @return {@code this}`)

    if (includeParserWarning) {
      builder.nlAppend(" * @throws ParserException If a syntax exception occurred")
    }

    builder.nlAppend(" */")
  }

  builder.nlIndent()

  if (GENERATE_IMPL) {
    builder.append("@Override")
    builder.nlAppend("public ")
  }

  builder.append(`${className} set${key}(@Nullable ${paramType} value)`)

  if (GENERATE_IMPL) {
    builder.append(" {")
    builder.indent++

    if (includeParserWarning) {
      builder.nlAppend(`parse`)
    } else {
      builder.nlAppend(`set`)
    }

    builder.append(`(Properties.${name.constKey}, value);`)

    builder.nlAppend("return triggerChange();")

    builder.indent--
    builder.nlAppend("}")
  } else {
    if (includeParserWarning) {
      builder.append(" throws ParserException")
    }

    builder.append(";")
  }
}

interface PropertyName {
  constKey: string
  cssKey: string
}

function toPropertyName(key: string): PropertyName {
  let constKey = ""
  let cssKey = ""

  let count = 0

  for (let ch of key) {
    if (ch == ch.toUpperCase() && count > 0) {
      constKey += "_"
      constKey += ch

      cssKey += "-"
      cssKey += ch.toLowerCase()

      count++
      continue
    }

    constKey += ch.toUpperCase()
    cssKey += ch.toLowerCase()

    count++
  }
  
  return {constKey: constKey, cssKey: cssKey}
}