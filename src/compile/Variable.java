package compile;

import java.util.Map;


public class Variable<T> implements Expression<T> {

	private static int ID = 0;
	private final int id = ID++;
	
	@Override
	public T evaluate() {
		throw new RuntimeException("Variable ca not be evaluated");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> Expression<T> replace(Variable<X> x, Expression<X> y) {
		if (equals(x)) {
			return (Expression<T>) y;
		} else {
			return this;
		}
	}
	
	
	public String toString() {
		return "$"+id+"";
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
				return false;
			}
			@Override
			public Boolean handle(Variable<?> that) {
				if (map.containsKey(Variable.this)) {
					return map.get(Variable.this).equals(that);
				} else {
					map.put(Variable.this, that);
					return true;
				}
			}
		});
	}

	@Override
	public <X> X accept(Visitor<X> visitor) {
		return visitor.handle(this);
	}



}
