package compile2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import compile2.type.Type;

/**
 * {@link Expression}s representing trees of operators, {@link Constant}s and {@link Variable}s
 * 
 * @author 0xMyon
 */
public abstract class Expression {

	
	abstract int simplicity();
	
	
	static Expression forAll(Function<Expression, Expression> f, Type t) {
		return f.apply(new Variable(t));
	}
	
	public abstract Type type();
	
	
	/**
	 * Applies parameters to a function {@link Expression}
	 * @param params parameters to be applied
	 * @return a new {@link Expression} representing the function {@link Application}
	 * @see #apply(Expression)
	 */
	public final Expression apply(Expression... params) {
		Expression result = this;
		for(Expression param : params) {
			result = result.apply(param);
		}
		return result;
	}
	
	/**
	 * Applies a parameter to a function {@link Expression}
	 * @param param parameter to be applied
	 * @return a new {@link Expression} representing the function {@link Application}
	 */
	public Expression apply(Expression param) {
		return Application.of(this, param);
	}
	
	/**
	 * Replaces all exact occurrences of an {@link Expression} with another
	 * @param that {@link Expression} to be replaced
	 * @param replacement
	 * @return a new {@link Expression} where all occurrences are replaced
	 * @see #isIdentical(Expression)
	 */
	public final Expression replace(Expression that, Expression replacement) {
		return this.isIdentical(that) ? replacement : replaceR(that, replacement);
	}
	
	/**
	 * Replaces all occurrences (by {@link #equals(Object)}) of an {@link Expression} with another  (recursive call)
	 * @param that {@link Expression} to be replaced
	 * @param replacement
	 * @return a new {@link Expression} where all occurrences are replaced
	 * @see #replace(Expression, Expression)
	 */
	public Expression replaceR(Expression that, Expression replacement) {
		return this;
	}
	
	/**
	 * Replaces all {@link Expression} keys by {@link Expression} values
	 * @param map that contains the replacement
	 * @return a new {@link Expression} where all occurrences are replaced
	 * @see #replace(Expression, Expression) 
	 */
	public final Expression replaceAll(Map<? extends Expression, ? extends Expression> map) {
		Expression result = this;
		for(Entry<? extends Expression, ? extends Expression> e : map.entrySet()) {
			result = result.replace(e.getKey(), e.getValue());
		}
		return result;
	}
	
	/**
	 * Finds all matches of an {@link Expression} within sub-expressions
	 * @param that {@link Expression} to be matched
	 * @return {@link Stream} of all matches (for each element of the stream the expression is only matched once)
	 */
	public final Stream<Map<Variable, Expression>> findMatches(Expression that) {
		Map<Variable, Expression> map = new HashMap<>();
		Optional<Expression> match = that.findMatch(this, map);
		if (match.isPresent()) {
			map.put(null, match.get());
			return Stream.concat(findMatchesR(that), Stream.of(map));
		} else {
			return findMatchesR(that);
		}
	}
	
	/**
	 * Finds all matches of an {@link Expression} within sub-expressions (recursive call)
	 * @param that {@link Expression} to be matched
	 * @return {@link Stream} of all matches in sub-expressions
	 * @see #findMatches(Expression)
	 */
	public Stream<Map<Variable, Expression>> findMatchesR(Expression that) {
		return Stream.of();
	}

	/**
	 * gets the mapping of a match for displaying purposes
	 * @param that sub-expression to be searched for
	 * @return the mapping
	 * @see #findMatch(Expression, Map)
	 */
	@Deprecated
	public final Map<Variable, Expression> getMatch(Expression that) {
		Map<Variable,Expression> map = new HashMap<>();
		return null != findMatch(that, map) ? map : null;
	}
	
	public abstract boolean isIdentical(Expression that);
	
	/**
	 * Checks if two {@link Expression}s are equal up to variable names 
	 * @param that expression to be compared to
	 * @return true, if the expressions are equal up to variable names
	 */
	public final boolean isEqual(Expression that) {
		return this.hasMatch(that) && that.hasMatch(this);
	}
	
