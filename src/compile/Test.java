package compile;

import static compile.Expression.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

class Test {

	@org.junit.jupiter.api.Test
	void test() {
		
		System.out.println("λ");
		
		System.out.println(apply(TIMES, CONST(1), CONST(2)));
		
		
		System.out.println(apply(lambda(Integer.class, x ->  apply(TIMES, x, CONST(2))), CONST(3)));
		
		System.out.println(lambda(Integer.class, x-> lambda(Integer.class, y -> apply(TIMES, x, y))));
		
		// ' = f -> lim(h->0) (f(x+h)-f(x)) / h
		// lim(x->C) (a+b) == lim(x->C)a + lim(x->C)b
		// lim(x->C) x == C
		
		//Function<Object, Function<Object, Object>> func = forAll(x->y->null);
		
		System.out.println(forAll(x -> apply(EQUAL, x, x)));
		
		Expression<Function<Object,Object>> ID = lambda(y -> y);
		
		System.out.println(ID);
		
		Expression<Function<Object, Function<Object, Object>>> l = lambda(x -> ID);
		System.out.println(l);
		
		assertEquals(3, apply(l, CONST(5), CONST(3)).evaluate());
		
		assertTrue(ID.isEqual(apply(l, CONST(5))));
		
		assertTrue(lambda(x->x).isEqual(lambda(y->y)));
		assertFalse(lambda(x->x).isEqual(CONST(5)));
		assertTrue(CONST(5).isEqual(CONST(5)));
		
		
	}

}
