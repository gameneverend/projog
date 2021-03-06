/*
 * Copyright 2013 S. Webber
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
package org.projog.core.udp.compiler;

import org.projog.core.udp.TailRecursivePredicate;
import org.projog.core.udp.TailRecursivePredicateMetaData;

/**
 * A super-class of all compiled "tail recursion optimised" user defined predicates.
 * <p>
 * For a user defined predicate to be implemented using {@code CompiledTailRecursivePredicate} it must be judged as
 * eligible for <i>tail recursion optimisation</i> using the criteria used by {@link TailRecursivePredicateMetaData}.
 * </p>
 *
 * @see TailRecursivePredicateMetaData
 */
public abstract class CompiledTailRecursivePredicate extends TailRecursivePredicate implements CompiledPredicate {
   /**
    * Indicates which arguments, if they are immutable, will prohibit re-evaluation of the predicate from succeeding.
    * <p>
    * The length of the returned array will be equal to the arity (i.e. number of arguments) of the predicate. If an
    * element in the array is {@code true} then that indicates that, if the corresponding predicate argument is
    * immutable, then evaluation of the predicate will succeed (at most) once.
    */
   public abstract boolean[] isSingleResultIfArgumentImmutable();
}
