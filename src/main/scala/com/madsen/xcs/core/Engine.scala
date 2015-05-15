package com.madsen.xcs.core

import com.madsen.xcs.core.action.ActionStore
import com.madsen.xcs.core.actuator.CompositeActuatorStore
import com.madsen.xcs.core.genetic.Chromosome
import com.madsen.xcs.core.predicate.PredicateStore
import com.madsen.xsc.interop.action.Action
import com.madsen.xsc.interop.predicate.Predicate
import com.madsen.xsc.interop.sensor.SensorStore

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait Engine {

  protected val predicateStore: PredicateStore
  protected val actionStore: ActionStore
  protected val actuatorStore: CompositeActuatorStore
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
