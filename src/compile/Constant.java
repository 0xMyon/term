package compile;

import java.util.Map;


public class Constant<T> implements Expression<T> {

	private final T value;
	
	public Constant(T value) {
		this.value = value;
	}

	@Override
	public T evaluate() {
		return value;
	}

	@Override
	public <X> Expression<T> replace(Variable<X> x, Expression<X> y) {
		return this;
	}

	public String toString() {
		return value.toString();
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
				return value.equals(that.value);
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
