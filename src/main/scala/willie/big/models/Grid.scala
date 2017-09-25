package willie.big.models

import io.vertx.core.json.JsonArray

class Grid(val myId: Int, val width: Int, val height: Int, contents: JsonArray) {
  val sitesArray = Array.ofDim[Site](contents.size(), contents.getJsonArray(0).size())
  for (y <- 0 until contents.size) {
    for (x <- 0 until contents.getJsonArray(y).size()) {
      val gridPoint = contents.getJsonArray(y).getJsonObject(x)
      sitesArray(y)(x) = Site(x, y, gridPoint.getInteger(Grid.PRODUCTION), gridPoint.getInteger(Grid.OWNER), gridPoint.getInteger(Grid.STRENGTH), Int.MinValue)
    }
  }

  def site(x: Int, y: Int): Site = sitesArray(y)(x)

  val sites: Seq[Site] = sitesArray.toList.flatten

  val mine: Set[Site] = {
    (for {
      x <- 0 until width
      y <- 0 until height
      if sitesArray(y)(x).id == myId
    } yield site(x, y)).toSet
  }

  val borderSites: Set[Site] = {
    mine.flatMap(site => {
      neighbors(site.x, site.y).filter(neighbor => {
        neighbor.site.id == 0
      }).map(neighbor => neighbor.site)
    })
  }

  def distance(x1: Int, y1: Int, x2: Int, y2: Int): Int = {
    var dx = Math.abs(x1 - x2)
    var dy = Math.abs(y1 - y2)

    if (dx > width / 2.0) {
      dx = width - dx
    }

    if (dy > height / 2.0) {
      dy = height - dy
    }

    dx + dy
  }

  def neighbor(x: Int, y: Int, direction: Direction): Neighbor = {
    direction match {
      case North => Neighbor(site(x, if (y == 0) height - 1 else y - 1), North)
      case East => Neighbor(site(if (x == width - 1) 0 else x + 1, y), East)
      case South => Neighbor(site(x, if (y == height - 1) 0 else y + 1), South)
      case West => Neighbor(site(if (x == 0) width - 1 else x - 1, y), West)
      case Still => Neighbor(site(x, y), Still)
    }
  }

  def neighbors(x: Int, y: Int): Seq[Neighbor] = {
    Direction.CARDINALS map (d => neighbor(x, y, d))
  }

  def myNeighbors(x: Int, y: Int): Seq[Neighbor] = {
    neighbors(x, y).filter(_.site.id == myId)
  }

}

object Grid {
  val NEUTRAL = 0
  val OWNER = "owner"
  val PRODUCTION = "production"
  val STRENGTH = "strength"
}
