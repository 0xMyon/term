package compile;

import java.util.Map;

public class Named<T> implements Expression<T> {

	private final Expression<T> proxy;
	private final String name;
	
	Named(Expression<T> proxy, String name) {
		this.proxy = proxy;
		this.name = name;
	}
	
	@Override
	public T evaluate() {
		return proxy.evaluate();
	}

	@Override
	public <X> Expression<T> replace(Variable<X> x, Expression<X> y) {
		return new Named<T>(proxy.replace(x, y), name);
	}
	
	public String toString() {
		return name;
	}

	@Override
	public boolean isEqual(Expression<?> other, Map<Variable<?>, Variable<?>> map) {
		return proxy.isEqual(other, map);
	}

	@Override
	public <X> X accept(Visitor<X> visitor) {
		return visitor.handle(this);
	}

	Expression<T> getExpression() {
		return proxy;
	}

}
