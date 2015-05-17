package com.madsen.xcs.core.action

import com.madsen.xsc.interop.action.Action

import scala.concurrent.Future

/**
 * Created by erikmadsen2 on 15/05/15.
 */
trait ActionStore {

  def lookup(l: Long): Future[Option[Action]]
}
