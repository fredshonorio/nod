package nod

import java.util.UUID

import nod.QueryExample.SchemaExample.ActorOut.ActedIn
import nod.core.Statement.Ident
import nod.query.Query._
import nod.query.Schema.{Relation, label}

object QueryExample {

  /**
    * This is how one would describe a schema
    */
  object SchemaExample {

    // Movie
    case class Movie(uuid: UUID, `nõme`: String)

    implicit val movieLabel: NodeLabel[Movie.type] = label("Movie")

    // Actor
    case class Actor(`nãme`: String)

    implicit val actorLabel: NodeLabel[Actor.type] = label("Actor")

    object ActorOut {
      case class ActedIn(role: String) extends Relation[Actor, Movie]
      // TODO: rel Type
    }

  }

  def main(args: Array[String]): Unit = {
    import SchemaExample._

    val m = Ident("m")
    val a = Ident("a")
    val r = Ident("r")
    val M = Movie
    val A = Actor

    val stmt =
      neo4j"""MATCH ($m:${M.label}) <[$r]- ($a:${A.label})
             |RETURN
             | $m.${field[Movie](scala.Symbol("nõme"))}
             |,$r.${field[ActedIn]('role)}
             |,$a.${field[Actor]('nãme)}"""

    println(stmt.mkString(""))

  }
}