	/**
	 * @see #hasMatch(Expression, Map)
	 */
	public final boolean hasMatch(Expression that) {
		return hasMatch(that, new HashMap<>());
	}
	
	
	/**
	 * 
	 * Checks if there is a variable assignment of this {@link Expression} such that, 
	 * if replaced both expressions would be equal {@link #equals(Object)}.
	 * @param that {@link Expression} that is mapped to
	 * @param map mapping that is pre-defined
	 * @return true, if such a mapping exists
	 * @see #findMatch(Expression, Map)
	 */
	public final boolean hasMatch(Expression that, Map<Variable, Expression> map) {
		 return findMatch(that, map).isPresent();
	}
	
	/**
	 * Finds a match to that {@link Expression} in this {@link Expression}
	 * @param that sub-expression to be searched for
	 * @param map mapping to revert the match such that {@code this.findMatch(that, map).replaceAll(map) == that}
	 * @return null, if no match was found or a new {@link Expression} with {@link Variable} inserted where they matched
	 */
	public abstract Optional<Expression> findMatch(Expression that, Map<Variable, Expression> map);
	
	/**
	 * replaces matches with an {@link Expression} with another
	 * @param left {@link Expression} to be matched
	 * @param right {@link Expression} to be replaces
	 * @return a {@link Stream} of new {@link Expression} where matching was replaced
	 * such that the original is contained in the result the inverse transition being applied  
	 * @see #findMatches(Expression)
	 */
	public final Stream<Expression> transit(Expression left, Expression right) {
		return rename(right.freeVars()).findMatches(left).map(m -> 
			m.get(null).replace(left, right).replaceAll(m)
		).filter(Expression::isValid);
	}
	

	/**
	 * applies a transition
	 * @param that {@link Transition} to be applied
	 * @return {@link Stream} of new {@link Expression} 
	 * @see #transit(Expression, Expression)
	 */
	public final Stream<Expression> apply(Transition that) {
		return transit(that.source, that.target);
	}
	
	/**
	 * renames all variables
	 * @param variables to be renamed 
	 * @return {@link Expression} with variables renamed
	 */
	public final Expression rename(Stream<Variable> variables) {
		Expression result = this;
		Iterator<Variable> it = variables.iterator();
		while(it.hasNext()) {
			Variable var = it.next();
			result = result.replace(var, new Variable(var));
		}
		return result;
	}
	
	/**
	 * Gets all free variables of the {@link Expression}
	 * @return all free variables
	 */
	public abstract Stream<Variable> freeVars();
	
	
	/**
	 * 
	 * @return
	 */
	public abstract boolean isValid();
	
	public final int distance(Expression that) {
		return isEqual(that) ? 0 : Util.distance(this.toString(), that.toString());
	}
	
	public final ExpressionClass CLASS() {
		return new ExpressionClass(this);
	}
	
	@Override
	public final boolean equals(Object other) {
		if (other instanceof Expression)
			return isIdentical((Expression)other);
		else if (other instanceof ExpressionClass) {
			return isEqual(((ExpressionClass)other).expression);
		}
		return false;
	}
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract int hashCode();
	
	
	public abstract <R> R accept(Visitor<R> visitor);
	
	interface Visitor<R> {
		R handle(Application that);
		R handle(Constant that);
		R handle(Lambda that);
		R handle(Variable that);
		R handle(Restriction restriction);
	}
	
	public Expression no(Variable x) {
		return Restriction.of(this, x);
	}


	protected boolean isConstant() {
		return this.freeVars().count() == 0;
	}
	
	static <T, U, R> Optional<R> build(BiFunction<T, U, R> f, Optional<T> t, Optional<U> u) {
		return (t.isPresent() && u.isPresent()) ? Optional.of(f.apply(t.get(), u.get())) : Optional.empty();
	}
	
}
