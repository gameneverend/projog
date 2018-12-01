/*
 * Copyright 2013-2014 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.core.function.compound;

import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Term;

/* TEST
 %FALSE \+ true
 %TRUE \+ fail

 % Note: "not" is a synonym for "\+".
 %FALSE not(true)
 %TRUE not(fail)

 %QUERY \+ [A,B,C,9]=[1,2,3,4], A=6, B=7, C=8
 %ANSWER
 % A=6
 % B=7
 % C=8
 %ANSWER

 %QUERY \+ ((X=Y,1>2)), X=1, Y=2
 %ANSWER
 % X=1
 % Y=2
 %ANSWER

 test(X,Y) :- \+ ((X=Y,1>2)), X=1, Y=2.

 %QUERY test(X,Y)
 %ANSWER
 % X=1
 % Y=2
 %ANSWER
 */
/**
 * <code>\+ X</code> - "not".
 * <p>
 * The <code>\+ X</code> goal succeeds if an attempt to satisfy the goal represented by the term <code>X</code> fails.
 * The <code>\+ X</code> goal fails if an attempt to satisfy the goal represented by the term <code>X</code> succeeds.
 * </p>
 */
public final class Not extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term t) {
      Predicate e = KnowledgeBaseUtils.getPredicate(getKnowledgeBase(), t);
      if (!e.evaluate()) {
         t.backtrack();
         return true;
      } else {
         return false;
      }
   }
}
