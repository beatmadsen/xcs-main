package com.madsen.xcs.store

/**
 * Created by erikmadsen2 on 03/05/15.
 */
trait BaseStore[T] {
  def lookup(l: Long): T
}


trait SeqBasedStore[T] extends BaseStore[T] {
  protected val seq: Seq[T]

  protected def size: Long = seq.size

  def lookup(l: Long): T = seq.apply(l.toInt)
}

trait ModStore[T] extends BaseStore[T] {

  protected def size: Long

  protected def doLookup(l: Long): T

  override def lookup(l: Long): T = doLookup(l % size) // TODO: This implies that behaviour will change completely when one more item is added
}

trait SeqModStore[T] extends SeqBasedStore[T] with ModStore[T] {
  protected def doLookup(l: Long): T = super[SeqBasedStore].lookup(l)
}


case class SimpleSeqModStore[T](seq: Seq[T]) extends SeqModStore[T]
