package compile2;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import compile2.type.Type;

/**
 * Representation of a functional application.
 * 
 * @author 0xMyon
 */
public class Application extends Expression {

	private final Expression function;
	private final Expression parameter;
	
	
	private Application(Expression function, Expression parameter) {
		this.function = function;
		this.parameter = parameter;
	}
	
	static Expression of(Expression function, Expression parameter) {
		if (null == function || null == parameter)
			return null;
		return new Application(function, parameter);
	}

	@Override
	public Expression replaceR(Expression variable, Expression param) {
		return new Application(function.replace(variable, param), parameter.replace(variable, param));
	}
	
	public Stream<Map<Variable, Expression>> findMatchesR(Expression that) {
		return Stream.concat(function.findMatches(that).map(m -> {
			m.put(null, new Application(m.get(null), parameter));
			return m;
		}), parameter.findMatches(that).map(m -> {
			m.put(null, new Application(function, m.get(null)));
			return m;
		}));
	}

	@Override
	public Optional<Expression> findMatch(Expression other, Map<Variable, Expression> map) {
		if (other instanceof Application) {
			final Application that = (Application) other;
			return build(Application::new, 
				this.function.findMatch(that.function, map),
				this.parameter.findMatch(that.parameter, map)
			);
		}
		return Optional.empty();
	}
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(function, parameter);
	}
	
	@Override
	public String toString() {
		return function.toString()+"("+parameter.toString()+")";
	}

	@Override
	public Stream<Variable> freeVars() {
		return Stream.concat(function.freeVars(), parameter.freeVars()).distinct();
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.handle(this);
	}

	@Override
	public boolean isIdentical(Expression other) {
		if (other instanceof Application) {
			Application that = (Application) other;
			return function.isIdentical(that.function) && parameter.isIdentical(that.parameter);
		}
		return false;
	}

	@Override
	public Type type() {
		return function.type().apply(parameter.type());
	}

	@Override
	int simplicity() {
		return 1 + function.simplicity() + parameter.simplicity();
	}

	@Override
	public boolean isValid() {
		return function.isValid() && parameter.isValid();
	}
	
}
