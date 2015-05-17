package com.madsen.util

import java.util.Optional

import scala.language.implicitConversions

/**
 * Created by erikmadsen on 22/04/2015.
 */
object JavaConversions {

  import scala.collection.JavaConversions.seqAsJavaList

  implicit def longs2Longs(seq: Seq[Long]): java.util.List[java.lang.Long] = seq map (l => l: java.lang.Long)


  implicit def doubles2Doubles(seq: Seq[Double]): java.util.List[java.lang.Double] = seq map (d => d: java.lang.Double)


  implicit def booleans2Booleans(seq: Seq[Boolean]): java.util.List[java.lang.Boolean] = seq map (b => b: java.lang.Boolean)


  implicit def option2Optional[T](option: Option[T]): Optional[T] = option match {

    case Some(x) ⇒ Optional.of(x)
    case _ ⇒ Optional.empty()
  }
}
