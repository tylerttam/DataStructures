import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered
 * linked list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	/**
	 * Makes a layered linked list representing the given arrays of train stations,
	 * bus stops, and walking locations. Each layer begins with a location of 0,
	 * even though the arrays don't contain the value 0.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops      Int array listing all the bus stops
	 * @param locations     Int array listing all the walking locations (always
	 *                      increments by 1)
	 * @return The zero node in the train layer of the final layered linked list
	 */
	public static TNode makeList(int[] trainStations, int[] busStops, int[] locations) {
		TNode start3 = new TNode(0);
		TNode start2 = new TNode(0, null, start3);
		TNode start1 = new TNode(0, null, start2);
		TNode temp1 = start1;
		TNode temp2 = start2;
		TNode temp3 = start3;
		TNode temp22 = temp2;
		TNode temp32 = temp3;
		for (int k = 0; k < locations.length; k++) {
			temp3.next = new TNode(locations[k]);
			temp3 = temp3.next;
		}
		for (int j = 0; j < busStops.length; j++) {
			temp2.next = new TNode(busStops[j]);
			temp2 = temp2.next;
			while (temp32.location != busStops[j])
				temp32 = temp32.next;
			temp2.down = temp32;
		}
		for (int i = 0; i < trainStations.length; i++) {
			temp1.next = new TNode(trainStations[i]);
			temp1 = temp1.next;
			while (temp22.location != trainStations[i])
				temp22 = temp22.next;
			temp1.down = temp22;
		}
		return start1;
	}

	/**
	 * Modifies the given layered list to remove the given train station but NOT its
	 * associated bus stop or walking location. Do nothing if the train station
	 * doesn't exist
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param station   The location of the train station to remove
	 */
	public static void removeTrainStation(TNode trainZero, int station) {
		TNode temp = trainZero;
		while (temp.next != null) {
			if (temp.next.location == station) {
				temp.next = temp.next.next;
				break;
			}
			temp = temp.next;
		}
	}

	/**
	 * Modifies the given layered list to add a new bus stop at the specified
	 * location. Do nothing if there is no corresponding walking location.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param busStop   The location of the bus stop to add
	 */
	public static void addBusStop(TNode trainZero, int busStop) {
		TNode temp = trainZero.down;
		TNode temp2 = trainZero.down.down;
		TNode temp22 = temp2;
		int counter = 0;
		while (temp22 != null) {
			temp22 = temp22.next;
			counter++;
		}
		if (busStop > counter - 1)
			return;

		while (temp.next != null && temp.next.location <= busStop)
			temp = temp.next;
		if (temp.location == busStop)
			return;

		while (temp2.location != busStop)
			temp2 = temp2.next;
		temp.next = new TNode(busStop, temp.next, temp2);
	}

	/**
	 * Determines the optimal path to get to a given destination in the walking
	 * layer, and collects all the nodes which are visited in this path into an
	 * arraylist.
	 * 
	 * @param trainZero   The zero node in the train layer of the given layered list
	 * @param destination An int representing the destination
	 * @return
	 */
	public static ArrayList<TNode> bestPath(TNode trainZero, int destination) {
		ArrayList<TNode> arr = new ArrayList<TNode>();
		TNode temp = trainZero;
		arr.add(temp);
		while (temp != null) {
			if (temp.location == destination) {
				break;
			}
			if (temp.next != null) {
				if (temp.next.location <= destination) {
					temp = temp.next;
					arr.add(temp);
				} else {
					temp = temp.down;
					arr.add(temp);
				}
			}
			if (temp.next == null && temp.down != null) {
				temp = temp.down;
				arr.add(temp);
			}
		}
		while (temp.down != null) {
			temp = temp.down;
			arr.add(temp);
		}
		return arr;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the
	 * same locations and connections, but every node is a NEW node.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @return
	 */
	public static TNode duplicate(TNode trainZero) {
		TNode temp1 = trainZero;
		TNode temp2 = trainZero.down;
		TNode temp3 = trainZero.down.down;
		TNode temp12 = trainZero;
		TNode temp22 = trainZero.down;
		TNode temp32 = trainZero.down.down;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		while (temp12 != null) {
			count1++;
			temp12 = temp12.next;
		}
		while (temp22 != null) {
			count2++;
			temp22 = temp22.next;
		}
		while (temp32 != null) {
			count3++;
			temp32 = temp32.next;
		}
		count1--;
		count2--;
		count3--;
		int[] train = new int[count1];
		int[] bus = new int[count2];
		int[] loc = new int[count3];
		for (int i = 0; i < count1; i++) {
			temp1 = temp1.next;
			train[i] = temp1.location;
		}
		for (int i = 0; i < count2; i++) {
			temp2 = temp2.next;
			bus[i] = temp2.location;
		}
		for (int i = 0; i < count3; i++) {
			temp3 = temp3.next;
			loc[i] = temp3.location;
		}
		return makeList(train, bus, loc);
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param trainZero    The zero node in the train layer of the given layered
	 *                     list
	 * @param scooterStops An int array representing where the scooter stops are
	 *                     located
	 */
	public static void addScooter(TNode trainZero, int[] scooterStops) {
		TNode locTemp = trainZero.down.down;
		TNode busTemp = trainZero.down;
		TNode scooterZero = new TNode(0, null, trainZero.down.down);
		trainZero.down.down = scooterZero;
		TNode scootTemp = scooterZero;
		for (int k = 0; k < scooterStops.length; k++) {
			scootTemp.next = new TNode(scooterStops[k]);
			scootTemp = scootTemp.next;
			while (locTemp.location != scooterStops[k])
				locTemp = locTemp.next;
			scootTemp.down = locTemp;
			if (busTemp.next != null && busTemp.next.location == scooterStops[k]) {
				System.out.println(busTemp.next.location);
				busTemp = busTemp.next;
				busTemp.down = scootTemp;
			}
		}
	}
}