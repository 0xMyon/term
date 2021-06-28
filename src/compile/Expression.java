package compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public interface Expression<T> {

	/**
	 * @return the evaluated {@link Expression}
	 */
	T evaluate();
	
	
	/**
	 * @param <X> type of the expression to be replaces
	 * @param variable that is replaces
	 * @param expression that it is replaces by
	 * @return a new {@link Expression} where variable is replaced by expression
	 */
	<X> Expression<T> replace(Variable<X> variable, Expression<X> expression);
	
	
	default Expression<T> name(String name) {
		return new Named<T>(this, name);
	}
	
	public static final Expression<Function<Integer, Function<Integer, Integer>>> PLUS = CONST(x->y->x+y, "+");
	public static final Expression<Function<Integer, Function<Integer, Integer>>> TIMES = CONST(x->y->x*y, "*");
	
	public static final Expression<Function<Object, Function<Object, Boolean>>> EQUAL = CONST(x->y->Objects.equals(x, y), "=");
	
	//public static final Expression<Function<Object, Function<Function<Object, Object>, Object>>> LIM = CONST(c -> f ->  )
	
	/**
	 * @param <T> type of the expression
	 * @param value that is constant
	 * @return a new {@link Constant} {@link Expression}
	 */
	public static <T> Expression<T> CONST(T value) {
		return new Constant<T>(value);
	}
	/**
	 * @return a new {@link Constant} with name
	 * @see #CONST(T)
	 * @see #name(String)
	 */
	public static <T> Expression<T> CONST(T value, String name) {
		return CONST(value).name(name);
	}
	
	/**
	 * Applies parameters to a unary function
	 * @param <T> type of the parameter
	 * @param <R> type of the return value
	 * @param function to be applied
	 * @param parameter to be passed 
	 * @return new {@link Expression} where function is applied to parameter
	 */
	public static <T,R> Expression<R> apply(Expression<Function<T,R>> function, Expression<T> parameter) {
		return Application.of(function, parameter);
	}
	/**
	 * Applies parameters to a binary function
	 * @see Expression#apply(Expression, Expression)
	 */
	public static <T,U,R> Expression<R> apply(Expression<Function<T,Function<U,R>>> f, Expression<T> a, Expression<U> b) {
		return apply(apply(f, a), b);
	}
	
	
	static <T,R> R forAll(Class<T> x, Function<Expression<T>, R> f) {
		return forAll(f);
	}
	
	static <T,R> R forAll(Function<Expression<T>, R> x) {
		return x.apply(new Variable<T>());
	}
	
	static <T,R> Expression<Function<T,R>> lambda(Function<Expression<T>, Expression<R>> f) {
		return new Lambda<>(f);
	}
	static <T,R> Expression<Function<T,R>> lambda(Class<T> hint, Function<Expression<T>, Expression<R>> f) {
		return lambda(f);
	}


	default boolean isEqual(Expression<?> other) {
		return isEqual(other, new HashMap<>());
	}
	boolean isEqual(Expression<?> other, Map<Variable<?>, Variable<?>> map);
	
	
	static interface Visitor<X> {
		X handle(Application<?, ?> that);
		X handle(Constant<?> that);
		X handle(Lambda<?, ?> that);
		X handle(Variable<?> that);
		default X handle(Named<?> named) {
			return named.getExpression().accept(this);
		}
	}
	
	<X> X accept(Visitor<X> visitor);
	
	
}
