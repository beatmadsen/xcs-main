package com.madsen.xcs.core.predicate

import com.madsen.xsc.interop.predicate.Predicate

import scala.concurrent.Future

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait PredicateStore {

  def lookup(l: Long): Future[Option[Predicate]]
}
