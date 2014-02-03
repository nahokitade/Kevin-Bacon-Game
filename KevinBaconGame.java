import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.Vertex;

/**
 * KevinBaconGame.java
 * Program that runs the Kevin Bacon game.
 * @author Naho Kitade
 *
 */
public class KevinBaconGame{
	//stores a Tree that has all the actors' shortest path to Kevin Bacon
	private static DirectedAdjListMap<String, String> kevBaconTree; 
	// stores a graph representing all actors and movies provided.
	private static Graph<String, String> MovActorGraph;
	
	/** 
  * Class method to let the user pick a file name.
  * @return filePath
 	*/ 
	public static Picture pickAndShow() { 
  	String filePath = FileChooser.pickAFile(); 
  	return filePath;
	}
	
	
	/**
	 * A program that creates a graph representing all actors and movies provided.
	 * @return a graph representing all actors and movies provided.
	 * @throws IOException
	 */
	private static Graph<String, String> makeKevinBaconGraph() throws IOException{
		// simply calls makeActorMovieGraphFromText method from ProduceActorMovieGraphs.java with
		// parameters as the respective input paths from the computer. 
		System.out.println("Pick the file containing actor names.");
		String inputPathActors = "/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/actors.txt";
		System.out.println("Pick the file containing movie names.")
		String inputPathMovies = "/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/movies.txt";
		System.out.println("Pick the file containing actor-movie combinations.")
		String inputPathActorsAndMovies =	"/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/movie-actors.txt";
		Graph<String, String> actorMovieGraph = ProduceActorMovieGraphs.makeActorMovieGraphFromText(inputPathActors, inputPathMovies, inputPathActorsAndMovies);
		MovActorGraph = actorMovieGraph; // store the graph as a private static variable.
		return actorMovieGraph; // return the graph.
	}
	
	/**
	 * Program that makes a Tree that has all the actors' shortest path to Kevin Bacon.
	 * @throws IOException
	 */
	private static void runKevBaconBFS() throws IOException{
		// makes the graph representing all actors and movies provided then calls BFS on it with root being "Kevin Bacon"
		AdjacencyListGraphMap<String,String> kevBaconGraph = (AdjacencyListGraphMap<String, String>) makeKevinBaconGraph();
		BFS<String, String> bfs = new BFS<String, String>();
		DirectedAdjListMap<String, String> resultTree = bfs.runBFS(kevBaconGraph, "Kevin Bacon");
		kevBaconTree = resultTree; // store the result as a private static variable.
	}
	
	/**
	 * Program that runs the Kevin Bacon Game.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		runKevBaconBFS(); 
    System.out.println("To quit the program, type return in answer to a question.");
    Scanner input = new Scanner(System.in);
    String name = "nothing yet";            // a name
    while (!(name.equals(""))){ //quit program if the user doesnt enter anything
    	//initializes all local variables
    	int number = 0; //stores Kevin Bacon number
    	Vertex<String> oppositeVert = null; 
    	//stores the name of the actors to Kevin Bacon who is the child 
    	LinkedListQueue<String> sourceNames = new LinkedListQueue<String>(); 
    	//stores the name of the actors to Kevin Bacon who is the parent
    	LinkedListQueue<String> destinationNames = new LinkedListQueue<String>();
    	//stores the movie name that connects the source and the destination actor names.
    	LinkedListQueue<String> movieNames = new LinkedListQueue<String>();
    	//prompt the user to type in a name
      System.out.print("Enter the name of an actor: ");
      name = input.nextLine();
      // if what the user typed isnt even in the original graph, prompt the user to type 
    	// in another name until the user types in a valid name.
    	while (!((AdjacencyListGraphMap<String, String>) MovActorGraph).vertexInGraph(name) && !(name.equals(""))){
    		System.out.print("That actor is not in our graph. Enter the name of another actor: ");
    		name = input.nextLine();
    	}
      if(!(name.equals(""))){ // make sure that the user entered something
      	if(name.equals("Kevin Bacon")){ //if the user types in Kevin Bacon, we already know the answer.
    			System.out.println("Kevin Bacon's number is 0");
    			System.out.println("Kevin Bacon appeares in all his own movies, silly.");
    			}
      	else if(!kevBaconTree.vertexInGraph(name)){ 
      		// if the name is in the graph but not in the tree, this actor does not have a Kevin Bacon number.
    			System.out.println("Oops! " + name + " does not have a Kevin Bacon number :(");
    			}
    		else{
    			//anything other than those special cases, we have to iterate up the tree to calculate 
    			// the Kevin Bacon number.
    			sourceNames.enqueue(name); //enqueue the name that was typed in by the user as the first source name.
    			while (!name.equals("Kevin Bacon")){ //loop until we reach Kevin Bacon (root)
    			//get the edge connected to the current name. (should only be 1)
    				Iterable<Edge<String>> edgeList = kevBaconTree.incidentEdgesOut(name);
    				Iterator<Edge<String>> edgeListIter = edgeList.iterator();
    				Edge<String> outEdge = edgeListIter.next();
    				//get the vertex opposite the current name vertex.
    				oppositeVert = kevBaconTree.opposite(name, outEdge);
    				// store the movie name connecting the two vertex.
    				movieNames.enqueue(outEdge.element());
    				// the new name becomes the name stored in the vertex opposite of the original name.
    				name = oppositeVert.element();
    				// enqueue that new name as a destination name
    				destinationNames.enqueue(name);
    				// it now is also a source name for the next iteration.
    				sourceNames.enqueue(name);
    				// each iteration should add to the Kevin Bacon number.
    				number ++;
    			}
    			destinationNames.enqueue(name); // enqueue "Kevin Bacon" as the final destination name.
    			// print out the number for the actor name user provided (we know that is stored in the 
    			//front of the source name queue)
    			System.out.println(sourceNames.front() + "'s number is " + number);
    			// loop until the movie names queue is empty (doesnt really matter which queue we check)
    			while (!movieNames.isEmpty()){
    				// dequeue one name at a time from all the queues and format it correctly to print in console.
    				System.out.println(sourceNames.dequeue() + " appeared in " + movieNames.dequeue() + 
    						" with " + destinationNames.dequeue());
    			}
    		}
    	}
    	System.out.println(""); // print new line when we ask the user for another actor.
    }
	}
}