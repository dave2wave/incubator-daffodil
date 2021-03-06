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

package edu.illinois.ncsa.daffodil.processors.unparsers

import edu.illinois.ncsa.daffodil.processors.RuntimeData
import edu.illinois.ncsa.daffodil.processors.FillByteEv
import edu.illinois.ncsa.daffodil.processors.SuspendableOperation
import edu.illinois.ncsa.daffodil.processors.SuspendableUnparser
import edu.illinois.ncsa.daffodil.util.LogLevel

class SkipRegionUnparser(
  skipInBits: Int,
  e: RuntimeData,
  fillByteEv: FillByteEv)
  extends PrimUnparserObject(e) {

  override def runtimeDependencies = List(fillByteEv)

  override def unparse(state: UState) = {
    val dos = state.dataOutputStream
    if (!dos.skip(skipInBits)) UE(state, "Unable to skip %s(bits).", skipInBits)
  }
}

class AlignmentFillUnparserSuspendableOperation(
  alignmentInBits: Int,
  override val rd: RuntimeData,
  fillByteEv: FillByteEv)
  extends SuspendableOperation {

  override def test(ustate: UState) = {
    val dos = ustate.dataOutputStream
    if (dos.maybeAbsBitPos0b.isEmpty) {
      log(LogLevel.Debug, "%s %s Unable to align to %s bits because there is no absolute bit position.", this, ustate, alignmentInBits)
    }
    dos.maybeAbsBitPos0b.isDefined
  }

  override def continuation(state: UState) {
    val dos = state.dataOutputStream
    val fb = fillByteEv.evaluate(state)
    dos.setFillByte(fb)
    val b4 = dos.relBitPos0b
    if (!dos.align(alignmentInBits))
      UE(state, "Unable to align to %s(bits).", alignmentInBits)
    val aft = dos.relBitPos0b
    val delta = aft - b4
    if (delta == 0)
      log(LogLevel.Debug, "%s did nothing.", this)
    else
      log(LogLevel.Debug, "%s moved %s bits to align to %s(bits).", this, delta, alignmentInBits)
  }
}

class AlignmentFillUnparser(
  alignmentInBits: Int,
   val rd: RuntimeData,
  fillByteEv: FillByteEv)
  extends PrimUnparserObject(rd)
  with SuspendableUnparser {

  override def runtimeDependencies = List(fillByteEv)

  override def suspendableOperation =
    new AlignmentFillUnparserSuspendableOperation(
      alignmentInBits, rd, fillByteEv)
}

class MandatoryTextAlignmentUnparser(
  alignmentInBits: Int,
  e: RuntimeData,
  fillByteEv: FillByteEv)
  extends AlignmentFillUnparser(alignmentInBits, e, fillByteEv)
