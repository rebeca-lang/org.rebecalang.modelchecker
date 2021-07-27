import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.State;

import com.ibm.icu.util.StringTokenizer;

public class StateSpaceComparator {

	public static void main(String[] args) {

		try {
//			 State S0 = loadGraph("src/test/resources/train.dot");
//			 State P0 = loadGraph("src/test/resources/TrainController.dot");
			State S0 = loadGraph("src/test/resources/DiningPhilsophers.dot");
			State P0 = loadGraph("src/test/resources/philsState.dot");

			// compareGraphs(P0, S0);
			compareBFSGraphs(P0, S0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static State loadGraph(String fileName) throws IOException {

		RandomAccessFile file = new RandomAccessFile(fileName, "r");
		file.readLine();
		String line;
		Hashtable<Integer, State> states = new Hashtable<Integer, State>();
		while ((line = file.readLine()) != null) {
			if (!line.contains("->"))
				continue;
			if (line.contains("}"))
				break;
			StringTokenizer stt = new StringTokenizer(line, "-> [] \"=;");
			String sourceNode = stt.nextToken().substring(1);
			int sourceID = Integer.parseInt(sourceNode);
			if (!states.containsKey(sourceID)) {
				State newState = new State();
				newState.setId(sourceID);
				states.put(sourceID, newState);
			}
			String destNode = stt.nextToken().substring(1);
			int destID = Integer.parseInt(destNode);
			if (!states.containsKey(destID)) {
				State newState = new State();
				newState.setId(destID);
				states.put(destID, newState);
			}
			State sourceState = states.get(sourceID);
			State destState = states.get(destID);
			stt.nextToken();
			String label = stt.nextToken();
			sourceState.addChildState(label, destState);
			destState.addParentState(label, sourceState);
		}
		file.close();
		return states.get(0);
	}

	static Hashtable<Integer, Integer> visitedSet = new Hashtable<Integer, Integer>();

	private static boolean compareGraphs(State s0, State p0) {
		if (visitedSet.containsKey(p0.getId()))
			return visitedSet.get(p0.getId()) == s0.getId();
		visitedSet.put(p0.getId(), s0.getId());
		Set<String> outLabelsS = new HashSet<String>();
		Set<String> outLabelsP = new HashSet<String>();
		for (Pair<String, State> child : s0.getChildStates()) {
			outLabelsS.add(child.getFirst().toUpperCase());
		}
		for (Pair<String, State> child : p0.getChildStates()) {
			outLabelsP.add(child.getFirst().toUpperCase());
		}
		if (!outLabelsP.equals(outLabelsS)) {
			System.out.println(s0.getId() + ",\t" + p0.getId());
			return false;
		}
		for (Pair<String, State> childP : p0.getChildStates()) {
			State st = null;
			for (Pair<String, State> childS : s0.getChildStates()) {
				if (childP.getFirst().equalsIgnoreCase(childS.getFirst())) {
					st = childS.getSecond();
					break;
				}
			}
			if (!compareGraphs(st, childP.getSecond())) {
				System.out.println("S" + s0.getId() + ",\tP" + p0.getId() + ",\t" + childP.getFirst());
				return false;
			}
		}
		return true;
	}

	private static void compareBFSGraphs(State s0, State p0) {
		Hashtable<Integer, Pair<Integer, String>> parentTableS = new Hashtable<Integer, Pair<Integer, String>>();
		Hashtable<Integer, Pair<Integer, String>> parentTableP = new Hashtable<Integer, Pair<Integer, String>>();
		LinkedList<Pair<State, State>> openBorder = new LinkedList<Pair<State, State>>();
		visitedSet.put(s0.getId(), p0.getId());
		openBorder.add(new Pair<State, State>(s0, p0));
		while (!openBorder.isEmpty()) {
			Pair<State, State> current = openBorder.poll();
			for (Pair<String, State> childS : current.getFirst().getChildStates()) {
				State PChild = null;
				for (Pair<String, State> childP : current.getSecond().getChildStates()) {
					if (childP.getFirst().equalsIgnoreCase(childS.getFirst())) {
						PChild = childP.getSecond();
						parentTableP.put(childP.getSecond().getId(),
								new Pair<Integer, String>(current.getSecond().getId(), childP.getFirst()));
						parentTableS.put(childS.getSecond().getId(),
								new Pair<Integer, String>(current.getFirst().getId(), childS.getFirst()));
						break;
					}
				}
				if (PChild == null) {
					System.out.println("No match for node: " +current.getFirst().getId() +" -> "+childS.getSecond().getId() + " (" +childS.getFirst() + ")");
					reportCounterExample(current, parentTableS, parentTableP);
					return;
				}
				if (visitedSet.containsKey(childS.getSecond().getId())) {
					if (visitedSet.get(childS.getSecond().getId()) != PChild.getId()) {
						reportCounterExample(current, parentTableS, parentTableP);
						return;
					}
				} else {
					visitedSet.put(childS.getSecond().getId(), PChild.getId());
					openBorder.add(new Pair<State, State>(childS.getSecond(), PChild));
				}
				// if(!compareGraphs(st, childP.getSecond())) {
				// System.out.println("S" + s0.getId() + ",\tP" + p0.getId() +",\t" +
				// childP.getFirst());
				// return false;
				// }
			}
		}
	}

	private static void reportCounterExample(Pair<State, State> current,
			Hashtable<Integer, Pair<Integer, String>> parentTableS,
			Hashtable<Integer, Pair<Integer, String>> parentTableP) {
		int idS = current.getFirst().getId();
		int idP = current.getSecond().getId();
		System.out.println(idS + "\t" + idP);
		while (parentTableS.containsKey(idS)) {
			Pair<Integer, String> pair = parentTableS.get(idS);
			Pair<Integer, String> pair2 = parentTableP.get(idP);
			idS = pair.getFirst();
			idP = pair2.getFirst();
			System.out.println(idS + "\t" + idP + "\t" + pair.getSecond());
		}

	}

}
