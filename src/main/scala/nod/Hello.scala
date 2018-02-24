package nod

import nod.core.Conversions._
import nod.core.Statement._

import scala.collection.immutable.TreeMap

object Hello {

  def main(args: Array[String]): Unit = {

    val n = Ident("n") // what names do they use in the AST?

    val Person = Label("Person")
    val name = Field("name")
    val props = Properties(TreeMap("a" -> 1))

    val value =
      neo4j"""
        MATCH ($n: $Person $props)
        RETURN $n.$name
        """

    println(value.mkString(""))
  }
}
