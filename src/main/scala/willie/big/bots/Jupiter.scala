package willie.big.bots

import willie.big.models.{Grid, Move, Site, Still}

import scala.collection.mutable.PriorityQueue

class Jupiter(myId: Int) extends Bot {
  override def moves(grid: Grid): Iterable[Move] = {
    scoreMySites(grid)
  }

  private def scoreMySites(grid: Grid) = {
    val ordering = Ordering.by[Site, Int](site => site.score)
    val pqueue = new PriorityQueue[Site]()(ordering)
    grid.borderSites.foreach(site => {
      site.score = siteScore(site)
      pqueue.enqueue(site)
    })
    scoreMySitesRec(grid, pqueue)
  }

  private def scoreMySitesRec(grid: Grid, pqueue: PriorityQueue[Site]): Iterable[Move] = {
    if (pqueue.isEmpty)
      directions(grid)
    else {
      val bestSite = pqueue.dequeue()
      grid.myNeighbors(bestSite.x, bestSite.y)
          .filter(neighbor => neighbor.site.score == Int.MinValue)
          .foreach(neighbor => {
            neighbor.site.score = bestSite.score - neighbor.site.production - 2
            pqueue.enqueue(neighbor.site)
      })
      scoreMySitesRec(grid, pqueue)
    }
  }

  private def directions(grid: Grid) = {
    grid.mine.map { site =>
      val bestNeighbor = grid.neighbors(site.x, site.y).maxBy(neighbor => neighbor.site.score)
      if (site.strength > 6 * site.production)
        Move(site.x, site.y, bestNeighbor.direction)
      else
        Move(site.x, site.y, Still)
    }
  }

  private def siteScore(site: Site) = (site.production.asInstanceOf[Double] * 5 - (site.strength.asInstanceOf[Double] * 7/10) + 50).asInstanceOf[Int]
}

