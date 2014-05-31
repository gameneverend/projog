package org.projog.core.function.math;

import java.util.Arrays;

import org.projog.core.Calculatable;
import org.projog.core.KnowledgeBase;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;
import org.projog.core.term.Variable;

/* SYSTEM TEST
 squared(X,Y) :- Y is X * X.
 
 % %QUERY% squared(3,X)
 % %ANSWER% X=9
 
 % %QUERY% X is squared(3)
 % %EXCEPTION% Cannot find calculatable: squared
 
 % %TRUE% arithmetic_function(squared/1)
 
 % %QUERY% X is squared(3)
 % %ANSWER% X=9
 */
/**
 * <code>arithmetic_function(X)</code> - defines a predicate as an arithmetic function.
 * <p>
 * Allows the predicate defined by <code>X</code> to be used as an arithmetic function.
 */
public class AddArithmeticFunction extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate(Term... args) {
      return evaluate(args[0]);
   }

   /**
    * Overloaded version of {@link #evaluate(Term...)} that avoids the overhead of creating a new {@code Term} array.
    * 
    * @see org.projog.core.Predicate#evaluate(Term...)
    */
   public boolean evaluate(Term arg) {
      final PredicateKey key = PredicateKey.createFromNameAndArity(arg);
      getKnowledgeBase().addCalculatable(key.getName(), new ArithmeticFunction(key));
      return true;
   }

   private static class ArithmeticFunction implements Calculatable {
      final int numArgs;
      final PredicateKey key;

      ArithmeticFunction(PredicateKey originalKey) {
         this.numArgs = originalKey.getNumArgs();
         this.key = new PredicateKey(originalKey.getName(), numArgs + 1);
      }

      @Override
      public Numeric calculate(KnowledgeBase kb, Term[] args) {
         final PredicateFactory pf = kb.getPredicateFactory(key);
         final Variable result = new Variable("result");
         final Term[] argsPlusResult = createArgumentsIncludingResult(args, result);

         if (pf.getPredicate(argsPlusResult).evaluate(argsPlusResult)) {
            return TermUtils.castToNumeric(result);
         } else {
            throw new ProjogException("Could not evaluate: " + key + " with arguments: " + Arrays.toString(args));
         }
      }

      private Term[] createArgumentsIncludingResult(Term[] args, final Variable result) {
         final Term[] argsPlusResult = new Term[numArgs + 1];
         for (int i = 0; i < numArgs; i++) {
            argsPlusResult[i] = args[i].getTerm();
         }
         argsPlusResult[numArgs] = result;
         return argsPlusResult;
      }
   }
}