/* Copyright (c) 2016 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

package edu.illinois.ncsa.daffodil.util

import edu.illinois.ncsa.daffodil.exceptions.Assert
// import edu.illinois.ncsa.daffodil.equality._ // TODO: Scala compiler bug - can't use =#= in this file (scalac 2.11.7) because we get a spurious compile error (unable to find ViewEquality in package equality.)
import Maybe._

object MStack {
  final case class Mark(val v: Int) extends AnyVal
  val nullMark = Mark(0)
}
/**
 * Avoid boxing and unboxing of primitive types by using these
 *
 * Note: I turned on -XCheckinit in eclipse. adds overhead, but
 * catches improper initialization. These were not initializing properly,
 * so the idiom evolved to use the scala initializers.
 */
final class MStackOfBoolean private () extends MStack[Boolean]((n: Int) => new Array[Boolean](n), false)

object MStackOfBoolean {
  def apply() = {
    val stk = new MStackOfBoolean()
    stk.init
    stk
  }
}

final class MStackOfInt extends MStack[Int]((n: Int) => new Array[Int](n), 0)

object MStackOfInt {
  def apply() = {
    val stk = new MStackOfInt()
    stk.init
    stk
  }
}

final class MStackOfLong extends MStack[Long]((n: Int) => new Array[Long](n), 0L)

object MStackOfLong {
  def apply() = {
    val stk = new MStackOfLong()
    stk.init
    stk
  }
}

/**
 * Workaround for when we want MStackOf[Maybe[T]]. Use MStackOfMaybe[T], and to push
 * and pop call pushMaybe and popMaybe.
 *
 * We need this workaround since Scala won't let us define Iterator[Maybe[T]] due to
 * problems compiling the def next(): Maybe[T] method. It cannot seem to deal
 * with an Iterator with a value class as the result type.
 *
 * So we use an Array[AnyRef] as the representation here, and we
 * convert null to Nope, and an actual object reference to One(x)
 */
final class MStackOfMaybe[T <: AnyRef] {

  override def toString = delegate.toString

  private val delegate = new MStackOf[T]
  private val nullT = null.asInstanceOf[T]

  def copyFrom(other: MStackOfMaybe[T]) = delegate.copyFrom(other.delegate)

  @inline final def length = delegate.length

  @inline final def push(m: Maybe[T]) = {
    if (m.isDefined) delegate.push(m.get)
    else delegate.push(nullT)
  }

  @inline final def pop: Maybe[T] = {
    val m = delegate.pop
    if (m eq null) Nope
    else One(m)
  }

  @inline final def top: Maybe[T] = {
    val m = delegate.top
    if (m eq null) Nope
    else One(m)
  }

  @inline final def isEmpty = delegate.isEmpty

  def clear() = delegate.clear()
  def toListMaybe = delegate.toList.map {
    x: AnyRef => Maybe(x) // Scala compiler bug without this cast
  }
}

/**
 * Use for objects
 *
 * AnyRef is used here because we really don't need more than one specialized version of this.
 * One generic object version will be sufficient.
 *
 * TODO: Note: Maybe or other Value classes (derived from AnyVal). Currently
 * scala will not let you define an Iterator[Maybe[T]] because of a problem with
 * the def next(): Maybe[T]. It seems to not want to allow this to be a value class.
 * The workaround, which still avoids boxing the Maybe objects, is to use
 * an object reference or null, and call Maybe(thing) explicitly outside the
 * iteration. Maybe(null) is Nope, and Maybe(thing) is One(thing) if thing is not null.
 */
final class MStackOf[T <: AnyRef] {

  override def toString = delegate.toString

  def copyFrom(other: MStackOf[T]) = delegate.copyFrom(other.delegate)

  @inline final def length = delegate.length

  private val delegate = MStackOfAnyRef()

  @inline final def mark = delegate.mark
  @inline final def reset(m: MStack.Mark) = delegate.reset(m)

  @inline final def push(t: T) = delegate.push(t)
  @inline final def pop: T = delegate.pop.asInstanceOf[T]
  @inline final def top: T = delegate.top.asInstanceOf[T]
  @inline final def isEmpty = delegate.isEmpty
  def clear() = delegate.clear()
  def toList = delegate.toList

  def iterator = delegate.iterator.asInstanceOf[Iterator[T]]

}

private[util] final class MStackOfAnyRef private () extends MStack[AnyRef](
  (n: Int) => new Array[AnyRef](n),
  null.asInstanceOf[AnyRef])

object MStackOfAnyRef {
  def apply() = {
    val stk = new MStackOfAnyRef()
    stk.init
    stk
  }
}

/**
 * This is a specialized mutable stack.
 *
 * We have stacks of objects, but we also have stacks of Int, Long, and Boolean
 * and we don't want boxing (which allocates) and unboxing (which leaves things
 * for the garbage collector to reclaim), when we push and pop Int/Long/Boolean
 * things.
 */
protected abstract class MStack[@specialized T] private[util] (
    arrayAllocator: (Int) => Array[T], nullValue: T) {

  private var index = 0
  private var table: Array[T] = null

  def init {
    index = 0
    table = arrayAllocator(32)
  }

  def copyFrom(other: MStack[T]) {
    this.index = other.index
    this.table = other.table.clone
  }
  // private var currentIteratorIndex = -1

  override def toString: String = {
    val stackContents = table.take(index).reverse.mkString(", ")
    "MStack(top=" + stackContents + ")"
  }

  private def growArray(x: Array[T]) = {
    val y = arrayAllocator(math.max(x.length * 2, 1))
    Array.copy(x, 0, y, 0, x.length)
    y
  }

  /**
   * Saves current stack index. Use with reset to restore stack index.
   *
   * This preserves the contents of the stack, to the extent that after calling
   * mark, so long as you don't pop before push, and don't pop more times than push,
   * it will restore the stack to the contents it had.
   */
  @inline final def mark = MStack.Mark(index)

  /**
   *  resets stack top to where it was when mark was called.
   */
  @inline final def reset(m: MStack.Mark) {
    index = m.v
  }

  /** The number of elements in the stack */
  @inline final def length = index

  /**
   * Push an element onto the stack.
   *
   *  @param x The element to push
   */
  @inline final def push(x: T) {
    if (index == table.length) table = growArray(table)
    table(index) = x
    index += 1
  }

  /**
   * Pop the top element off the stack.
   *
   *  @return the element on top of the stack
   */
  @inline final def pop(): T = {
    if (index == 0) Assert.usageError("Stack empty")
    index -= 1
    val x = table(index)
    table(index) = nullValue
    x
  }

  /**
   * View the top element of the stack.
   *
   *  Does not remove the element on the top. If the stack is empty,
   *  an exception is thrown.
   *
   *  @return the element on top of the stack.
   */
  @inline final def top: T = table(index - 1).asInstanceOf[T]

  @inline final def isEmpty: Boolean = index == 0

  def clear() = {
    index = 0
  }

  def toList = iterator.toList

  /**
   * Creates and iterator over the stack in LIFO order.
   *  @return an iterator over the elements of the stack.
   */
  def iterator: Iterator[T] = new Iterator[T] {
    private var currentIndex = index
    private val initialIndex = index

    def hasNext = currentIndex > 0
    def next() = {
      Assert.usage(hasNext)
      Assert.usage(initialIndex <= index) // can't make it smaller than when initialized.
      currentIndex -= 1
      table(currentIndex).asInstanceOf[T]
    }
  }

}
