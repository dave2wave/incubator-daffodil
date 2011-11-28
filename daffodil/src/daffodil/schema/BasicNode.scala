package daffodil.schema

/**
 * Copyright (c) 2010 NCSA.  All rights reserved.
 * Developed by: NCSA Cyberenvironments and Technologies
 *               University of Illinois at Urbana-Champaign
 *               http://cet.ncsa.uiuc.edu/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the names of NCSA, University of Illinois, nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 *
 */

/*
 * Created By: Alejandro Rodriguez < alejandr @ ncsa . uiuc . edu >
 * Date: 2010
 */

import annotation.Annotation
import daffodil.processors.VariableMap
import org.jdom.Parent
import daffodil.xml.Namespaces
import java.io.Serializable
import daffodil.parser.RollbackStream
import daffodil.parser.regex.Regex
import scala.collection.mutable.LinkedList


abstract class BasicNode(val target:String,val namespaces:Namespaces,
                         var annotation:Annotation) extends Serializable with Diffable {

  def apply(input:RollbackStream,parentAnnotations:Annotation,
                     variables:VariableMap,parent:Parent,
                     maxLength:Int,terminators:List[Regex]):LinkedList[org.jdom.Element]
  
  def canEqual(o:Any):Boolean = o.isInstanceOf[BasicNode]

 
  
  def diff(o:Any) : Similarity = {
     o match {
      case null => Different(this, o)
      case that:BasicNode => {
       val annoDiff = Diffable.diff(annotation, that.annotation)
       if (annoDiff != Same) return annoDiff
       if (this.target != that.target) return Different(this.target, that.target) 
       if (this.namespaces != that.namespaces) return Different(this.namespaces, that.namespaces)
       Same
      }
      case	 _ => DifferentType
    }
    
  }

}
