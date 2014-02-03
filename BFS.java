import net.datastructures.Edge;
import net.datastructures.Vertex;

/**
 * Program that enables a run of the Breadth First Search
 * @author Naho Kitade
 */
public class BFS<V, E>{

	/**
	 * Runs a BFS search on a given graph and given vertex element.
	 * @param G graph to perform the BFS search
	 * @param r element of vertex to find the shortest paths to. 
	 * @return a Tree represented by a Directed Graph that contains the shortest path to the element of vertex 
	 * given for every vertex in the graph given as parameter. 
	 */
	public DirectedAdjListMap<V, E> runBFS (AdjacencyListGraphMap<V, E> G, V r){
		CS10Queue<V> vertexQueue = new LinkedListQueue<V>(); 
		DirectedAdjListMap<V, E> visitResult = new DirectedAdjListMap<V, E>();
		vertexQueue.enqueue(r); //enqueue the root vertex in the queue first. 
		visitResult.insertVertex(r); // basically marks this vertex as visited
		while (!(vertexQueue.isEmpty())){ // loops until there is no more vertex in the queue
			V v = vertexQueue.dequeue(); 
			// dequeues a vertex and for all of its incident edges, gets the vertex on the opposite of its 
			// edge, adds that vertex into the result Tree and a directed path to the original vertex if it 
			//is not yet visited.
			for (Edge<E> e: G.incidentEdges(v)){
				Vertex<V> vEnd = G.opposite(v, e);
				if (!(visitResult.vertexInGraph(vEnd.element()))){
					visitResult.insertVertex(vEnd.element());
					visitResult.insertDirectedEdge(vEnd.element(), v, e.element());
					vertexQueue.enqueue(vEnd.element()); // finally, enqueues the vertex into the vertex queue. 
				}
			}
		}
		return visitResult;
	}
	
	/**
	 * Testing code.
	 */
	public static void main(String [] args){
		// create a new graph to run the BFS on
		AdjacencyListGraphMap<String, String> testGraph = new AdjacencyListGraphMap<String, String>();
		testGraph.insertVertex("Kevin Bacon");
		testGraph.insertVertex("actor1");
		testGraph.insertVertex("actor2");
		testGraph.insertVertex("actor3");
		testGraph.insertVertex("actor4");
		testGraph.insertVertex("actor5");
		testGraph.insertVertex("actor6");
		testGraph.insertEdge("Kevin Bacon", "actor1", "movie1");
		testGraph.insertEdge("Kevin Bacon", "actor2", "movie1");
		testGraph.insertEdge("actor1", "actor2", "movie1"); 
		testGraph.insertEdge("actor1", "actor3", "movie2");
		testGraph.insertEdge("actor3", "actor2", "movie3"); 
		testGraph.insertEdge("actor3", "actor4", "movie4");
		testGraph.insertEdge("actor5", "actor6", "movie5");
		System.out.println("Graph that was created:");
		System.out.println(testGraph);
		System.out.println("");
		BFS<String, String> bfs = new BFS<String, String>();
		// run BFS on the graph just created with "Kevin Bacon" vertex as the start vertex.
		DirectedAdjListMap<String, String> resultTree = bfs.runBFS(testGraph, "Kevin Bacon");
		System.out.println("Tree that was created after run of BFS:");
		System.out.println(resultTree);
		System.out.println("");
		// just in case, prints out all the edges and the vertex incident upon it.
		System.out.println("All edges and the vertex incident upon it:");
		for(Edge<String> edge2: resultTree.edges()){
			System.out.println(edge2);
			Vertex<String> [] verts2 = resultTree.endVertices(edge2);
			System.out.println(verts2[0] + " " + verts2[1]);
		}
	}
}