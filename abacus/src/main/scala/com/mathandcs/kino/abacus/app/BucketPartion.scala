package com.mathandcs.kino.abacus.app

import java.util

import scala.util.control.Breaks._


class BucketPartion(val numPartion: Short)

/**
  * Each segmentPartion contains a treeMap which stores all segments in tree format
  *
  */
class SegmentPartion(val lineSet: SerializeTreeSet[SegmentNode]) extends java.io.Serializable {

  def this() {
    this(new SerializeTreeSet[SegmentNode]())
  }

  def this(start: Long) {
    this(new SerializeTreeSet[SegmentNode]())
    lineSet.add(new SegmentNode(start))
  }

  /** use start(left) number as node key */
  def put(node: SegmentNode) = {
    val key = node.start
    val lower = lineSet.lower(node)
    val higher = lineSet.higher(node)

    if (null == lower || !lower.enlargeToBack(key)) {
      // higher(right) related
      if (null != higher && higher.enlargeInFront(key)) {
        // merge with right
        higher.withFrontExpand()
      } else
        lineSet.add(node)
    } else if (null == higher || !higher.enlargeInFront(key)) {
      // merge with lower
      lower.withBackExpand()
    } else {
      // merge lower and higher
      lineSet.remove(higher)
      lineSet.add(lower.mergeToBack(higher))
    }
    this
  }

  /** check if line id as start or not (container in lower node) */
  def contains(lineid: Long) = {
    val curNode = new SegmentNode(lineid)
    val lower = lineSet.lower(curNode)
    val higher = lineSet.higher(curNode)
    lineSet.contains(curNode) || null != lower && lower.overlapWith(curNode)
  }

  override def toString: String = {
    val str = new StringBuilder
    val iter = lineSet.iterator()
    while (iter.hasNext) {
      val node = iter.next()
      str ++= s"[${node.start}, ${node.end}]|"
    }
    str.toString()
  }

  def merge(sp: SegmentPartion): SegmentPartion = {
    if (this.lineSet.isEmpty)
      sp
    else if (sp.lineSet.isEmpty)
      this
    else
      new SegmentPartion(SegmentPartion.merge(this, sp))
  }

}

object SegmentPartion {

  def merge(firstSp: SegmentPartion, secondSp: SegmentPartion): SerializeTreeSet[SegmentNode] = {
    assert(firstSp.lineSet.size() > 0 && secondSp.lineSet.size() > 0)
    val mergeNodes = new util.ArrayList[SegmentNode]()
    var pre:SegmentNode = null
    val myIterator = firstSp.lineSet.iterator()
    val spIterator = secondSp.lineSet.iterator()
    // initalize loop invariant
    var myNode = myIterator.next()
    var spNode = spIterator.next()
    assert(!myNode.overlapWith(spNode))    // same line id should not coocurrence
    if (myNode.compareTo(spNode) < 0) {
      pre = myNode
      myNode = null
    } else {
      pre = spNode
      spNode = null
    }

    def appendOrReplace(arr: util.ArrayList[SegmentNode], node: SegmentNode, previous: SegmentNode) = {
      if (previous.enlargeNodeToBack(node)) {
        previous.mergeToBack(node)
        previous
      }
      else {
        arr.add(previous)
        node
      }
    }

    // begin to loop
    breakable {
      while (true) {
        if (null == myNode)
          myNode = if (myIterator.hasNext) myIterator.next() else null
        if (null == spNode)
          spNode = if (spIterator.hasNext) spIterator.next() else null

        if (null != myNode && null != spNode) {
          assert(!spNode.overlapWith(myNode))
          if (myNode.compareTo(spNode) < 0) {
            pre = appendOrReplace(mergeNodes, myNode, pre)
            // flip consumed node
            myNode = null
          } else {
            pre = appendOrReplace(mergeNodes, spNode, pre)
            // flip consumed node
            spNode = null
          }
        } else if (null != myNode) {
          pre = appendOrReplace(mergeNodes, myNode, pre)
          myNode = null
        } else if (null != spNode) {
          pre = appendOrReplace(mergeNodes, spNode, pre)
          spNode = null
        } else {
          break()
        }
      }
    }

    if (null != pre) mergeNodes.add(pre)

    // todo: optimize into linear construction
    new SerializeTreeSet[SegmentNode](mergeNodes)
  }

}

/**
  * segment representation
  *
  * @param start
  * @param end
  */
 class SegmentNode(var start: Long, var end: Long) extends Comparable[SegmentNode] with java.io.Serializable {

  def this(point: Long) = {
    this(point, point)
  }

  def enlargeInFront(key: Long) = start == key + 1

  def enlargeToBack(key: Long) = end == key - 1

  def enlargeNodeInFromt(node: SegmentNode) = enlargeInFront(node.end)

  def enlargeNodeToBack(node: SegmentNode) = enlargeToBack(node.start)

  def withFrontExpand() = {
    start -= 1
    this
  }

  def withBackExpand() = {
    end += 1
    this
  }

  def mergeToBack(node: SegmentNode) = {
    end = node.end
    this
  }

  def mergeInFront(node: SegmentNode) = {
    start = node.start
    this
  }

  override def toString = s"[$start, $end]"

  /* intersection not empty */
  def overlapWith(node: SegmentNode) = {
    end >= node.start && start <= node.end
  }

  /** start value as compare value */
  override def compareTo(node: SegmentNode): Int = {
    if (start < node.start)
      -1
    else if (start == node.start)
      0
    else
      1
  }
}

/**
  * Serializable tree map for spark
 *
  * @tparam E
  */
class SerializeTreeSet[E] extends util.TreeSet[E] with java.io.Serializable {
  def this(c: util.Collection[_ <: E]) = {
    this()
    addAll(c)
  }
}