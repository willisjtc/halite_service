package willie.big

import io.vertx.core.http.{HttpHeaders, HttpMethod}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.{DeploymentOptions, Vertx}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Router, RoutingContext}
import willie.big.bots.RandomBot
import willie.big.models.{Grid, Version}

class BotService extends ScalaVerticle {
  println("Starting " + this.getClass.getName)

  override def start() {
    println("Starting in future")
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    route(router)
    server.requestHandler(router.accept _)
    server.listen(5353)
  }

  override def stop() = {
    println("Stopping " + this.getClass.getName)
  }

  private def route(router: Router) {
    router.route().handler(BodyHandler.create())
    router.route(HttpMethod.POST, "/versions/:version/bots/:botName/moves").blockingHandler((routingContext: RoutingContext) => {
      val response = routingContext.response()
      val version = routingContext.request().getParam("version").getOrElse("1")
      val botName = routingContext.request().getParam("botName").getOrElse("")

      val body = routingContext.getBodyAsJson().getOrElse(new JsonObject())
      val id = body.getInteger("id")
      val gameMap = body.getJsonObject("gameMap")
      val contents = gameMap.getJsonArray("contents")
      val grid = new Grid(id, gameMap.getInteger("width"), gameMap.getInteger("height"), contents)
      val bot = new Version(version).bots(id).getOrElse(botName, new RandomBot(id))

      response.setStatusCode(200)
      response.putHeader(HttpHeaders.CONTENT_TYPE.toString, "application/json")
      val moves = bot.movesToString(bot.moves(grid))
      response.end(new JsonObject().put("moves", moves).encode())

    }, false)
  }
}

object Main {
  def main(args: Array[String]) {
    val vertx = Vertx.vertx
    val deployOptions = DeploymentOptions.fromJson(new JsonObject().put("port", args(0)))
    vertx.deployVerticleFuture(ScalaVerticle.nameForVerticle[BotService], deployOptions)
  }
}