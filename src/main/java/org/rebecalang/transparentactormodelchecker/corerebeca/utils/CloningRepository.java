package org.rebecalang.transparentactormodelchecker.corerebeca.utils;

import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.ModelCheckingRuntimeException;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;

public class CloningRepository {
	private static HashMap<Integer, CoreRebecaActorState> clonedActors;
	private static Environment clonedEnvironment;
	
	public static void resetRepository() {
		clonedActors = new HashMap<Integer, CoreRebecaActorState>();
		clonedEnvironment = null;
	}
	
//	public static CoreRebecaActorState actorStateDeepCopy(CoreRebecaActorState actorState) {
//		CoreRebecaActorState deepCopy = clonedActors.get(actorState.getId());
//		if(deepCopy != null)
//			return deepCopy;
//		deepCopy = new CoreRebecaActorState(actorState);
//		return deepCopy;
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object cloneObject(Object value) {
		if(value == null)
			return null;
		if(value instanceof Constable) {
			return value;
		}
		if(value instanceof Pair) {
			Pair result = ((Pair)value).clone();
			result.setSecond(cloneObject(((Pair)value).getSecond()));
			return result;
		}
		if(value instanceof CoreRebecaActorState) {
			CoreRebecaActorState actorState = (CoreRebecaActorState) value;
			CoreRebecaActorState retrievedActorState = clonedActors.get(actorState.getId());
			if(retrievedActorState == null) {
				retrievedActorState = actorState.clone();
			}
			return retrievedActorState;
		}
		throw new ModelCheckingRuntimeException("Unknown cloning strategy for " + value.getClass());
	}

	public static ArrayList<CoreRebecaMessageState> cloneMessageQueue(ArrayList<CoreRebecaMessageState> value) {
		ArrayList<CoreRebecaMessageState> clonedMessageQueue = new ArrayList<CoreRebecaMessageState>();
		for(CoreRebecaMessageState message : value)
			clonedMessageQueue.add(message.clone());
		return clonedMessageQueue;
	}

	public static Environment cloneEnvironment(Environment environment) {
		if(clonedEnvironment == null) {
			clonedEnvironment = new Environment();
			for(Entry<String, Object> entry : environment.getAllVariblesEntrySet()) {
				clonedEnvironment.setVariableValue(entry.getKey(), 
						CloningRepository.cloneObject(entry.getValue()));
			}
		}			
		return clonedEnvironment;
	}

	public static void addActor(CoreRebecaActorState actorState) {
		clonedActors.put(actorState.getId(), actorState);
	}
	
	public static CoreRebecaActorState getActor(int id) {
		return clonedActors.get(id);
	}
}
