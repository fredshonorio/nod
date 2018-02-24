package nod.core

import scala.collection.immutable.{List, Map}
import scala.language.implicitConversions

sealed trait NValue

sealed trait NPrimitive
case class NNull()           extends NValue with NPrimitive with NParam with NProperty with NResult
case class NFloat(f: Float)  extends NValue with NPrimitive with NParam with NProperty with NResult
case class NBool(b: Boolean) extends NValue with NPrimitive with NParam with NProperty with NResult
case class NInt(i: Int)      extends NValue with NPrimitive with NParam with NProperty with NResult
case class NStr(s: String)   extends NValue with NPrimitive with NParam with NProperty with NResult

sealed trait NProperty
case class NPropList(props: List[NPrimitive])   extends NValue with NProperty

sealed trait NResult
case class NResultMap(kv: Map[String, NResult]) extends NValue with NResult
case class NResultList(vs: List[NResult])       extends NValue with NResult
case class NNode(kv: Map[String, NProperty])    extends NValue with NResult
case class NPath()                              extends NValue with NResult

sealed trait NParam
case class NParamMap(kv: Map[String, NParam])   extends NValue with NParam
case class NParamList(vs: List[NParam])         extends NValue with NParam

object Conversions {
  implicit def floatNeo(v: Float)   : NFloat = NFloat(v)
  implicit def boolNeo (v: Boolean) : NBool  = NBool(v)
  implicit def intNeo  (v: Int)     : NInt   = NInt(v)
  implicit def strNeo  (v: String)  : NStr   = NStr(v)

  implicit def propToValue(p: NProperty) = p match {
    case x@NNull() => x
    case x@NFloat(f) => x
    case x@NBool(b) => x
    case x@NInt(i) => x
    case x@NStr(s) => x
    case x@NPropList(props) => x
  }
}

