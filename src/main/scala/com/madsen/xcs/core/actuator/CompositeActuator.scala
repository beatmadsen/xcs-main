package com.madsen.xcs.core.actuator

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait CompositeActuator extends Actuator {

  protected val modelActuator: ModelActuator
  protected val environmentActuator: EnvironmentActuator

  override protected[actuator] def doEngage(map: Map[String, AnyRef]): Unit = {

    modelActuator.doEngage(map)
    environmentActuator.doEngage(map)

    ()
  }
}
