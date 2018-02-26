package nod

import java.util.UUID

import nod.core.Conversions._
import nod.core.Statement._
import shapeless._

import scala.collection.immutable.TreeMap

object Hello {

  /**
    * Overall goals:
    * - describing schema
    * - query builder with inline parameters
    * - ensuring coherence between schema and queries
    * - generate typesafe writers and readers for node properties
    */
  /**
    * The objectives here are:
    * - reduce boilerplate involved in describing properties of a node/relationship
    *   - ideally this is not done manually
    * - we need to describe various things about the schema, (node labels, rel types, outgoing rels, prop names and types, etc)
    * - if there is a distinction between the data and the schema (a user case class and a user schema object),
    * coherence between the two should be either automatic or typesafe (compile errors when there is a mismatch)
    * - we need to have syntax that allows referencing some parts of the schema inside the `neo4j` interpolator, enumerated
    * in the `Checklist` session
    * - we should be able to destinguish between creating and updating a node, again, the coherence between both should either
    * be guaranteed automatically or be a type error
    *   - updating might more complex than setting/not setting a value, so maybe the MovieF[Option] trick is not useful enough
    *   - might be better to force manual coherence, have only NodeProps[MovieUpd]
    */

  /* The schema is useful because there are schema-ish things we can't encode in the case class structure (e.g outgoing relationships, labels) */
  object MovieSchema {
    val label: Label = Label("Label")

    val name = Fieldname("name") // TODO: remove repetition, shapeless?
    val id = Fieldname("id")
  }

  /*
  * Can we avoid a schema and use typeclasses?
  *
  * # For relationships yes:
  *
  * object OutUser {
  *   val Likes : Relation = ???
  * }
  *
  * implicit val outgoingUser = Outgoing[User] = OutUser
  *
  * add .out syntax for Outgoing, can we make a typeclass for the companion object?
  *
  * if so then:
  * neo4j"""
  *   MATCH ($n) - [${User.out.Likes}] -> ....
  *   """
  *
  * # For fields?
  * We need
  * neo4j"""
  *   MATCH ($n)
  *   RETURN ($n.${User.field.id}
  *   """
  *
  * https://github.com/milessabin/shapeless/blob/master/examples/src/main/scala/shapeless/examples/labelledgeneric.scala
  * it seems possible!
  *
  * # Checklist
  *
  * We need to be able to have static schema notation inside the builder for the following:
  *
  * - node labels -> possible with a typeclass -> Label[User] + syntax ops, do we care about multiple labels? use one typeclass for each (single and mult)?
  * - node fields -> maybe possible with LabelledGeneric, unsure about syntax yet
  * - node (outgoing) relationships -> as above, typeclass Outgoing[User], object for each node (OutUser), + syntax
  * - rel fields -> same as node fields, if applicable
  * - rel type -> sabe as labeled
  *
  * Its obv also be possible to use values directly, so labels and fields can be manually defined, and coherence/safety
  * is up to the user. This is useful if there are things we can't express.
  *
  * */

  case class MovieF[F[_]](id: UUID, name: F[String])

  type Movie = MovieF[Id]
  type MovieUpdate = MovieF[Option]

  /* seems easy enough to write derivations for, final step will need Label[Data] as well to fully describe create
   * def create[A](a: A)(implicit val props:CreateProps[A], implicit val label: Label[A])
   */
  trait CreateProps[DATA] {
    def create(data: DATA): Properties
  }

  val createUser: CreateProps[Movie] = movie => Properties(TreeMap(
    MovieSchema.id.s -> movie.id.toString,
    MovieSchema.name.s -> movie.name
  ))

  /**
    * The F encoding is cool because it maintains coherence (adding a field to MovieF propagates to Movie and MovieUpdate).
    * However I'm unsure how we can derive an Update typeclass.
    *
    * We could restrict it to UpdateProps, making the selection of the node is a distinct concern. But I still don't know
    * how to derive that.
    *
    * Maybe represent update object explicitly and ensure coherence with a toCreate(u: UserUpdate) : User method
    * implement with this => https://github.com/kailuowang/henkan
    * That way we could just derive UpdateProps[MovieUpdate] normally
    *
    */

  /*
  val update: Update[MovieUpdate] = movie => (
    Properties(TreeMap(MovieSchema.id.s -> movie.id.toString)),
    Properties(TreeMap()).insert_(MovieSchema.name.s, movie.name.map(NStr))
  )

  val updateProps: UpdateProps[MovieUpdate] = movie =>
    Properties(TreeMap()).insert_(MovieSchema.name.s, movie.name.map(NStr))
  */

  trait Update[DATA] {
    def update(data: DATA): (Properties, Properties)
  }

  trait UpdateProps[DATA] {
    def update(data: DATA): Properties
  }

  def main(args: Array[String]): Unit = {
  }
}
