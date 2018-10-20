/*
 * Copyright 2018 S. Webber
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
package org.projog.core.function.math;

/* TEST
 %QUERY X is 13 << 0
 %ANSWER X=13

 %QUERY X is 13 << 1
 %ANSWER X=26

 %QUERY X is 13 << 2
 %ANSWER X=52

 %QUERY X is 13 << 3
 %ANSWER X=104

 %QUERY X is 13 << 4
 %ANSWER X=208

 % Note bit shifting using a negative value gives different results than in some other Prolog implementations.
 %QUERY X is 13 << -1
 %ANSWER X=-9223372036854775808
 */
/**
 * <code>&lt;&lt;</code> - left shift bits.
 */
public final class ShiftLeft extends AbstractBinaryIntegerArithmeticOperator {
   // TODO for both this class and ShiftRight, review what happens when the second argument is negative
   @Override
   protected long calculateLong(long n1, long n2) {
      return n1 << n2;
   }
}
