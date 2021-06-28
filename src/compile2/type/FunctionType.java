package compile2.type;

import java.util.Objects;

public class FunctionType implements Type {

	private final Type domain, codomain;
	
	public FunctionType(Type domain, Type codomain) {
		this.domain = domain;
		this.codomain = codomain;
	}
	
	public boolean equals(Object other) {
		if (other instanceof FunctionType) {
			FunctionType that = (FunctionType) other;
			return Objects.equals(this.domain, that.domain) && Objects.equals(this.codomain, that.codomain);
		}
		return false;
	}
	
	public int hashCode() {
		return Objects.hash(domain, codomain);
	}
	
	public String toString() {
		return domain.toString()+"->"+codomain.toString();
	}

	@Override
	public Type apply(Type type) {
		if (Objects.equals(type, domain))
			return codomain;
		return new ErrorType();
	}

	@Override
	public boolean match(Type other) {
		if (other instanceof FunctionType) {
			FunctionType that = (FunctionType) other;
			return this.domain.match(that.domain) && this.codomain.match(that.codomain);
		}
		return false;
	}
	
	
}
