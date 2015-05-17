package com.madsen.xcs.core.actuator

import java.util

import com.madsen.xsc.interop.actuator.{Actuator ⇒ InteropActuator}


/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait Actuator extends InteropActuator {

  override final def engage(map: util.Map[String, AnyRef]): Unit = {

    import scala.collection.JavaConversions._

    val convertedMap: Map[String, AnyRef] = map.toMap

    doEngage(convertedMap)
  }


  protected[actuator] def doEngage(map: Map[String, AnyRef]): Unit
}
