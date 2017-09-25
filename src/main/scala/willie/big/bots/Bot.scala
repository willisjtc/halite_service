package willie.big.bots

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import willie.big._
import willie.big.models.{Grid, Move}

trait Bot {
  def movesToString(moves: Iterable[Move]) = {
    val jsonMoves = moves.map(move => {
      val jsonMove = new JsonObject()
      val loc = new JsonObject()
      loc.put("x", move.x)
      loc.put("y", move.y)
      jsonMove.put("loc", loc)
      jsonMove.put("dir", move.direction.getValue)
      jsonMove
    })

    val jsonArray = new JsonArray()
    jsonMoves.foreach(jsonMove => {
      jsonArray.add(jsonMove)
    })
    jsonArray
  }

  def moves(grid: Grid): Iterable[Move]

  def name = getClass.getSimpleName
}

object Bot {
  def make(id: Int, name: String, version: String): Bot = {
    if (name.equals("mars")) {
      new Mars(id)
    } else if (name.equals("vibration")) {
      new Vibration(id)
    } else {
      new RandomBot(id)
    }
  }
}
