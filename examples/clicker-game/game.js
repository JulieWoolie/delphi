
class ValueAndElement {
  _value = 0.0
  _element = null
  suffix = ""
  round = false

  listeners = []

  set value(v) {
    this._value = v
    this.updateElementContent()

    for (const cb of this.listeners) {
      cb(v, this)
    }
  }

  get value() {
    return this._value
  }

  set element(el) {
    this._element = el
    this.updateElementContent()
  }

  onchange(cb) {
    this.listeners.push(cb)
  }

  increment(value = 1) {
    if (value == 0) {
      return
    }

    this.value = this._value + value
  }

  updateElementContent() {
    if (!this._element) {
      return
    }

    let numValue;

    if (this.round) {
      numValue = Math.floor(this._value)
    } else {
      numValue = this._value.toFixed(2)
    }

    this._element.setTextContent(`${numValue}${this.suffix}`)
  }
}

class UpgradeComponent {
  schema = {}
  tier = 0
  priceElement = null
  buttonElement = null

  get fullPrice() {
    return Math.floor((this.tier * this.schema.price * 0.5) + this.schema.price)
  }

  onAttemptPurchase() {
    const price = this.fullPrice
    if (score.value < price) {
      return
    }

    this.tier++

    cps.increment(this.schema.cps)
    score.increment(-price)

    if (this.priceElement) {
      this.priceElement.setTextContent(`Price: ${this.fullPrice}`)
    }
  }

  onScoreChange(v) {
    const fullPrice = this.fullPrice

    if (!this.buttonElement) {
      return
    }

    if (v < fullPrice) {
      this.buttonElement.setEnabled(false)
    } else {
      this.buttonElement.setEnabled(true)
    }
  }
}

const purchaseOptions = {
  cps1: {
    title: "+0.25 cps",
    price: 10,
    cps: 0.25
  },
  cps2: {
    title: "+1.0 cps",
    price: 25,
    cps: 1
  },
  cps3: {
    title: "+5.0 cps",
    price: 50,
    cps: 5
  },
  cps4: {
    title: "+10.0 cps",
    price: 75,
    cps: 10
  },
}

const body = document.getBody()

let score = new ValueAndElement()
let cps = new ValueAndElement()

score.suffix = " Clicks"
cps.suffix = " clicks per second"

initGame()

setInterval(() => {
  const deltaTime = 1.0 / 20
  const value = cps.value
  const dtValue = value * deltaTime
  score.increment(dtValue)
}, 1, 1)

function initGame() {
  const scoreEl = document.createElement("h1")
  scoreEl.setClassName("counter");

  const cpsEl = document.createElement("span")
  cpsEl.setClassName("cps")

  body.appendChild(scoreEl)
  body.appendChild(cpsEl)

  score.element = scoreEl
  cps.element = cpsEl

  const counterBtn = document.createElement("button")
  counterBtn.setClassName("btn")
  counterBtn.setTextContent("Click me!")
  counterBtn.onClick(ev => score.increment())

  body.appendChild(counterBtn)

  if (view.getPath().getQuery("debug") == "true") {
    const debugBtn = document.createElement("button")
    debugBtn.setTextContent("+10 cps")
    debugBtn.setClassName("btn")
    debugBtn.onClick(ev => {
      cps.increment(10)
    })
    body.appendChild(debugBtn)
  }

  initUpgrades()

  let closebtn = document.createElement("button")
  closebtn.onClick(ev => view.close())
  closebtn.setTextContent("Close")
  closebtn.setClassName("btn")
  body.appendChild(closebtn)
}

function initUpgrades() {
  const purchaseContainer = document.createElement("div")
  purchaseContainer.setClassName("upgrade-container");

  let currentLine = null
  let upgradeIdx = 0
  
  for (const key in purchaseOptions) {
    if (currentLine == null) {
      currentLine = document.createElement("div")
      currentLine.setClassName("upgr-line")
    }

    const schema = purchaseOptions[key]

    const comp = new UpgradeComponent()
    comp.schema = schema
    comp.tier = 0

    let element = document.createElement("button")
    let titleEl = document.createElement("div")
    let priceEl = document.createElement("div")

    comp.buttonElement = element
    comp.priceElement = priceEl

    element.setClassName("upgr-btn")
    titleEl.setClassName("upgr-title")
    priceEl.setClassName("upgr-price")

    element.appendChild(titleEl)
    element.appendChild(priceEl)

    titleEl.setTextContent(schema.title)
    priceEl.setTextContent(`Price: ${schema.price}`)

    element.onClick(() => comp.onAttemptPurchase())
    score.onchange((v) => comp.onScoreChange(v))

    currentLine.appendChild(element)
    upgradeIdx++

    if (upgradeIdx % 2 == 0) {
      purchaseContainer.appendChild(currentLine)
      currentLine = null
    }
  }

  if (currentLine != null) {
    purchaseContainer.appendChild(currentLine)
    currentLine = null
  }

  body.appendChild(purchaseContainer)
}
