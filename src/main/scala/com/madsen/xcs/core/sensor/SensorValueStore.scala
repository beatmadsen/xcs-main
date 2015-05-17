package com.madsen.xcs.core.sensor

import java.util.Optional

import com.madsen.xsc.interop.sensor.{SensorValueStore ⇒ InteropSensorValueStore}

import scala.collection.mutable.{Map ⇒ MutaMap}


/**
 * Created by erikmadsen2 on 17/05/15.
 */
trait SensorValueStore extends InteropSensorValueStore {

  import com.madsen.util.JavaConversions._

  override def latestValueOn[T](sensorId: String): Optional[T] = valueBy(sensorId)

  protected def valueBy[T](sensorId: String): Option[T]

  def recordValue[T <: AnyRef](sensorId: String, value: T): Unit
}


trait MapBasedSensorValueStore extends SensorValueStore {

  protected val map: MutaMap[String, AnyRef]


  override protected def valueBy[T](sensorId: String): Option[T] = {

    map.get(sensorId).map(_.asInstanceOf[T])
  }

  override def recordValue[T <: AnyRef](sensorId: String, value: T): Unit = {

    map += ((sensorId, value))
  }
}


case class SimpleSensorValueStore(map: MutaMap[String, AnyRef]) extends MapBasedSensorValueStore