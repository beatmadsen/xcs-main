package com.madsen.xcs.main

import java.nio.ByteBuffer

import com.madsen.xsc.interop.{Action, ParameterDto, Predicate, PredicateStore}

import scala.collection.JavaConversions

class Lada {

  val action: Action = null
}

object Gene {
  val GeneLength = 4098
  val SizeOfInt = 8
  val SizeOfFloat = 8
  val SizeOfHeaders = SizeOfInt + 1 + 1


  def byteToBooleans(byte: Byte): Seq[Boolean] =
    0 to 7 map { bit => ((byte >> bit) & 1) == 1 }


  /*
  8 * 255 = 2040
  Key: header name (size in bytes)
  +------------------------------------------------------------------------------------------+
  |id(8)|intCount(1)|doubleCount(1)|intParams(0-2040)|doubleParams(0-2040)|boolParams(8-4088)| TOTAL: 4098 bytes
  +------------------------------------------------------------------------------------------+
  */
  def process(bytes: Seq[Byte]): (Long, ParameterDtoWrapper) = {

    require(Option(bytes).isDefined)
    require(bytes.size == Gene.GeneLength)

    val array = bytes.toArray
    val buffer: ByteBuffer = ByteBuffer.wrap(array).asReadOnlyBuffer()

    val id = buffer.getLong
    val numInts = buffer.get() & 0xFF
    val numFloats = buffer.get() & 0xFF

    val intParams = 1 to numInts map { a => buffer.getLong }
    val floatParams = 1 to numFloats map { a => buffer.getDouble }

    val booleansAsBytes = new Array[Byte](booleanBytesCount(numInts, numFloats))
    buffer.get(booleansAsBytes)
    val boolParams = booleansAsBytes.toSeq.flatMap(byteToBooleans)

    (id, new ParameterDtoWrapper(intParams, floatParams, boolParams))
  }


  private def booleanBytesCount(intCount: Int, numFloats: Int): Int = {
    GeneLength -
      SizeOfHeaders -
      intCount * SizeOfInt -
      numFloats * SizeOfFloat
  }
}

case class StrictPredicateStore(map: Map[Long, Predicate]) extends MapBasedPredicateStore {
  override val defaultPredicate = new Predicate {
    override def isMatch(parameterDto: ParameterDto): Boolean = false
  }
}


case class ParameterDtoWrapper(intParams: Seq[Long], floatParams: Seq[Double], boolParams: Seq[Boolean]) {

  def convert: ParameterDto = {
    import JavaConversions.seqAsJavaList

    val ints = seqAsJavaList(intParams map long2Long)
    val floats = seqAsJavaList(floatParams map double2Double)
    val booleans = seqAsJavaList(boolParams map boolean2Boolean)

    new ParameterDto(ints, floats, booleans)
  }
}


case class LenientPredicateStore(map: Map[Long, Predicate]) extends MapBasedPredicateStore {
  override val defaultPredicate = new Predicate {
    override def isMatch(parameterDto: ParameterDto): Boolean = true
  }
}


trait MapBasedPredicateStore extends PredicateStore {
  abstract val map: Map[Long, Predicate]
  abstract val defaultPredicate: Predicate

  override def lookup(predicateId: Long): Predicate = map.getOrElse(predicateId, defaultPredicate)
}


