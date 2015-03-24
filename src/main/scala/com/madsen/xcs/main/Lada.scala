package com.madsen.xcs.main

import com.madsen.xsc.interop.{Action, ParameterDto, Predicate, PredicateStore}

/**
 * Created by erikmadsen on 23/03/2015.
 */
class Lada {

  val action: Action = null
}

case class StrictPredicateStore(map: Map[Long, Predicate]) extends MapBasedPredicateStore {
  override val defaultPredicate = new Predicate {
    override def isMatch(parameterDto: ParameterDto): Boolean = false
  }
}

case class LenientPredicateStore(map: Map[Long, Predicate]) extends MapBasedPredicateStore {
  override val defaultPredicate = new Predicate {
    override def isMatch(parameterDto: ParameterDto): Boolean = true
  }
}


trait MapBasedPredicateStore extends PredicateStore {
  abstract val map: Map[Long, Predicate]
  abstract val defaultPredicate: Predicate

  override def lookup(predicateId: Long): Predicate = map.getOrElse(predicateId, defaultPredicate)
}
