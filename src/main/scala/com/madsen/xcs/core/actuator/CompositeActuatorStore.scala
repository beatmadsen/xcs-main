package com.madsen.xcs.core.actuator

import com.madsen.xsc.interop.actuator.{Actuator => InteropActuator, ActuatorStore}

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait CompositeActuatorStore extends ActuatorStore {

  override final def lookup(s: String): InteropActuator = doLookup(s)

  protected def doLookup(s: String): CompositeActuator
}
