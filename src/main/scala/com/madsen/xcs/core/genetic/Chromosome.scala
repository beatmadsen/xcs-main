package com.madsen.xcs.core.genetic

/**
 * Created by erikmadsen2 on 15/05/15.
 */
object Chromosome {

  val ChromosomeLength = 2 * Gene.GeneLength


  def apply(bytes: Seq[Byte]): Chromosome = {

    require(bytes.size == ChromosomeLength)

    val (predicateBytes, actionBytes) = bytes.splitAt(Gene.GeneLength)

    apply(Gene(predicateBytes), Gene(actionBytes))
  }
}


case class Chromosome(predicateGene: Gene, actionGene: Gene)
