package nod.core

import scala.collection.immutable.TreeMap

object Statement {

  case class Ident(s: String)
  case class Label(s: String)
  case class Fieldname(s: String)

  case class Properties(properties: TreeMap[String, NProperty]) {
    def insert[T <: NProperty](key: String, value: T) : Properties =
      Properties(properties.insert(key, value))

    def insert_[T <: NProperty](key: String, value: Option[T]) : Properties =
      value match {
        case Some(v) => this.insert(key, v)
        case None => this
      }
  }
}

