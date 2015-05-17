package com.madsen.xcs.core

import java.util.concurrent.atomic.AtomicBoolean


trait Sumo {


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
