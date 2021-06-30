package compile2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import compile2.type.AnyType;
import compile2.type.Type;
import compile2.type.UserType;

import static compile2.Expression.*;

class Test {
	
	final Type B = new UserType("BOOL");
	final Type A = new AnyType();
	
	
	final Expression T = new Constant("true", B);
	final Expression F = new Constant("false", B);
	
	final Expression NOT = new Constant("¬", B.to(B));
	
	final Expression AND = new Constant("∧", B.to(B.to(B)));
	final Expression OR = new Constant("∨", B.to(B.to(B)));
	final Expression EQ = new Constant("=", A.to(A.to(B)));
	
	final Expression ID = new Lambda(y -> y);
	
	@org.junit.jupiter.api.Test
	void test() {
		
		System.out.println("λ");
		
		
		
		assertEquals("=", EQ.toString());
		assertEquals("true", T.toString());
		assertEquals("=(true)", EQ.apply(T).toString());
		//assertEquals("λ.$3 : $3", ID.toString());
		
		
		assertNotEquals(ID, new Lambda(x -> x));
		assertEquals(ID.CLASS(), new Lambda(x -> x));
		assertEquals(ID, new Lambda(x -> x).CLASS());
		assertEquals(ID.CLASS(), new Lambda(x -> x).CLASS());
		
		assertNotEquals(new Lambda(x->T), new Lambda(x->T));
		assertNotEquals(new Lambda(x->T), T);
		
		
		//System.out.println(ID);
		//System.out.println(ID.apply(T));
		
		assertEquals(T, ID.apply(T));
		assertEquals(ID, ID);
		
		Variable x = new Variable(B, "x");
		Variable y = new Variable(B, "y");
		
		//System.out.println(EQ.apply(AND.apply(T, x), x));
		//System.out.println(EQ.apply(AND.apply(T, x), x).findMatches(y).collect(Collectors.toSet()));
		
		assertEquals(4, EQ.apply(AND.apply(T, x), x).findMatches(y).collect(Collectors.toSet()).size());
		assertEquals(8+1, EQ.apply(AND.apply(T, x), x).findMatches(new Variable(A)).collect(Collectors.toSet()).size());
		
		assertEquals(1, EQ.apply(AND.apply(T, x), x).findMatches(AND.apply(T, y)).collect(Collectors.toSet()).size());
		assertEquals(0, EQ.apply(AND.apply(T, x), x).findMatches(F).collect(Collectors.toSet()).size());
		
		
		Variable a = new Variable(B, "a");
		Variable b = new Variable(B, "b");
		Variable c = new Variable(B, "c");
		
		
		// a & (b | c)
		Expression left = AND.apply(a, OR.apply(b, c));
		// (a & b) | (a & c)
		Expression right = OR.apply(AND.apply(a,b), AND.apply(a,c));
				
		Expression l2 = AND.apply(T, OR.apply(F, T));
		Expression r2 = OR.apply(AND.apply(T, F), AND.apply(T, T));
		
		/*
		System.out.println("");
		System.out.println(l2);
		System.out.println(l2.rename(right.freeVars()));
		System.out.println(l2.rename(right.freeVars()).findMatches(left).collect(Collectors.toSet()));
		System.out.println(l2.rename(right.freeVars()).findMatches(left).map(m -> 
			m.get(null)
		).collect(Collectors.toSet()));
		System.out.println(l2.rename(right.freeVars()).findMatches(left).map(m -> 
			m.get(null).replace(left, right)
		).collect(Collectors.toSet()));
		System.out.println(l2.rename(right.freeVars()).findMatches(left).map(m -> 
			m.get(null).replace(left, right).replaceAll(m)
		).collect(Collectors.toSet()));
		*/
			
		
		assertEquals(r2, l2.transit(left, right).findFirst().get());
		
		// a & (b | c)
		Expression left2 = AND.apply(a, F);
		// (a & b) | (a & c)
		Expression right2 = F;
		
		assertEquals(OR.apply(F, AND.apply(T,T)), r2.transit(left2, right2).findFirst().get());
		assertTrue(new Lambda(n -> OR.apply(F, AND.apply(T,T))).hasMatch( new Lambda(n->r2).transit(left2, right2).findFirst().get()  ));
		
		assertEquals(0, F.freeVars().count());
		assertEquals(1, left2.freeVars().count());
		assertEquals(0, ID.freeVars().count());
		assertEquals(3, left.freeVars().count());
		assertEquals(3, right.freeVars().count());
		assertEquals(0, r2.freeVars().count());
		assertEquals(1, new Lambda(u->a).freeVars().count());
		
		assertEquals(T, EQ.apply(F,F).transit(forAll(n->EQ.apply(n,n), new AnyType()), T).findFirst().get());
		assertEquals(0, EQ.apply(F,T).transit(forAll(n->EQ.apply(n,n), new AnyType()), T).count());
		
		
		Expression self = OR.apply(T, new Variable(B));
		Expression self2 = self.transit(T, self).findFirst().get();
		
		//System.out.println(self);
		//System.out.println(self2);
		
		assertTrue(self2.hasMatch(OR.apply(OR.apply(T, x), y)));
		
		assertFalse(OR.apply(OR.apply(T, x), x).hasMatch(self2));
		assertTrue(self2.hasMatch(OR.apply(OR.apply(T, x), x)));
		
		
		
		
		Context C = new Context();
		
		C.add(NOT.apply(F), T);
		C.add(NOT.apply(T), F);
		
		C.add(AND.apply(F, F), F);
		C.add(AND.apply(F, T), F);
		C.add(AND.apply(T, F), F);
		C.add(AND.apply(T, T), T);
		
		C.add(OR.apply(F, F), F);
		C.add(OR.apply(F, T), T);
		C.add(OR.apply(T, F), T);
		C.add(OR.apply(T, T), T);
		
		
	}
	
	
	
