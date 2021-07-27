package org.rebecalang.modelchecker;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;

public class RebecaModelChecker {

//	public static CoreRebecaModelChecker getAppropriateModelChecker(Set<CompilerExtension> compilerFeatures, File rebecaFile) {
//		if (compilerFeatures.contains(CompilerExtension.TIMED_REBECA)) {
//			return new TimedRebecaModelChecker(compilerFeatures, rebecaFile);
//		}
//		return new CoreRebecaModelChecker(compilerFeatures, rebecaFile);
//	}
	
	public static void main(String[] args) throws ModelCheckingException {

//		Set<CompilerExtension> compilerFeatures = new HashSet<CompilerExtension>();
//		compilerFeatures.add(CompilerExtension.CORE_2_3);
////		compilerFeatures.add(CompilerFeature.TIMED_REBECA);
//
////		File rebecaFile = new File("src/test/resources/TimedPingPong.rebeca");
//		File rebecaFile = new File("src/test/resources/phils.rebeca");
//
//		CoreRebecaModelChecker rebecaModelChecker = new CoreRebecaModelChecker(compilerFeatures, rebecaFile);//getAppropriateModelChecker(compilerFeatures, rebecaFile);
////		rebecaModelChecker.configPolicy(CoreRebecaModelChecker.FINE_GRAINED_POLICY);
//		rebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
//		
//		
//		try {
//			rebecaModelChecker.modelCheck();
//		} catch (ExceptionContainer e) {
//			reportCompilerErrors(e);
//		}
	}

	public static void printStateSpace(State state) {
		Set<Integer> visitedSet = new HashSet<Integer>();
		LinkedList<State> openBorderList = new LinkedList<State>();
		openBorderList.add(state);
		visitedSet.add(state.getId());
		System.out.println("S0 [label=\"S0\"];");
		while (!openBorderList.isEmpty()) {
			State polledFirst = openBorderList.pollFirst();
			for (Pair<String, State> child : polledFirst.getChildStates()) {
				int childId = child.getSecond().getId();
				System.out.println("S" + childId + "[label=\"S" + childId + "\"];");
				String transitionLabel = child.getFirst();
				transitionLabel = transitionLabel.split("\\.")[0] + "." + transitionLabel.split("\\.")[2].toUpperCase();
				System.out.println(
						"S" + polledFirst.getId() + " -> S" + childId + "[label=\"" + transitionLabel + "\"];");

				// System.out
				// .println(polledFirst.getId() + "---[" + child.getFirst() + "]--->" +
				// childId);
				if (!visitedSet.contains(childId)) {
					openBorderList.add(child.getSecond());
					visitedSet.add(childId);
				}
			}
		}
	}

	// static public State deepCopy(State oldObj) {
	// ObjectOutputStream oos = null;
	// ObjectInputStream ois = null;
	// try {
	// ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
	// oos = new ObjectOutputStream(bos); // B
	// // serialize and pass the object
	// oos.writeObject(oldObj); // C
	// oos.flush(); // D
	// ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
	// byte[] byteArray = bos.toByteArray();
	// System.out.println("bos: " + byteArray.length);
	// ois = new ObjectInputStream(bin); // F
	// // return the new object
	// return (State) ois.readObject(); // G
	// } catch (Exception e) {
	// System.out.println("Exception in ObjectCloner = " + e);
	// throw (new RuntimeException(e));
	// } finally {
	// try {
	// oos.close();
	// ois.close();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
}
