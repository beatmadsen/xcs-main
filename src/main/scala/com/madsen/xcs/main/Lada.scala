package com.madsen.xcs.main

import java.nio.ByteBuffer

import com.madsen.xsc.interop._

import scala.collection.JavaConversions

class Lada {

  val action: Action = null
}

case class Chromosome(predicateGene: Gene, actionGene: Gene) {

  private def findPredicate(predicateStore: PredicateStore): Predicate = {
    predicateStore.lookup(predicateGene.id)
  }


  private def findAction(actionStore: ActionStore): Action = {
    actionStore.lookup(actionGene.id)
  }


  def executeOnMatch(predicateStore: PredicateStore, actionStore: ActionStore) = {
    val isMatch = findPredicate(predicateStore).isMatch(predicateGene.parameters)

    if (isMatch) {
      findAction(actionStore).execute(actionGene.parameters) // TODO: Measure delta utility from action how?
    }
  }
}


object Chromosome {

  val ChromosomeLength = 2 * Gene.GeneLength


  def apply(bytes: Seq[Byte]): Chromosome = {
    require(bytes.size == ChromosomeLength)

    val (predicateBytes, actionBytes) = bytes.splitAt(Gene.GeneLength)

    apply(Gene(predicateBytes), Gene(actionBytes))
  }
}


case class Gene(id: Long, ints: Seq[Long], floats: Seq[Double], booleans: Seq[Boolean]) {

  def parameters: ParameterDto = {
    import JavaConversions.seqAsJavaList

    val intParams = seqAsJavaList(ints map long2Long)
    val floatParams = seqAsJavaList(floats map double2Double)
    val boolParams = seqAsJavaList(booleans map boolean2Boolean)

    new ParameterDto(intParams, floatParams, boolParams)
  }
}

object Gene {

  val GeneLength = 4098
  val SizeOfInt = 8
  val SizeOfFloat = 8
  val SizeOfHeaders = SizeOfInt + 1 + 1


  def byteToBooleans(byte: Byte): Seq[Boolean] = 0 to 7 map { bit => ((byte >> bit) & 1) == 1 }


  /**

  Take sequence of bytes and parse out a gene. Layout of incoming bytes:

  Key: <code>header name (size in bytes)</code>

  <code>
    +------------------------------------------------------------------------------------------+
    |id(8)|intCount(1)|doubleCount(1)|intParams(0-2040)|doubleParams(0-2040)|boolParams(8-4088)| TOTAL: 4098 bytes
    +------------------------------------------------------------------------------------------+
  </code>

    */
  def apply(bytes: Seq[Byte]): Gene = {

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

    apply(id, intParams, floatParams, boolParams)
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


