package willie.big.bots

import willie.big.models.{Grid, Move, Site, Still}

class Vibration(myId: Int) extends Bot {
  override def moves(grid: Grid): Iterable[Move] = {
    scoreMySites(grid)
  }

  private def scoreMySites(grid: Grid) = {
    grid.borderSites.foreach(site => site.score = siteScore(site, -100))
    scoreMySitesRec(grid, grid.borderSites, grid.borderSites, 0)
  }

  private def scoreMySitesRec(grid: Grid, borderingSites: Set[Site], totalScoredSites: Set[Site], depth: Int): Iterable[Move] = {
    val newBorderSites = borderingSites.flatMap(borderSite => {
      grid.neighbors(borderSite.x, borderSite.y)
          .filter(neighbor => neighbor.site.id == myId && neighbor.site.score == Int.MinValue)
          .map(neighbor => neighbor.site)
    })
    if (newBorderSites.isEmpty) {
      directions(grid, totalScoredSites)
    } else {
      newBorderSites.foreach(site => site.score = siteScore(site, depth))
      scoreMySitesRec(grid, newBorderSites, totalScoredSites ++ newBorderSites, depth + 1)
    }
  }

  private def directions(grid: Grid, scoredSites: Set[Site]) = {
    scoredSites.filter(site => site.id == myId)
               .map(site => {
                 val neighbor = grid.neighbors(site.x, site.y).maxBy(neighbor => neighbor.site.score)
                 if (neighbor.site.id != myId && site.strength < neighbor.site.strength)
                   Move(site.x, site.y, Still)
                 else
                   Move(site.x, site.y, neighbor.direction)
    })
  }

  private def siteScore(site: Site, depth: Int) = site.production * 5 - (site.strength * 7/10) + 50 - depth * 10
}