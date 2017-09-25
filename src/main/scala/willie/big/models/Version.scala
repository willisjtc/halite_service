package willie.big.models

import willie.big.bots._

class Version(version: String) {

  def bots(id: Int): Map[String, Bot] = {
    if (version.equals("1")) {
      Map("mars" -> new Mars(id),
          "random" -> new RandomBot(id),
          "jupiter" -> new Jupiter(id),
          "jupiterV2" -> new JupiterV2(id))
    } else {
      Map()
    }
  }
}
