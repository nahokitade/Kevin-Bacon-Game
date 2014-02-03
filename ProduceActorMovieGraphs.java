import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import net.datastructures.Graph;

/**
 * ProduceActorMovieGraphs.java
 * 
 * Program that creates a graph using all the actor names as vertex, and connecting each actor 
 * vertex with edges from a input text file indicated as parameters. 
 * @author Naho Kitade
 *
 */
public class ProduceActorMovieGraphs{
	
	/**
	 * Reads a file and creates a map with the ID as keys and Names as the values stored in the keys. 
	 * File must be formatted so each line is: ID|Name
	 * @param inputPath path of the file to make the map.
	 * @return map with the ID as keys and Names as the values stored in the keys. 
	 */
	private static Map<String, String> IDNameMatchingMap(String inputPath) throws IOException{
			// makes the text ready to read.
			Map<String, String> textToMap = new HashMap<String, String>();
			BufferedReader input =  new BufferedReader(new FileReader(inputPath));
			String currentline;
		try{
			while ((currentline = input.readLine()) != null){
				// separate out the names and the IDs.
				String[] mapKeyValue = currentline.split("\\|");
				// maps ID as key and Name as value for all the ID and name pair.
				textToMap.put(mapKeyValue[0], mapKeyValue[1]);
			}
		return textToMap;
		}
		finally{
			// close the input not matter what.
			input.close();
		}
	}
	
	/**
	 * creates and returns a map that uses movie names as key, and set of actor names for that movie as value.
	 * @param inputPath path of input containing Movie ID and Actor ID, formatted: Movie ID:Actor ID 
	 * @param IDAndActor map containing Actor ID as key and Actor name as value.
	 * @param IDandMovie map containing Movie ID as key and Movie name as value.
	 * @return a map that uses movie names as key, and set of actor names for that movie as value.
	 */
	private static Map<String, Set<String>> actorMovieMatchingMap(String inputPath, Map<String, String> IDAndActor, 
			Map<String, String> IDandMovie) throws IOException{
		// Make a new map to return, and make the text ready to read.
		Map<String, Set<String>> textToMap = new HashMap<String, Set<String>>();
		BufferedReader input =  new BufferedReader(new FileReader(inputPath));
		String currentline;
	try{
		// go through the whole text. 
		while ((currentline = input.readLine()) != null){
			String[] mapKeyValue = currentline.split("\\|");
			// if the map doesnt contain the movie name already, make a new hashSet to store the 
			//actor names and add the current actor name into the set. store the key value pair 
			// in the map.
			if (!(textToMap.containsKey(IDandMovie.get(mapKeyValue[0])))){
				Set<String> actorNames = new HashSet<String>();
				actorNames.add(IDAndActor.get(mapKeyValue[1]));
				textToMap.put(IDandMovie.get(mapKeyValue[0]), actorNames);
			}
			// if the map contains the movie name already, get its set containing its actor names
			// and add the current actor name into that set.
			else{
				Set<String> actorNames = textToMap.get(IDandMovie.get(mapKeyValue[0]));
				actorNames.add(IDAndActor.get(mapKeyValue[1]));
				textToMap.put(IDandMovie.get(mapKeyValue[0]), actorNames);
			}
		}
	return textToMap;
	}
	finally{
		// close the input not matter what.
		input.close();
	}
}
	
	/**
	 * Creates a graph using all the actor names as vertex, and connecting each actor vertex with edges
	 * with names of movies that the actors appeared in. 
	 * @param actorMovieMatchingMap Map containing movie names as keys and the sets of actors from that movie
	 * as values.
	 * @param IDAndActor Map that uses Actor IDs as a key and Actor names as its value.
	 * @return a graph using all the actor names as vertex, and connecting each actor vertex with edges
	 */
	private static Graph<String, String> makeActorMovieGraph(Map<String, Set<String>> actorMovieMatchingMap, Map<String, String> IDAndActor){
		AdjacencyListGraphMap<String, String> actorMovieGraph = new AdjacencyListGraphMap<String, String>();
		Set<String> actorIDs = IDAndActor.keySet();
		//insert a vertex for all the actor names in the IDAndActor map.
		for(String actorID: actorIDs){
			String actorName = IDAndActor.get(actorID);
			actorMovieGraph.insertVertex(actorName);
		}
		//get set of movie names contained in the actorMovieMatchingMap.
		Set<String> movieNames = actorMovieMatchingMap.keySet();
		// for all of the movies, get the set of actor names that performed in that movie
		for(String movieName: movieNames){
			Set<String> actorNames = actorMovieMatchingMap.get(movieName);
			Iterator<String> actorNamesIter = actorNames.iterator();
			//Create an edge between all of the actors in a particular set. 
			while (actorNames.size() > 1){
				String actorName = actorNamesIter.next();
				//remove an actor from the set
				actorNamesIter.remove();
				Iterator<String> actorNamesIter2 = actorNames.iterator();
				// create an edge between that removed actor and all the actors that are still in the set.
				// this process continues until there is only one actor name in the set.
				while (actorNamesIter2.hasNext()){
					String actorName2 = actorNamesIter2.next();
					actorMovieGraph.insertEdge(actorName, actorName2, movieName);
				}
			}	
		}
		return actorMovieGraph;
	}
	
	/**
	 * Creates a graph using all the actor names as vertex, and connecting each actor vertex with edges
	 * from a input text file indicated as parameters. 
	 * @param inputPathActors path of input containing ActorID|ActorName pairs
	 * @param inputPathMovies path of input containing MovieID|MovieName pairs
	 * @param inputPathActorsAndMovies path of input containing MovieID|ActorID pairs
	 * @return Creates a graph using all the actor names as vertex, and connecting each actor vertex with edges
	 * @throws IOException
	 */
	public static Graph<String, String> makeActorMovieGraphFromText(String inputPathActors, 
			String inputPathMovies, String inputPathActorsAndMovies) throws IOException{
		//make all the needed maps to use as parameters in makeActorMovieGraph() method.
		Map<String, String> movieMap = IDNameMatchingMap(inputPathMovies);
		Map<String, String> actorMap = IDNameMatchingMap(inputPathActors);
		Map<String, Set<String>> actorMovieMap= actorMovieMatchingMap(inputPathActorsAndMovies, actorMap, movieMap);
		// make the final graph to return using the created Maps. 
		Graph<String, String> actorMovieGraphFromText = makeActorMovieGraph(actorMovieMap, actorMap);
		return actorMovieGraphFromText;
	}
 
	/**
	 * Testing code.
	 */
	public static void main(String [] args) throws IOException{
		String inputPathActors = "/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/actorsTest.txt";
		String inputPathMovies = "/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/moviesTest.txt";
		String inputPathActorsAndMovies =	"/Users/nahokitade/Desktop/COSC 10/Problem Set/PS5/data/movie-actorsTest.txt";
		Graph<String, String> actorMovieGraph = makeActorMovieGraphFromText(inputPathActors, inputPathMovies, inputPathActorsAndMovies);
		System.out.println(actorMovieGraph);
	}
	
	
	
}