package nod.query

import nod.core.Statement.Label
import nod.query.Query.NodeLabel

object Schema {

  trait Relation[FROM, TO]

  def label[A](name: String) : NodeLabel[A] = _ => Label(name)

}
