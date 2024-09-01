let generateImpl = true

const types: string[] = [
  "Padding",
  "Border",
  "Outline",
  "Margin"
]

const edges: string[] = [
  "Top",
  "Right",
  "Bottom",
  "Left"
]

const indent = "    "

let outp: string = ""

for (let t of types) {
  let lowT = t.toLowerCase()

  for (let edge of edges) {
    if (!generateImpl) {
      outp += `
    /**
     * Set the ${edge.toLowerCase()} ${lowT}.
     * @param value ${edge.toLowerCase()} ${lowT}
     * @return {@code this}
     */`
    } else {
      outp += `

    @Override`
    }

    outp += "\n    "

    if (generateImpl) {
      outp += "public "
    }

    outp += `StyleProperties set${t}${edge}(@Nullable String value)`

    if (!generateImpl) {
      outp += ";"
    } else {
      outp += ` {
      set(Properties.${t.toUpperCase()}_${edge.toUpperCase()}, value);
      return signalChanged();
    }`
    }
  }

  outp += "\n"

  for (let i = 1; i < 5; i++) {
    if (i > 1) {
      outp += "\n"
    }

    if (!generateImpl) {
      outp += `
    /**
     * Sets the ${lowT}.
     *`

    if (i == 1) {
      outp += `
     * @param value ${t}, applied to all sides`
    } else if (i == 2) {
      outp += `
     * @param x left and right ${lowT}
     * @param y top and bottom ${lowT}`
    } else if (i == 3) {
      outp += `
     * @param top top ${lowT}
     * @param x left and right ${lowT}
     * @param bottom bottom ${lowT}`
    } else {
      outp += `
     * @param top top ${lowT}
     * @param right right side ${lowT}
     * @param left left side ${lowT}
     * @param bottom bottom ${lowT}`
    }

    outp += `
     *
     * @return {@code this}.
     */`
    } else {
      outp += "\n    @Override"
    }

    generateShorthand(i, t)
  }
}

console.log(outp)

function generateShorthand(paramCount: number = 1, t: string) {
  outp += "\n"
  outp += indent

  if (generateImpl) {
    outp += 'public'
  } else {
    outp += 'default'
  }

  outp += ` StyleProperties set${t}(`

  let pnames: string[]
  let edgeIdx: number[]

  if (paramCount == 1) {
    pnames = ["value"]
    edgeIdx = [0,0,0,0]
  } else if (paramCount == 2) {
    pnames = ["x", "y"]
    edgeIdx = [1,0,1,0]
  } else if (paramCount == 3) {
    pnames = ["top", "x", "bottom"]
    edgeIdx = [0,1,2,1]
  } else {
    pnames = ["top", "right", "bottom", "left"]
    edgeIdx = [0,1,2,3]
  }

  let printed = false
  for (let str of pnames) {
    if (printed) {
      outp += ", "
    }

    outp += `Primitive ${str}`
    printed = true
  }

  if (!generateImpl) {
    outp += ");\n"
    return
  }

  outp += ") {\n"
  outp += indent
  outp += indent
  
  if (!generateImpl) {
    outp += "return this"
  }

  printed = false

  for (let idx = 0; idx < edges.length; idx++) {
    if (printed) {
      outp += "\n"
      outp += indent
      outp += indent
      
      if (!generateImpl) {
        outp += indent
      }
    }

    let edge = edges[idx]!
    let edgeparam = pnames[edgeIdx[idx]]!

    if (generateImpl) {
      outp += `set(Properties.${t.toUpperCase()}_${edge.toUpperCase()}, ${edgeparam});`
    } else {
      outp += `.set${t}${edge}(${edgeparam})`
    }

    printed = true
  }

  if (!generateImpl) {
    outp += ";"
  } else {
    outp += "\n"
    outp += indent
    outp += indent
    outp += "return signalChanged();"
  }

  outp += "\n"
  outp += indent
  outp += "}"
}

let nlcount: number = 0
let last: number = 0

while (1) {
  let idx = outp.indexOf("\n", last)

  if (idx == -1) {
    break
  }

  nlcount++
  last = idx + 1
}

console.log(`Generated ${nlcount} lines`)