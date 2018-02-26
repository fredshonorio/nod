package nod.query

import nod.core.Conversions.propToValue
import nod.core.Statement.{Fieldname, Ident, Label, Properties}
import nod.core.{NValue, Util}
import shapeless.{MkFieldLens, Witness}

import scala.collection.immutable.TreeMap

object Query {

  // query builder

  implicit class StatementInterpolation(ctx: StringContext) {
    def neo4j(subs: Fragment*): List[Fragment] = {
      Util.intersperse(ctx.parts.map(Str).toList, subs.to[List])
    }
  }


  sealed trait Fragment
  case class Str(s: String)                                                        extends Fragment {
    override def toString: String = s
  }
  case class Node(name: Option[Ident], label: Option[Label], property: Properties) extends Fragment
  case class Rel(name: Option[Ident], rType: Option[Label], property: Properties)  extends Fragment
  case class Path(first: Node, rest: List[(Rel, Node)])                            extends Fragment
  case class Map(map: TreeMap[String, NValue])                                     extends Fragment {
    override def toString: String = map.toString
  }

  case class Frags(frags: List[Fragment])                                          extends Fragment

  implicit def identFrag(i: Ident) : Fragment = Str(i.s)
  implicit def labelFrag(i: Label) : Fragment = Str(i.s)
  implicit def fieldFrag(i: Fieldname) : Fragment = Str(i.s)
  implicit def propsFrag(i: Properties) : Fragment = {
    Map(
      i.properties.map { case (k, v) => (k, v: NValue) }
    )
  }

  // Label syntax
  trait NodeLabel[DATA] {
    def label(d: DATA): Label
  }

  implicit class LabelOps[A: NodeLabel](lhs: A) {
    def label: Label = implicitly[NodeLabel[A]].label(lhs)
  }

  // Type (relations) syntax
  // TODO

  // Field syntax
  def field[A](fieldKey: Witness.Lt[_ <: Symbol])(implicit mkl: MkFieldLens[A, fieldKey.T]) : Fieldname =
    Fieldname(fieldKey.value.name)

  // Using Movie.fields('name) would be nicer, but this will do for now
}
