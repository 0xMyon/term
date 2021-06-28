package compile;

import java.util.Map;
import java.util.function.Function;

public class Application<T, R> implements Expression<R> {

	private final Expression<Function<T,R>> function;
	private final Expression<T> parameter;
	
	public static <T,R> Expression<R> of(Expression<Function<T,R>> function, Expression<T> parameter) {
		if (function instanceof Lambda) {
			@SuppressWarnings("unchecked")
			Lambda<T, R> lambda = (Lambda<T, R>) function;
			return lambda.apply(parameter);
		} else {
			return new Application<>(function, parameter);
		}
	}
	
	private Application(Expression<Function<T,R>> function, Expression<T> parameter) {
		this.function = function;
		this.parameter = parameter;
	}

	@Override
	public R evaluate() {
		return function.evaluate().apply(parameter.evaluate());
	}

	@Override
	public <X> Application<T, R> replace(Variable<X> x, Expression<X> y) {
		return new Application<>(function.replace(x, y), parameter.replace(x, y));
	}

	public String toString() {
		return function.toString()+"("+parameter.toString()+")";
	}

	
	@Override
	public boolean isEqual(Expression<?> other, Map<Variable<?>, Variable<?>> map) {
		return other.accept(new Visitor<Boolean>() {
			@Override
			public Boolean handle(Application<?, ?> that) {
				return function.isEqual(that.function, map) && parameter.isEqual(that.parameter, map);
			}
			@Override
			public Boolean handle(Constant<?> that) {
				return false;
			}
			@Override
			public Boolean handle(Lambda<?, ?> that) {
				return false;
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
