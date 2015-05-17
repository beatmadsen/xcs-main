package com.madsen.xcs.core.actuator

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait CompositeActuator extends Actuator {

  protected val modelActuator: ModelActuator
  protected val environmentActuator: EnvironmentActuator

  override def lama(map: Map[String, AnyRef]): Unit = {

    modelActuator.lama(map)
    environmentActuator.lama(map)

    ()
  }
}
