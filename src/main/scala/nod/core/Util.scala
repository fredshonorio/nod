package nod.core

object Util {

  def intersperse[A](first: List[A], next: List[A]): List[A] = {
    first.map(List(_)).zipAll(next.map(List(_)), Nil, Nil).flatMap(Function.tupled(_ ::: _))
  }
}
