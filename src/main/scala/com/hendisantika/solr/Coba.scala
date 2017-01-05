package com.hendisantika.solr

import com.github.takezoe.solr.scala.{Order, SolrClient}

/**
  * Created by hendisantika on 05/01/17.
  */
object Coba {
  def main(args: Array[String]): Unit = {
    val client = new SolrClient("http://localhost:8983/solr/gettingstarted/")

    // register
    client
      .add(Map("id"->"0011", "manu" -> "Lenovo", "name" -> "ThinkPad X201s"))
      .add(Map("id"->"0012", "manu" -> "Lenovo", "name" -> "ThinkPad X220"))
      .add(Map("id"->"0013", "manu" -> "Lenovo", "name" -> "ThinkPad X121e"))
      .commit

    // query
    val result = client.query("name: %name%")
      .fields("id", "manu", "name")
      .sortBy("id", Order.asc)
      .getResultAsMap(Map("name" -> "ThinkPad"))

    result.documents.foreach { doc: Map[String, Any] =>
      println("id: " + doc("id"))
      println("  manu: " + doc("manu"))
      println("  name: " + doc("name"))
    }
  }
}
