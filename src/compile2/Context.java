package compile2;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Context {

	// NO MAP
	private final Set<Transition> axioms = new HashSet<>();
	
	private final Set<Expression> invalids = new HashSet<>();
	
	public void addOnly(Expression left, Expression right) {
		axioms.add(new Transition(left, right));
	}
		
	
	public void add(Expression left, Expression right) {
		Transition t = new Transition(left, right);
		
		boolean all = true;
		
		if (all || t.varBilance() <= 0)
			axioms.add(t);
		else
			System.out.println("NOT "+t);
		
		if (all ||  t.varBilance() == 0)
			axioms.add(t.inverse());
		else 
			System.out.println("NOT "+t.inverse());
	}
	
	Stream<ExpressionClass> next(ExpressionClass e) {
		return axioms.stream().map(x -> e.expression.apply(x)).reduce(Stream.of(), Stream::concat).filter(this::isValid).map(Expression::CLASS);
	}
	
	public String toString() {
		return axioms.toString();
	}

	public void invalid(Expression expression) {
		this.invalids.add(expression);
	}
	
	public boolean isValid(Expression e) {
		
		boolean res =  !invalids.stream().anyMatch(inv -> e.findMatches(inv).count() > 0);
		if (e.toString().startsWith("/(") && e.toString().endsWith(")(0)") && !res) {
			//System.out.println("???");
		}
		return res;
	}
	
	
}
