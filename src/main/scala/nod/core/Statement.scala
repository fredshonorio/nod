package nod.core

import nod.core.Conversions.propToValue

import scala.collection.immutable.TreeMap

object Statement {

  case class Ident(s: String)
  case class Label(s: String)
  case class Field(s: String)

  case class Properties(properties: TreeMap[String, NProperty])

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

  implicit class Dec(ctx: StringContext) {
    def neo4j(subs: Fragment*): List[Fragment] = {
      Util.intersperse(ctx.parts.map(Str).toList, subs.to[List])
    }
  }

  implicit def identFrag(i: Ident) : Fragment = Str(i.s)
  implicit def labelFrag(i: Label) : Fragment = Str(i.s)
  implicit def fieldFrag(i: Field) : Fragment = Str(i.s)
  implicit def propsFrag(i: Properties) : Fragment = {
    Map(
      i.properties.map { case (k, v) => (k, v: NValue) }
    )
  }
}

