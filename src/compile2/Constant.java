package compile2;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import compile2.type.Type;

/**
 * Representation of constant {@link Expression}s. 
 * This includes 0-airy, true constants and n-airy functions.
 * 
 * @author 0xMyon
 *
 */
public class Constant extends Expression {

	private final String name;
	
	private final Type type;
	
	
	public Constant(String name, Type type) {
		this.name = name;
		this.type = type;
	}


	@Override
	public Optional<Expression> findMatch(Expression other, Map<Variable, Expression> map) {
		return isIdentical(other) ? Optional.of(other) : Optional.empty();
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	@Override
	public Stream<Variable> freeVars() {
		return Stream.of();
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.handle(this);
	}
	
	@Override
	public boolean isIdentical(Expression other) {
		if (other instanceof Constant) {
			Constant that = (Constant) other;
			return Objects.equals(this.name, that.name);
		}
		return false;
	}


	@Override
	public Type type() {
		return type;
	}


	@Override
	int simplicity() {
		return 1;
	}


	@Override
	public boolean isValid() {
		return true;
	}


	@Override
	public int distance2(Expression other, Map<Variable, Variable> map) {
		if (other instanceof Constant) {
			Constant that = (Constant) other;
			return this.name.equals(that.name) ? 0 : 1;
		}
		return super.distance2(other, map);
	}


	@Override
	public int size() {
		return 1;
	}


	@Override
	public Expression replaceR(Expression that, Expression replacement) {
		return this;
	}


	
	
}
