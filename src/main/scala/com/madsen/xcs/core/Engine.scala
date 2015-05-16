package com.madsen.xcs.core

import java.util.concurrent.atomic.AtomicBoolean

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

  private def findPredicate(id: Long): Predicate = predicateStore.lookup(id).get


  private def findAction(id: Long): Action = actionStore.lookup(id).get


  def executeOnMatch(chromosome: Chromosome): Unit = {

    val Chromosome(predicateGene, actionGene) = chromosome

    val isMatch = findPredicate(predicateGene.id).isMatch(predicateGene.parameters, sensorStore)

    if (isMatch) {
      findAction(actionGene.id).execute(actionGene.parameters, actuatorStore)
    }
  }
}


trait Sumo {

  protected val engine: Engine

  private val running: AtomicBoolean = new AtomicBoolean(true)

  def run(): Unit = {

    while (running.get()) {
      /*
      (1) Sensor values are read.
      (2) All active chromosomes are in a pool. Predicates react to sensors.
      (3) All predicates matching sensor values are found
      (4) The highest fitness chromosome with a matching predicate is chosen
      (5) The chromosome's action is executed
      (6) Reinforcement system's feedback value for action is obtained
      (7) Update fitness
      (8) Generate new rules
       */

    }
  }

  def stop() = running.set(false)
}
