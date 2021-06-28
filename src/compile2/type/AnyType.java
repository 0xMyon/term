package compile2.type;

public class AnyType implements Type {


	@Override
	public boolean match(Type that) {
		return true;
	}

}
