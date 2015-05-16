package com.madsen.xcs.core.genetic

import com.madsen.xcs.core.action.ActionStore
import com.madsen.xcs.core.predicate.PredicateStore
import com.madsen.xsc.interop.action.Action
import com.madsen.xsc.interop.predicate.Predicate
import com.madsen.xsc.interop.sensor.SensorStore

/**
 * Created by erikmadsen2 on 15/05/15.
 */
object Chromosome {

  val ChromosomeLength = 2 * Gene.GeneLength


  def apply(bytes: Seq[Byte]): Chromosome = {

    require(bytes.size == ChromosomeLength)

    val (predicateBytes, actionBytes) = bytes.splitAt(Gene.GeneLength)

    apply(Gene(predicateBytes), Gene(actionBytes))
  }
}


case class Chromosome(predicateGene: Gene, actionGene: Gene)


trait ChromosomePool {


  protected val chromosomes: Seq[Chromosome]

  protected val sensorStore: SensorStore
  protected val actionStore: ActionStore
  protected val predicateStore: PredicateStore

  def x = {

    chromosomes
      .map { chromosome => chromosome.predicateGene }
      .map { gene => (gene.id, gene.parameters) }
      .flatMap { case (id, parameters) => findPredicate(id) map (p => (p, parameters)) }
      .filter { case (predicate, parameters) => predicate.isMatch(parameters, sensorStore)
    }

    ???
  }

  private def y(chromosome: Chromosome): Option[Action] = {

    val gene = chromosome.predicateGene

    findPredicate(gene.id) flatMap { predicate =>
      val isMatch = predicate.isMatch(gene.parameters, sensorStore)

      if (isMatch) {
        findAction(chromosome.actionGene.id)
      } else None
    }
  }


  private def findPredicate(id: Long): Option[Predicate] = predicateStore.lookup(id)


  private def findAction(id: Long): Option[Action] = actionStore.lookup(id)
}