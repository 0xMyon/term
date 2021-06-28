package compile2.type;

public interface Type {

	default Type apply(Type type) {
		return new ErrorType();
	}

	default Type to(Type that) {
		return new FunctionType(this, that);
	}

	boolean match(Type that);
	
}
