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

package edu.illinois.ncsa.daffodil.section05.simple_types

import edu.illinois.ncsa.daffodil.tdml.Runner
import org.junit.Test
import org.junit.AfterClass

object TestBoolean2 {
  private val testDir = "/edu/illinois/ncsa/daffodil/section05/simple_types/"

  val runner = Runner(testDir, "Boolean.tdml")

  @AfterClass def shutdown {
    runner.reset
  }
}

class TestBoolean2 {
  import TestBoolean2._

  @Test def test_binaryBoolean_0() { runner.runOneTest("binaryBoolean_0") }
  @Test def test_binaryBoolean_unparse_0() { runner.runOneTest("binaryBoolean_unparse_0") }
  @Test def test_binaryBoolean_1() { runner.runOneTest("binaryBoolean_1") }
  @Test def test_binaryBoolean_unparse_1() { runner.runOneTest("binaryBoolean_unparse_1") }
  @Test def test_binaryBoolean_unparse_2() { runner.runOneTest("binaryBoolean_unparse_2") }
  @Test def test_binaryBoolean_2() { runner.runOneTest("binaryBoolean_2") }
  @Test def test_binaryBoolean_pe_0() { runner.runOneTest("binaryBoolean_pe_0") }
  @Test def test_binaryBoolean_sde_0() { runner.runOneTest("binaryBoolean_sde_0") }
  @Test def test_binaryBoolean_sde_1() { runner.runOneTest("binaryBoolean_sde_1") }
  @Test def test_textBoolean_0() { runner.runOneTest("textBoolean_0") }
  @Test def test_textBoolean_unparse_0() { runner.runOneTest("textBoolean_unparse_0") }
  @Test def test_textBoolean_1() { runner.runOneTest("textBoolean_1") }
  @Test def test_textBoolean_2() { runner.runOneTest("textBoolean_2") }
  @Test def test_textBoolean_3() { runner.runOneTest("textBoolean_3") }
  @Test def test_textBoolean_sde_0() { runner.runOneTest("textBoolean_sde_0") }
  @Test def test_textBoolean_sde_1() { runner.runOneTest("textBoolean_sde_1") }
  @Test def test_textBoolean_sde_2() { runner.runOneTest("textBoolean_sde_2") }
  @Test def test_textBoolean_unparse_sde_0() { runner.runOneTest("textBoolean_unparse_sde_0") }
  @Test def test_textBoolean_sde_3() { runner.runOneTest("textBoolean_sde_3") }
  @Test def test_textBoolean_sde_4() { runner.runOneTest("textBoolean_sde_4") }
  @Test def test_textBoolean_sde_5() { runner.runOneTest("textBoolean_sde_5") }
  @Test def test_textBoolean_pe_0() { runner.runOneTest("textBoolean_pe_0") }

  @Test def test_textBoolean_IgnoreCase() { runner.runOneTest("textBoolean_IgnoreCase") }
}
