package compile2.type;

import java.util.Objects;

public class UserType implements Type {

	public UserType(String name) {
		this.name = name;
	}
	
	private final String name;
	
	public boolean equals(Object other) {
		if (other instanceof UserType) {
			UserType that = (UserType) other;
			return Objects.equals(this.name, that.name);
		}
		return false;
	}
	
	public int hashCode() {
		return Objects.hash(name);
	}
	
	public String toString() {
		return name;
	}

	

	@Override
	public boolean match(Type that) {
		return Objects.equals(this, that);
	}
	
}