	@org.junit.jupiter.api.Test
	void testTransition() {
		
		Transition t1 = new Transition(OR.apply(T, new Variable(B)), T);
		
		assertEquals(t1, t1.inverse().inverse());
		
		assertEquals(t1.andThen(t1).findFirst().get(), new Transition(OR.apply(OR.apply(T, new Variable(B)), new Variable(B)), T));
		
		//System.out.println(ID.distance(T));
		//System.out.println(T.distance(T));
		
		
		assertTrue(ID.distance(T) > T.distance(F));
		
	}
	
	static final Type I = new UserType("INT");
	
	static final Expression ZERO = new Constant("0", I);
	static final Expression INC = new Constant("+1", I.to(I));
	
	static Expression INT(int i) {
		Expression result = ZERO;
		while(i > 0) {
			result = INC.apply(result);
			i--;
		}
		return result;
	}
	
	Expression ADD = new Constant("+", I.to(I.to(I)));
	Expression MINUS = new Constant("-", I.to(I.to(I)));
	
	Expression TIMES = new Constant("*", I.to(I.to(I)));
	Expression DIVIDE = new Constant("/", I.to(I.to(I)));
	
	
	Expression POW = new Constant("^", I.to(I.to(I)));
	
	//Expression DERIVE = new Constant("'", I.to(I).to(I.to(I)));
	
	
	Variable X = new Variable(I, "x");
	Variable H = new Variable(I, "h");
	
	Expression LIM = new Constant("lim", I.to(A).to(I.to(I.to(A))));
	
