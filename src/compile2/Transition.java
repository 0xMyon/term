package compile2;

import java.util.Objects;
import java.util.stream.Stream;

public class Transition {

	public final Expression source, target;
	
	Transition(Expression source, Expression target) {
		this.source = source;
		this.target = target;
	}
	
	
	public Stream<Expression> applyTo(Expression that) {
		return that.apply(this);
	}
	
	
	/**
	 * combines two {@link Transition}s
	 * @param that
	 * @return
	 */
	public Stream<Transition> andThen(Transition that) {
		return that.source.transit(this.target, this.source).map(x -> new Transition(x, that.target));
	}
	
	/**
	 * creates an inverse {@link Transition} such that the invert of the invert is the original
	 * @return the inverse
	 */
	public Transition inverse() {
		return new Transition(target, source);
	}
	
	public boolean equals(Object other) {
		if (other instanceof Transition) {
			Transition that = (Transition) other;
			return this.source.isEqual(that.source) && this.target.isEqual(that.target);
		}
		return false;
	}
	public int hashCode() {
		return Objects.hash(source, target);
	}


	public long varBilance() {
		return target.freeVars().count() - source.freeVars().count();
	}
	
	public String toString() {
		return source+" => "+target;
	}
	
}
