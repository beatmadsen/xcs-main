package com.madsen.xcs.main

import java.nio.ByteBuffer
import java.util

import com.madsen.xsc.interop._
import com.madsen.xsc.interop.action.Action
import com.madsen.xsc.interop.actuator.{Actuator => InteropActuator, ActuatorStore}
import com.madsen.xsc.interop.predicate._
import com.madsen.xsc.interop.sensor.SensorStore


trait ActionStore {
  def lookup(l: Long): Action
}


trait PredicateStore {
  def lookup(l: Long): Predicate
}


trait Execution {

  protected val predicateStore: PredicateStore
  protected val actionStore: ActionStore

  // TODO: the actuator store should return composite actuators: one that acts on reality, and one that acts on our model
  protected val actuatorStore: ActuatorStore
  protected val sensorStore: SensorStore

  private def findPredicate(id: Long): Predicate = predicateStore.lookup(id)


  private def findAction(id: Long): Action = actionStore.lookup(id)


  def executeOnMatch(chromosome: Chromosome): Unit = {

    val Chromosome(predicateGene, actionGene) = chromosome

    val isMatch = findPredicate(predicateGene.id).isMatch(predicateGene.parameters, sensorStore)

    if (isMatch) {
      findAction(actionGene.id).execute(actionGene.parameters, actuatorStore)
    }
  }
}

trait Actuator extends InteropActuator {

  override def engage(map: util.Map[String, AnyRef]): Unit = {

    import scala.collection.JavaConversions._

    val convertedMap: Map[String, AnyRef] = map.toMap

    engage(convertedMap)
  }


  protected def engage(map: Map[String, AnyRef]): Unit
}


trait ModelActuator extends Actuator

trait EnvironmentActuator extends Actuator

trait CompositeActuator extends Actuator {

  protected val modelActuator: ModelActuator
  protected val environmentActuator: EnvironmentActuator

  override protected def engage(map: Map[String, AnyRef]): Unit = {

    modelActuator.engage(map)
    environmentActuator.engage(map)
  }
}


case class Chromosome(predicateGene: Gene, actionGene: Gene)

object Chromosome {

  val ChromosomeLength = 2 * Gene.GeneLength


  def apply(bytes: Seq[Byte]): Chromosome = {
    require(bytes.size == ChromosomeLength)

    val (predicateBytes, actionBytes) = bytes.splitAt(Gene.GeneLength)

    apply(Gene(predicateBytes), Gene(actionBytes))
  }
}

case class Gene(id: Long, ints: Seq[Long], floats: Seq[Double], booleans: Seq[Boolean]) {

  import com.madsen.util.JavaConversions._

  def parameters: ParameterDto = new ParameterDto(ints, floats, booleans)
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



