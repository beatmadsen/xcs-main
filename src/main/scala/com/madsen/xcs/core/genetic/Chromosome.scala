package com.madsen.xcs.core.genetic

import com.madsen.xcs.core.action.ActionStore
import com.madsen.xcs.core.genetic.Gene._
import com.madsen.xcs.core.predicate.PredicateStore
import com.madsen.xcs.core.sensor.SensorValueStore
import com.madsen.xsc.interop.action.Action
import com.madsen.xsc.interop.predicate.Predicate
import com.madsen.xsc.interop.sensor.{SensorValueStore ⇒ InteropSensorValueStore}

import scala.collection.mutable.{Map ⇒ MutaMap}
import scala.concurrent.Future

/**
 * Created by erikmadsen2 on 15/05/15.
 */
object Chromosome {

  val ChromosomeLength = 2 * GeneLength


  def apply(bytes: Seq[Byte]): Chromosome = {

    require(bytes.size == ChromosomeLength)

    val (predicateBytes, actionBytes) = bytes.splitAt(GeneLength)

    apply(Gene(predicateBytes), Gene(actionBytes), 0.0)
  }
}


case class Chromosome(predicateGene: Gene, actionGene: Gene, fitness: Double)


trait ChromosomePool {


  protected val chromosomes: Set[Chromosome]

  protected val sensorStore: SensorValueStore
  protected val actionStore: ActionStore
  protected val predicateStore: PredicateStore


  def bestAction[T <: AnyRef](sensorId: String, value: T): Future[Option[Action]] = {

    sensorStore.recordValue(sensorId, value)

    val futures = chromosomes map checkPredicate

    Future.sequence(futures)
      .map(_.flatten)
      .map { cs ⇒ cs.maxBy(_.fitness) }
      .flatMap { chromosome ⇒ findAction(chromosome.actionGene.id) }
  }


  private def checkPredicate(chromosome: Chromosome): Future[Option[Chromosome]] = {

    val gene = chromosome.predicateGene

    findPredicate(gene.id) map { maybe ⇒
      maybe flatMap { predicate ⇒
        if (predicate.isMatch(gene.parameters, sensorStore)) Some(chromosome)
        else None
      }
    }
  }


  private def findPredicate(id: Long): Future[Option[Predicate]] = predicateStore.lookup(id)


  private def findAction(id: Long): Future[Option[Action]] = actionStore.lookup(id)
}