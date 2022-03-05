package transit;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	public ArrayList<TNode> newMap(TNode begin, int fin){
		ArrayList<TNode> path = new ArrayList<>();
		TNode currentLocation = begin;
		for(; currentLocation!=null && currentLocation.getLocation() <= fin;currentLocation=currentLocation.getNext()){
			path.add(currentLocation);
		}
		return path;
	}

	public void newStop(TNode begin, int loc) {
		TNode curr = begin;
		TNode down;
		
		while(curr.getLocation()<loc){
			if (curr.getNext().getLocation()>loc){
				down = walk(curr.getDown(), loc);
				TNode newStop = new TNode(loc, curr.getNext(), down);
				curr.setNext(newStop);
			}
			curr=curr.getNext();
		}
 
	 }

	public TNode duplicateNode(TNode dup){
		TNode newCopy = new TNode(dup.getLocation(),dup.getNext(),dup.getDown());
		if (dup.getNext()!=null) {
			 newCopy.setNext(duplicateNode(dup.getNext()));
			return dup;
		}
		
		if (dup.getDown()!=null){
			newCopy.setDown(duplicateNode(dup.getDown()));
		}

		return newCopy;
	}
	public TNode walk(TNode begin, int fin){
		TNode currLocation = begin;
		for(; currLocation!=null && currLocation.getLocation() < fin; currLocation=currLocation.getNext());
		if (currLocation!=null && currLocation.getLocation() == fin){
			return currLocation;
		}
		return null;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {

	    // UPDATE THIS METHOD

		int walking;
		int buses = 0;
		int trains = 0;
		
		TNode firstLocation = new TNode(0);
		TNode firstBus = new TNode(0,null,firstLocation);
		trainZero = new TNode(0,null,firstBus);

		TNode locationNode = null;
		TNode busNode = null;
		TNode trainNode = null;
		TNode previousiousLocation = firstLocation;
		TNode previousiousBus = firstBus;
		TNode previousiousTrain = trainZero;
		
		for (int locationIndex = 0, busIndex = 0, trainIndex = 0; locationIndex  < locations.length; locationIndex++){
		  walking = locations[locationIndex];
			if (busIndex<busStops.length){
				buses = busStops[busIndex];
			}
			if (trainIndex<trainStations.length){
				trains = trainStations[trainIndex];
			}

			
			locationNode = new TNode(walking);

			if (previousiousLocation != null)
				previousiousLocation.setNext(locationNode);
			previousiousLocation = locationNode;

			if ( walking == buses){


				busNode = new TNode(buses, null, locationNode);
				if (previousiousBus != null)
					previousiousBus.setNext(busNode);
				previousiousBus = busNode;
				busIndex++;



				if (buses == trains){
					trainNode = new TNode(trains, null, busNode);
					if (previousiousTrain != null)
						previousiousTrain.setNext(trainNode);
					previousiousTrain = trainNode;
					trainIndex++;
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	    // UPDATE THIS METHOD
		
		TNode stop = trainZero.getNext();
		TNode previous=trainZero;

		while(stop!=null) {
			if (stop.getLocation()==station) {
				previous.setNext(stop.getNext());
			}
			previous = stop;
			stop = stop.getNext();
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	    // UPDATE THIS METHOD

		TNode firstBus = trainZero.getDown();
	    newStop(firstBus, busStop);
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {

	    // UPDATE THIS METHOD
	    ArrayList<TNode> newPath = new ArrayList<>();
		ArrayList<TNode> trains = newMap(trainZero, destination);
		ArrayList<TNode> buses = newMap(trains.get(trains.size()-1).getDown(), destination);
		ArrayList<TNode> locations = newMap(buses.get(buses.size()-1).getDown(), destination);

		newPath.addAll(trains);
		newPath.addAll(buses);
		newPath.addAll(locations);

	    return newPath;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {

	    // UPDATE THIS METHOD
	    TNode copy = duplicateNode(trainZero);
	    return copy;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {

	    // UPDATE THIS METHOD
		TNode firstBus = trainZero.getDown();
		TNode locationZero = firstBus.getDown();
		TNode firstScooter = new TNode(0,null,locationZero);

		firstBus.setDown(firstScooter);

		TNode curr = firstScooter;
		TNode down;

		for(int firstStop = 0; firstStop<scooterStops.length; firstStop++) {
			TNode currentBus = walk(firstBus, scooterStops[firstStop]);
			down = walk(locationZero, scooterStops[firstStop]);
			TNode newScooter = new TNode(scooterStops[firstStop], null, down);

			if (currentBus!=null) {
				currentBus.setDown(newScooter);
			 }

			curr.setNext(newScooter);
			curr=curr.getNext();
		}
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
