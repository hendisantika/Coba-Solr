package com.hendisantika.solr

import com.github.takezoe.solr.scala.Order
import com.github.takezoe.solr.scala.async.AsyncSolrClient

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationDouble}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by hendisantika on 05/01/17.
  */
object AsyncSolrClientSample extends App {
  val client = new AsyncSolrClient("http://localhost:8983/solr/gettingstarted/")

  val f1 = client.register(Map("id" -> "005", "name" -> "ThinkPad X1 Carbon", "manu" -> "Lenovo"))
  f1.onComplete {
    case Success(x) => println("registered!")
    case Failure(t) => t.printStackTrace()
  }

  Await.result(f1, Duration.Inf)

  val f2 = client.withTransaction {
    for {
      _ <- client.add(Map("id" -> "006", "name" -> "Nexus7 2012", "manu" -> "ASUS"))
      _ <- client.add(Map("id" -> "007", "name" -> "Nexus7 2013", "manu" -> "ASUS"))
      _ <- client.add(Map("id" -> "008", "name" -> "Xiaomi Redmi Note 1", "manu" -> "Xiaomi"))
      _ <- client.add(Map("id" -> "009", "name" -> "Xiaomi Redmi Note 2", "manu" -> "Xiaomi"))
      _ <- client.add(Map("id" -> "010", "name" -> "Xiaomi Redmi Note 3", "manu" -> "Xiaomi"))
    } yield ()
  }

  Await.result(f2, Duration.Inf)

  val future = client.query("name:%name%")
    .fields("id", "manu", "name")
    .facetFields("manu")
    .sortBy("id", Order.asc)
    .getResultAsMap(Map("name" -> "ThinkPad X201s"))

  future.onComplete {
    case Success(result) => {
      println("count: " + result.numFound)
      result.documents.foreach { doc =>
        println("id: " + doc("id"))
        println("manu: " + doc.get("manu").getOrElse("<NULL>"))
        println("name: " + doc("name"))
      }
    }
    case Failure(t) => println("Ada error nich ...")
      t.printStackTrace()
  }

  Await.result(future, Duration.Inf)
  client.shutdown
}

case class Product(id: String, manu: Option[String], name: String)

case class Param(name: String)
