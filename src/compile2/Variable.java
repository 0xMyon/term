package compile2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import compile2.type.Type;

/**
 * Representation of variable terms
 * 
 * @author 0xMyon
 *
 */
public class Variable extends Expression {
	
	private static int ID = 0;
	public final int id = ID++;
	public final String name;
	
	private final Type type;
	
	public Variable(Variable var) {
		this.type = var.type;
		this.name = var.name;
	}
	
	public Variable(Type type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Variable(Type type) {
		this(type, "");
	}
	
		
	@Override
	public Optional<? extends Variable> findMatch(Expression that, Map<Variable, Expression> map) {
		if (map.containsKey(this)) {
			Expression value = map.get(this);
			if (value instanceof Variable) {
				return value.isIdentical(that) ? Optional.of(this) : Optional.empty();
			}
			return value.hasMatch(that, new HashMap<>(map)) ? Optional.of(this) : Optional.empty();
		} else if (type().match(that.type())) {
			map.put(this, that);
			return Optional.of(this);
		}
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		return "$"+(name.isEmpty()?"":(name+":"))+id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Variable.class);
	}
	

	@Override
	public Stream<Variable> freeVars() {
		return Stream.of(this);
	}
	
	@Override
	protected void finalize() {
		if (ID == id+1) {
			ID--;
		}
	}
	
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.handle(this);
	}

	@Override
	public boolean isIdentical(Expression other) {
		if (other instanceof Variable) {
			Variable that = (Variable) other;
			return Objects.equals(this.id, that.id);
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

	public int distance2(Expression other, Map<Variable, Variable> map) {
		if (other instanceof Variable) {
			Variable that = (Variable) other;
			if (!map.containsKey(this)) {
				map.put(this, that);
				return 0;
			} else {
				return map.get(this).id == that.id ? 0 : 1;
			}
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
