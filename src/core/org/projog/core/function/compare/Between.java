package org.projog.core.function.compare;

import static org.projog.core.term.NumericTermComparator.NUMERIC_TERM_COMPARATOR;
import static org.projog.core.term.TermUtils.toLong;

import org.projog.core.KnowledgeBase;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.IntegerNumber;
import org.projog.core.term.Term;

/* TEST
 %TRUE between(1, 5, 1)
 %TRUE between(1, 5, 2)
 %TRUE between(1, 5, 3)
 %TRUE between(1, 5, 4)
 %TRUE between(1, 5, 5)
 
 %FALSE between(1, 5, 0)
 %FALSE between(1, 5, -1)
 %FALSE between(1, 5, -9223372036854775808)

 %FALSE between(1, 5, 6)
 %FALSE between(1, 5, 7)
 %FALSE between(1, 5, 9223372036854775807)

 %TRUE between(-9223372036854775808, 9223372036854775807, -9223372036854775808)
 %TRUE between(-9223372036854775808, 9223372036854775807, -1)
 %TRUE between(-9223372036854775808, 9223372036854775807, 0)
 %TRUE between(-9223372036854775808, 9223372036854775807, 1)
 %TRUE between(-9223372036854775808, 9223372036854775807, 9223372036854775807)
 
 %QUERY between(1, 1, X)
 %ANSWER X=1
 
 %QUERY between(1, 2, X)
 %ANSWER X=1
 %ANSWER X=2
 
 %QUERY between(1, 5, X)
 %ANSWER X=1
 %ANSWER X=2
 %ANSWER X=3
 %ANSWER X=4
 %ANSWER X=5
   
 %FALSE between(5, 1, X)
 
 %TRUE between(5-2, 2+3, 2*2)
 %FALSE between(5-2, 2+3, 8-6)
 */
/**
 * <code>between(X,Y,Z)</code> - checks if a number is within a specified range.
 * <p>
 * <code>between(X,Y,Z)</code> succeeds if the integer numeric value represented by <code>Z</code> is greater than or equal to the integer numeric value represented by <code>X</code>
 * and is less than or equal to the integer numeric value represented by <code>Y</code>.
 * </p>
 * <p>
 * If <code>Z</code> is an uninstantiated variable then <code>Z</code> will be successively unified with all integer values in the range from <code>X</code> to </code>Y</code>.
 * </p>
 */
public final class Between implements PredicateFactory {
   private final Singleton singleton = new Singleton();
   private KnowledgeBase kb;

   @Override
   public Predicate getPredicate(Term... args) {
      return getPredicate(args[0], args[1], args[2]);
   }

   public Predicate getPredicate(Term low, Term high, Term middle) {
      if (middle.getType().isVariable()) {
         return new Retryable(toLong(kb, low), toLong(kb, high));
      } else {
         return singleton;
      }
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      this.kb = kb;
      singleton.setKnowledgeBase(kb);
   }

   private static class Singleton extends AbstractSingletonPredicate {
      @Override
      protected boolean evaluate(Term low, Term high, Term middle) {
         final KnowledgeBase kb = getKnowledgeBase();
         return NUMERIC_TERM_COMPARATOR.compare(low, middle, kb) < 1 && NUMERIC_TERM_COMPARATOR.compare(middle, high, kb) < 1;
      }
   };

   private static class Retryable implements Predicate {
      final long max;
      long ctr;

      Retryable(long start, long max) {
         this.ctr = start;
         this.max = max;
      }

      @Override
      public boolean evaluate(Term... args) {
         return evaluate(args[0], args[1], args[2]);
      }

      private boolean evaluate(Term low, Term high, Term middle) {
         while (couldReEvaluationSucceed()) {
            middle.backtrack();
            IntegerNumber n = new IntegerNumber(ctr++);
            return middle.unify(n);
         }
         return false;
      }

      @Override
      public boolean isRetryable() {
         return true;
      }

      @Override
      public boolean couldReEvaluationSucceed() {
         return ctr <= max;
      }
   };
}