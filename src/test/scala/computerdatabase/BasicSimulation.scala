package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://opencart.abstracta.us") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  //  val scn = scenario("Busqueda IPHONE") // A scenario is a chain of requests and pauses
  //    .exec(
  //    http("DA RESULTADOS")
  //      .get("/index.php?route=product/search&search=iphone")
  //      .check(status.is(200))
  //      .check(substring("There is no product that matches the search criteria").notExists)
  //  )
  //    .pause(1)
  //    .exec(
  //      http("NO DA RESULTADOS")
  //        .get("/index.php?route=product/search&search=iphonasdasdasde")
  //        .check(status.is(200))
  //        .check(substring("There is no product that matches the search criteria").notExists)
  //    )


  val sets = csv("data.csv")
  val queue_size = sets.records.size
  val count = new java.util.concurrent.atomic.AtomicInteger(0)

  val scn = scenario("BUSQUEDA PRODUCTOS CSV")
    .asLongAs(_ => count.getAndIncrement() < queue_size) {
      feed(sets)
        .exec(
          http("request_csv")
            .get("/index.php?route=product/search&search=${search}")
            .check(status.is(200))
            .check(substring("There is no product that matches the search criteria").notExists)
        )
    }


  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
  //  setUp(scn2.inject(atOnceUsers(1)).protocols(httpConf))


}
