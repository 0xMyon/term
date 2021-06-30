package compile2;

public class ExpressionClass {

	public final Expression expression;
	
	public ExpressionClass(Expression e) {
		this.expression = e;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Expression) {
			return expression.isEqual((Expression)other);
		} else if (other instanceof ExpressionClass) {
			return expression.isEqual(((ExpressionClass)other).expression);
		}
		return false;
	}
	
	public String toString() {
		return expression.toString();
	}
	
	public int hashCode() {
		return expression.hashCode();
	}
	
	public int distance(ExpressionClass e) {
		return expression.distance(e.expression);
	}
	public int distance2(ExpressionClass e) {
		return expression.distance2(e.expression);
	}
	
	public int simplicity() {
		return expression.simplicity();
	}
	
}
