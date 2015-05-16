package com.madsen.xcs.core.predicate

import com.madsen.xsc.interop.predicate.Predicate

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait PredicateStore {

  def lookup(l: Long): Option[Predicate]
}
