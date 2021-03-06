package opencart

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://opencart.abstracta.us") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val sets = csv("data.csv")
  val queue_size = sets.records.size


  val scn = scenario("BUSQUEDA PRODUCTOS CSV")
    .during(15 minutes) {
      feed(sets.circular)
        .exec(
          http("request_csv")
            .get("/index.php?route=product/search&search=${search}")
            .check(status.is(200))
            .check(substring("There is no product that matches the search criteria").notExists)
            .check(regex("""<div class="product-thumb">""").count.greaterThan(0))
        )
        .pause(2 second)
    }

  // 1 usuario
  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))

  // 20 usuarios
  setUp(scn.inject(rampUsers(20) over (5 minutes)).protocols(httpConf))
}
