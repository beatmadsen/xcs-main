package com.madsen.xcs.main


import com.madsen.xsc.interop.{Action, Predicate, PredicateStore}

/**
 * Created by erikmadsen on 23/03/2015.
 */
class Lada {

  val action : Action = null
}

case class Limbo(map: Map[Long, Predicate]) extends PredicateStore {
  override def lookup(predicateId: Long): Predicate = map.apply(predicateId)
}
