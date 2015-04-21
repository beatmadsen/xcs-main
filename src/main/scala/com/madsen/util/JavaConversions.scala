package com.madsen.util

/**
 * Created by erikmadsen on 22/04/2015.
 */
object JavaConversions {

  import scala.collection.JavaConversions.seqAsJavaList

  implicit def longs2longs(seq: Seq[Long]): java.util.List[java.lang.Long] = seq map (l => l: java.lang.Long)


  implicit def doubles2doubles(seq: Seq[Double]): java.util.List[java.lang.Double] = seq map (d => d: java.lang.Double)


  implicit def booleans2Booleans(seq: Seq[Boolean]): java.util.List[java.lang.Boolean] = seq map (b => b: java.lang.Boolean)
}
