package compile;

import java.util.Map;
import java.util.function.Function;

public class Lambda<T,R> implements Expression<Function<T,R>>
{

	private final Expression<R> expression;
	private final Variable<T> variable;
	
	
	public Expression<R> apply(Expression<T> value) {
		return expression.replace(variable, value);
	}
	
	@Override
	public Function<T, R> evaluate() {
		return t -> apply(Expression.CONST(t)).evaluate();
	}

	@Override
	public <X> Expression<Function<T, R>> replace(Variable<X> x, Expression<X> y) {
		Variable<T> n = new Variable<>();
		return new Lambda<>(n, expression.replace(variable, n).replace(x, y));
	}
	
	private Lambda(Variable<T> variable, Expression<R> expression) {
		this.variable = variable;
		this.expression = expression;
	}
	
	public Lambda(Function<Expression<T>, Expression<R>> f) {
		this(new Variable<>(), f);
	}

	private Lambda(Variable<T> variable, Function<Expression<T>, Expression<R>> f) {
		this(variable, f.apply(variable));
	}

	public String toString() {
		return "λ."+variable+":"+expression;
	}
	
	
	@Override
	public boolean isEqual(Expression<?> other, Map<Variable<?>, Variable<?>> map) {
		return other.accept(new Visitor<Boolean>() {
			@Override
			public Boolean handle(Application<?, ?> that) {
				return false;
			}
			@Override
			public Boolean handle(Constant<?> that) {
				return false;
			}
			@Override
			public Boolean handle(Lambda<?, ?> that) {
				return variable.isEqual(that.variable, map) && expression.isEqual(that.expression, map);
			}
			@Override
			public Boolean handle(Variable<?> that) {
				return false;
			}
		});
	}

	@Override
	public <X> X accept(Visitor<X> visitor) {
		return visitor.handle(this);
	}

}
