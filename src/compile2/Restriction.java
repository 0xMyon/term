package compile2;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import compile2.type.Type;

public class Restriction extends Expression {

	private final Expression expression;
	private final Variable variable;
	
	private Restriction(Expression expression, Variable variable) {
		this.expression = expression;
		this.variable = variable;
	}
	
	
	@Override
	int simplicity() {
		return expression.simplicity();
	}

	@Override
	public Type type() {
		return expression.type();
	}

	@Override
	public boolean isIdentical(Expression other) {
		if (other instanceof Restriction) {
			Restriction that = (Restriction) other;
			return this.expression.isIdentical(that.expression) && this.variable.isIdentical(that.variable);
		}
		return false;
	}

	@Override
	public Optional<Expression> findMatch(Expression that, Map<Variable, Expression> map) {
		return expression.findMatch(that, map).map(e -> of(e, variable)).filter(e -> e.replaceAll(map).isValid());
	}

	@Override
	public Stream<Variable> freeVars() {
		return expression.freeVars();
	}

	@Override
	public String toString() {
		return expression.toString()+"["+variable.toString()+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(expression, variable);
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.handle(this);
	}


	
	public static Expression of(Expression expression, Variable variable) {
		if (expression.isConstant())
			return expression;
		else
			return new Restriction(expression, variable);
	}
	
	@Override
	public boolean isValid() {
		return !expression.freeVars().anyMatch(variable::equals);
	}
	
	/*
	private static Expression of(Expression expression, Expression variable) {
		if (variable instanceof Variable)
			return of(expression, (Variable) variable);
		return expression;
	}
	*/
	
	
	@Override
	public Expression replaceR(Expression variable, Expression param) {
		Expression ve = this.variable.replace(variable, param);
		if (ve instanceof Variable)
			return of(expression.replace(variable, param), (Variable) ve);
		else
			return expression.replace(variable, param);
	}
	
	
	Function<Map<Variable, Expression>, Map<Variable, Expression>> f(Function<Expression, Expression> e) {
		return m -> {
			m.put(null, e.apply(m.get(null)));
			return m;
		};
	}


	/*
	public Stream<Map<Variable, Expression>> findMatchesR(Expression that) {
		return expression.findMatches(that).map(m -> {
			m.put(null, of(m.get(null), variable));
			return m;
		}).filter(m -> m.get(null).isValid());
	}
	*/

	
	
}
