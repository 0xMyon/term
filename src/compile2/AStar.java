package compile2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

public class AStar<T> {
	
	public String toString() {
		return nodes.toString();
	}
	
	private final T goal;
	
	private final Function<T, Stream<T>> next;
	private final Function<T, Integer> distance;
	
	
	private final Map<T, Node> nodes = new HashMap<>();
	
	private final SortedSet<Node> open = new TreeSet<>(new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) { // compate(1,2) < 0 === 1 < 2
			if (o2.fscore != o1.fscore)
				return -(o2.fscore - o1.fscore);
			else if(o1.gscore != o2.gscore)
				return -(o2.gscore - o1.gscore);
			else
				return -(o2.hashCode() - o1.hashCode());
		}
	});
	
	AStar(T start, T goal, Function<T, Stream<T>> next, Function<T, Integer> distance) {
		this.goal = goal;
		this.next = next;
		this.distance = distance;
		open.add(new Node(start, 0, distance.apply(start), null));
	}
	
	
	private int steps = 0;
	
	public boolean step() {
		steps++;
		
		Iterator<Node> it = open.iterator();
		Node current = it.next();
		
		
		if (current.node.equals(goal)) {
			System.out.println("DONE - "+steps+"("+open.size()+")");
			System.out.println(goal);
			Node x = current;
			while(x.trace != null) {
				System.out.println("<= "+x.trace.node);
				x = x.trace;
			}
			return true;
		}
		
		
		it.remove();
		
		
		if (debug) System.out.println("Current: "+current);
		
		
		next.apply(current.node).forEach(t -> {
			
			if (debug) System.out.println("    check: "+t);
			
			//int s = 0*Math.abs(t.toString().length() - current.toString().length());
			
			if (nodes.containsKey(t)) {
				Node n = nodes.get(t);
				int g = current.gscore + 1;
				if(g < n.gscore) {
					n.trace = current;
					n.gscore = g;
					n.fscore = g+distance.apply(n.node);
				} else {
					return;
				}
				
			} else {
				nodes.put(t, new Node(t, current.gscore + 1, distance.apply(t), current));
			}
			
			
			if(!open.contains(nodes.get(t))) {
				if (debug) System.out.println("add: "+nodes.get(t));
				open.add(nodes.get(t));
			}
			else {
				if (debug) System.out.println("reject "+nodes.get(t));
			}
			
		});
		
		
		return false;
	}
	
	public void run() {
		while(!open.isEmpty() && !step()) {}
	}
	
	public void run(int i) {
		while(!open.isEmpty() && !step() && i-- >= 0) {}
		System.out.println("Open: "+open.size());
	}
	
	private class Node {
		
		Node(T node, int g, int f, Node source) {
			this.node = node;
			this.gscore = g;
			this.fscore = f+g;
			trace = source;
		}
		
		
		int gscore, fscore;
		
		final T node;
		
		Node trace = null;
		
		public int hashCode() {
			return node.hashCode();
		}
		public boolean equals(Object other) {
			if (other instanceof AStar.Node) {
				return node.equals(((AStar<?>.Node)other).node);
			}
			return false;
		}
		public String toString() {
			return node.toString()+" ["+gscore+","+fscore+"] <= "+((trace==null)?"NONE":trace.toString());
		}
		
	}

	private boolean debug = false;
	
	public AStar<T> debug() {
		debug = true;
		return this;
	}

	
	
	
	
}
