package compile2.type;

public class ErrorType implements Type {

	@Override
	public boolean match(Type that) {
		return false;
	}
	
}
