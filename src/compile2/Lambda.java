package compile2;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import compile2.type.AnyType;
import compile2.type.FunctionType;
import compile2.type.Type;


/**
 * Representation of lambda function.
 * By binding a {@link Variable} a new function is created,
 * that may be evaluated by replacing it with any expression.
 * @see #apply(Expression)
 *  
 * @author 0xMyon
 *
 */
public class Lambda extends Expression {

	private final Variable variable;
	private final Expression expression;
	
	
	public Lambda(Function<Expression, Expression> lambda) {
		this(lambda, new AnyType());
	}
	
	public Lambda(Function<Expression, Expression> lambda, Type type) {
		this.variable = new Variable(type);
		this.expression = lambda.apply(variable);
	}
	
	public Lambda(Variable variable, Expression expression) {
		this.variable = new Variable(variable.type());
		this.expression = expression.replace(variable, this.variable);
	}
		
	public static Expression of(Variable variable, Expression expression) {
		if (null == variable || null == expression) {
			return null;
		}
		return new Lambda(variable, expression);
	}
	
	@Override
	public Expression apply(Expression param) {
		return expression.replace(variable, param);
	}

	@Override
	public Expression replaceR(Expression variable, Expression param) {
		return new Lambda(this.variable, expression.replace(variable, param));
	}
	
	@Override
	public Stream<Map<Variable, Expression>> findMatchesR(Expression that) {
		return expression.findMatches(that).map(m -> {
			m.put(null, new Lambda(variable, m.get(null)));
			return m;
		});
	}
	
	@Override
	public Optional<Expression> findMatch(Expression other, Map<Variable, Expression> map) {
		if (other instanceof Lambda) {
			Lambda that = (Lambda) other;
			return this.expression.findMatch(that.expression, map).map(e -> of(variable, e));
		}
		return Optional.empty();
	}
		
	@Override
	public int hashCode() {
		return Objects.hash(variable, expression);
	}
	
	@Override
	public String toString() {
		return "λ."+variable.toString()+" : "+expression.toString();
	}

	@Override
	public Stream<Variable> freeVars() {
		return expression.freeVars().filter(v->!v.isIdentical(variable));
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.handle(this);
	}
	
	
	@Override
	public boolean isIdentical(Expression other) {
		if (other instanceof Lambda) {
			Lambda that = (Lambda) other;
			return variable.isIdentical(that.variable) && expression.isIdentical(that.expression);
		}
		return false;
	}

	@Override
	public Type type() {
		return new FunctionType(variable.type(), expression.type());
	}

	@Override
	int simplicity() {
		return 1 + variable.simplicity() + expression.simplicity();
	}

	@Override
	public boolean isValid() {
		return expression.isValid();
	}

}
