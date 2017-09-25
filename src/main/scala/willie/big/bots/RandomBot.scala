package willie.big.bots

import willie.big.models.{Direction, Grid, Move}

class RandomBot(myId: Int) extends Bot {
  override def moves(grid: Grid): Iterable[Move] = {
    for {
      site <- grid.mine
    } yield Move(site.x, site.y, Direction.getRandomDir)
  }
}