	Expression DERIVE = new Lambda(f -> 
		LIM.apply(new Lambda(H -> DIVIDE.apply(MINUS.apply( f.apply(ADD.apply(X, H)) , f.apply(X) ), H)), ZERO), 
		(I.to(I)).to(I.to(I)));
	
	
	
	
	@org.junit.jupiter.api.Test
	void testAStar() {
		
		Context C = new Context();
		
		Variable x = new Variable(I, "x");
		Variable y = new Variable(I, "y");
		Variable z = new Variable(I, "z");
		
		
		C.add(ADD.apply(ZERO, x), x);
		C.add(ADD.apply(z, y), ADD.apply(y, z));
		C.add(ADD.apply(INC.apply(y), z), ADD.apply(y, INC.apply(z)));
		
		C.add(MINUS.apply(x, x), ZERO);
		C.add(MINUS.apply(x, ZERO), x);
		
		C.add(DIVIDE.apply(x, x), INT(1));
		C.add(DIVIDE.apply(x, INT(1)), x);
		
		C.add(DIVIDE.apply(TIMES.apply(x,y), z), TIMES.apply(DIVIDE.apply(x,z),DIVIDE.apply(y,z)));
		
		
		C.add(TIMES.apply(INC.apply(y), z), ADD.apply(z, TIMES.apply(y, z)));
		
		C.add(TIMES.apply(ZERO, z), ZERO);
		C.add(TIMES.apply(INT(1), z), z);
		
		C.add(TIMES.apply(x, y), TIMES.apply(y, x));
		
		C.add(TIMES.apply(x, TIMES.apply(y, z)), TIMES.apply(TIMES.apply(x, y), z));
		C.add(ADD.apply(x, ADD.apply(y, z)), ADD.apply(ADD.apply(x, y), z));
		C.add(TIMES.apply(x, ADD.apply(y, z)), ADD.apply(TIMES.apply(x, y), TIMES.apply(x, z)));
		
		C.add(POW.apply(x, ZERO), INT(1));
		C.add(POW.apply(x, INT(1)), x);
		C.add(POW.apply(x, INC.apply(y)), TIMES.apply(x, POW.apply(x, y)));
		
		C.invalid(DIVIDE.apply(x, ZERO));
		C.invalid(POW.apply(ZERO, ZERO));
		
		Variable lambda = new Variable(A);
		
		C.add(LIM.apply(lambda, x), lambda.apply(x));
		
		
		// f'(x) == lim[h->0]: (f(x+h) - f(x)) / h
		//C.add(DERIVE.apply(f), LIM.apply(new Lambda(h -> DIVIDE.apply(MINUS.apply( f.apply(ADD.apply(x, h)) , f.apply(x) ) ,h))), ZERO);
		
		// lim[h->x]: h == x
		
		// lim[h->x]: y # z == lim[h->x]:y # lim[h->x]:z
		//C.add(LIM.apply(h, x, ADD.apply(y,z)), ADD.apply(LIM.apply(h,x,y), LIM.apply(h,x,z)));
		//C.add(LIM.apply(h, x, TIMES.apply(y,z)), TIMES.apply(LIM.apply(h,x,y), LIM.apply(h,x,z)));
		
		// TODO y contains no h
		// lim[h->x]:y == y (where y does not contain x)
		//C.add(LIM.apply(h, x, y.no(h)), y); 
		
		
		//final Expression test = new Lambda(ix -> ix, I);
		
		//System.out.println();
		
		//assertEquals(1, DERIVE.apply(test).transit(DERIVE.apply(f), f).count());
		
		
		//System.out.println(C);
		
		assertFalse(C.isValid(DIVIDE.apply(ADD.apply(INT(5), INT(15)), INT(0))));
		
		{
			ExpressionClass start = INT(0).CLASS();
			ExpressionClass goal = INT(1).CLASS();
			
			AStar<ExpressionClass> a = new AStar<ExpressionClass>(start, goal, C::next, goal::distance2);
			
			assertFalse(a.run(200));
		}
		
		{
			ExpressionClass start = ADD.apply(INT(2), INT(2)).CLASS();
			ExpressionClass goal = INT(4).CLASS();
			
			AStar<ExpressionClass> a = new AStar<ExpressionClass>(start, goal, C::next, goal::distance2);
			
			assertTrue(a.run(200));
		}
		
		{
			ExpressionClass start = TIMES.apply(INT(2), INT(2)).CLASS();
			ExpressionClass goal = INT(4).CLASS();
			
			AStar<ExpressionClass> a = new AStar<ExpressionClass>(start, goal, C::next, goal::distance2);
			
			assertTrue(a.run(200));
		}
		
		{
			ExpressionClass start = POW.apply(INT(2), INT(2)).CLASS();
			ExpressionClass goal = INT(4).CLASS();
			
			AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, goal::distance2);
			
			assertTrue(a.run(200));
		}
		
		
		{
			ExpressionClass start = LIM.apply(new Lambda(h -> h), INT(2)).CLASS();
			ExpressionClass goal = INT(2).CLASS();
			
			AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, goal::distance2);
			
			assertTrue(a.run(200));
		}
		
		{
			ExpressionClass start = LIM.apply(new Lambda(h -> INT(5)), INT(2)).CLASS();
			ExpressionClass goal = INT(5).CLASS();
			
			AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, goal::distance2);
			
			assertTrue(a.run(200));
		}
		
		
		{
			ExpressionClass start = POW.apply(ADD.apply(x,y), INT(2)) .CLASS();
			//ExpressionClass goal = TIMES.apply(ADD.apply(x,y), ADD.apply(x,y)) .CLASS();
			
			ExpressionClass goal = ADD.apply(POW.apply(x, INT(2)), ADD.apply(TIMES.apply(x, TIMES.apply(y, INT(2))), POW.apply(y, INT(2)))).CLASS();
			
			AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, goal::distance2);
			//a.debug();
			assertTrue(a.run(2000));
		}
		
		
		{

			
			final Expression SQUARE = new Lambda(ix -> POW.apply(ix, INT(2)), I, "x");
			final Expression TWICE = new Lambda(ix -> TIMES.apply(ix, INT(2)), I);
			
			
			C.next(LIM.apply(new Lambda(h -> SQUARE, "h")).CLASS()).forEach(System.out::println);
			
			
			ExpressionClass start = DERIVE.apply(SQUARE).CLASS();
			ExpressionClass goal = TWICE.CLASS();
			
			AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, goal::distance2);
			//AStar<ExpressionClass> a = new AStar<>(start, goal, C::next, ExpressionClass::simplicity);
			//a.debug();
						
			assertTrue(a.run(10000));
		}
		
		
	
		
	}

}
