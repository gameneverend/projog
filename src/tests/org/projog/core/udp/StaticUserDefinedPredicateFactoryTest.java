/*
 * Copyright 2013 S Webber
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
package org.projog.core.udp;

import junit.framework.TestCase;

import org.projog.TestUtils;
import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.function.bool.True;
import org.projog.core.function.flow.RepeatSetAmount;
import org.projog.core.term.Term;
import org.projog.core.udp.compiler.CompiledPredicate;
import org.projog.core.udp.compiler.CompiledTailRecursivePredicate;
import org.projog.core.udp.interpreter.InterpretedTailRecursivePredicateFactory;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Tests {@link StaticUserDefinedPredicateFactory}.
 * <p>
 * NOTE: For these tests to work you need to have "projogGeneratedClasses" in the classpath as that will be the output
 * directory for bytecode generated at runtime.
 * 
 * @see org.projog.TestUtils#COMPILATION_ENABLED_PROPERTIES
 */
public class StaticUserDefinedPredicateFactoryTest extends TestCase {
   private static final KnowledgeBase COMPILATION_ENABLED_KB = TestUtils.createKnowledgeBase(TestUtils.COMPILATION_ENABLED_PROPERTIES);
   private static final KnowledgeBase COMPILATION_DISABLED_KB = TestUtils.createKnowledgeBase(TestUtils.COMPILATION_DISABLED_PROPERTIES);
   private static final String[] RECURSIVE_PREDICATE_SYNTAX = {"concatenate([],L,L).", "concatenate([X|L1],L2,[X|L3]) :- concatenate(L1,L2,L3)."};
   private static final String[] NON_RECURSIVE_PREDICATE_SYNTAX = {"p(X,Y,Z) :- repeat(3), X<Y.", "p(X,Y,Z) :- X is Y+Z.", "p(X,Y,Z) :- X=a."};

   public void testTrue() {
      PredicateFactory pf = getActualPredicateFactory(toTerms("p."));
      assertSame(True.class, pf.getClass());
   }

   public void testRepeatSetAmount() {
      Term[] clauses = toTerms("p.", "p.", "p.");
      int expectedSuccessfulEvaluations = clauses.length;
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(RepeatSetAmount.class, pf.getClass());
      RepeatSetAmount p = ((RepeatSetAmount) pf).getPredicate();
      for (int i = 0; i < expectedSuccessfulEvaluations; i++) {
         assertTrue(p.evaluate());
      }
      assertFalse(p.evaluate());
   }

   public void testSingleRuleWithSingleImmutableArgumentPredicate() {
      Term clause = TestUtils.parseTerm("p(a)");
      PredicateFactory pf = getActualPredicateFactory(clause);
      assertSame(SingleRuleWithSingleImmutableArgumentPredicate.class, pf.getClass());
      SingleRuleWithSingleImmutableArgumentPredicate sr = (SingleRuleWithSingleImmutableArgumentPredicate) pf;
      assertEquals(clause.getArgument(0), sr.data);
   }

   public void testMultipleRulesWithSingleImmutableArgumentPredicate() {
      Term[] clauses = toTerms("p(a).", "p(b).", "p(c).");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(MultipleRulesWithSingleImmutableArgumentPredicate.class, pf.getClass());
      MultipleRulesWithSingleImmutableArgumentPredicate mr = (MultipleRulesWithSingleImmutableArgumentPredicate) pf;
      assertEquals(clauses.length, mr.data.length);
      for (int i = 0; i < clauses.length; i++) {
         assertEquals(clauses[i].getArgument(0), mr.data[i]);
      }
   }

   public void testSingleRuleWithMultipleImmutableArgumentsPredicate() {
      Term clause = TestUtils.parseTerm("p(a,b,c).");
      PredicateFactory pf = getActualPredicateFactory(clause);
      assertSame(SingleRuleWithMultipleImmutableArgumentsPredicate.class, pf.getClass());
      SingleRuleWithMultipleImmutableArgumentsPredicate sr = (SingleRuleWithMultipleImmutableArgumentsPredicate) pf;
      assertEquals(clause.getNumberOfArguments(), sr.data.length);
      for (int i = 0; i < clause.getNumberOfArguments(); i++) {
         assertEquals(clause.getArgument(i), sr.data[i]);
      }
   }

   public void testMultipleRulesWithMultipleImmutableArgumentsPredicate() {
      Term[] clauses = toTerms("p(a,b,c).", "p(1,2,3).", "p(x,y,z).");
      PredicateFactory pf = getActualPredicateFactory(clauses);
      assertSame(MultipleRulesWithMultipleImmutableArgumentsPredicate.class, pf.getClass());
      MultipleRulesWithMultipleImmutableArgumentsPredicate mr = (MultipleRulesWithMultipleImmutableArgumentsPredicate) pf;
      assertEquals(clauses.length, mr.data.length);
      for (int c = 0; c < clauses.length; c++) {
         assertEquals(clauses[c].getNumberOfArguments(), mr.data[c].length);
         for (int a = 0; a < clauses[c].getNumberOfArguments(); a++) {
            assertEquals(clauses[c].getArgument(a), mr.data[c][a]);
         }
      }
   }

   public void testInterpretedTailRecursivePredicateFactory() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedTailRecursivePredicateFactory.class, pf.getClass());
   }

   public void testCompiledTailRecursivePredicate() {
      PredicateFactory pf = getActualPredicateFactory(COMPILATION_ENABLED_KB, toTerms(RECURSIVE_PREDICATE_SYNTAX));
      assertTrue(pf instanceof CompiledPredicate);
      assertTrue(pf instanceof CompiledTailRecursivePredicate);
   }

   public void testInterpretedUserDefinedPredicate() {
      PredicateFactory pf = getActualPredicateFactory(toTerms(NON_RECURSIVE_PREDICATE_SYNTAX));
      assertSame(InterpretedUserDefinedPredicate.class, pf.getPredicate().getClass());
   }

   public void testCompiledPredicate() {
      PredicateFactory pf = getActualPredicateFactory(COMPILATION_ENABLED_KB, toTerms(NON_RECURSIVE_PREDICATE_SYNTAX));
      assertTrue(pf instanceof CompiledPredicate);
      assertFalse(pf instanceof CompiledTailRecursivePredicate);
   }

   private PredicateFactory getActualPredicateFactory(Term... clauses) {
      return getActualPredicateFactory(COMPILATION_DISABLED_KB, clauses);
   }

   private PredicateFactory getActualPredicateFactory(KnowledgeBase kb, Term... clauses) {
      StaticUserDefinedPredicateFactory f = null;
      for (Term clause : clauses) {
         if (f == null) {
            PredicateKey key = PredicateKey.createForTerm(clause);
            f = new StaticUserDefinedPredicateFactory(key);
            f.setKnowledgeBase(kb);
         }
         ClauseModel clauseModel = ClauseModel.createClauseModel(clause);
         f.addLast(clauseModel);
      }
      return f.getActualPredicateFactory();
   }

   private Term[] toTerms(String... clausesSyntax) {
      Term[] clauses = new Term[clausesSyntax.length];
      for (int i = 0; i < clauses.length; i++) {
         clauses[i] = TestUtils.parseSentence(clausesSyntax[i]);
      }
      return clauses;
   }

   private void assertEquals(Term t1, Term t2) {
      assertTrue("Term: " + t1 + " is not strictly equal to term: " + t2, t1.strictEquality(t2));
   }
}