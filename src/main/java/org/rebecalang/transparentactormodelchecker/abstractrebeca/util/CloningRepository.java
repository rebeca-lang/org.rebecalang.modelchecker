package org.rebecalang.transparentactormodelchecker.abstractrebeca.util;

import java.lang.constant.Constable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.ModelCheckingRuntimeException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;

public class CloningRepository {
	private static HashMap<Integer, AbstractActorState> clonedActors = new HashMap<Integer, AbstractActorState>();
	private static ActivationRecord clonedEnvironment;
	
	public static void resetRepository() {
		clonedActors.clear();
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
		if(value instanceof AbstractActorState) {
			AbstractActorState actorState = (AbstractActorState) value;
			AbstractActorState retrievedActorState = clonedActors.get(actorState.getId());
			if(retrievedActorState == null) {
				retrievedActorState = actorState.clone();
			}
			return retrievedActorState;
		}
		if(value instanceof Object[]) {
			Object[] valueData = (Object[]) value;
			Object[] cloned = new Object[valueData.length];
			for(int cnt = 0; cnt < valueData.length; cnt++)
				cloned[cnt] = cloneObject(valueData[cnt]);
			return cloned;
		}
		throw new ModelCheckingRuntimeException("Unknown cloning strategy for " + value.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractMessageState> ArrayList<T> cloneArrayList(ArrayList<T> value) {
		ArrayList<T> clonedMessageQueue = new ArrayList<T>();
		for(T message : value)
			clonedMessageQueue.add((T) message.clone());
		return clonedMessageQueue;
	}

	public static ArrayList<? extends AbstractMessageState> cloneMessageQueue(ArrayList<? extends AbstractMessageState> value) {
		ArrayList<AbstractMessageState> clonedMessageQueue = new ArrayList<AbstractMessageState>();
		for(AbstractMessageState message : value)
			clonedMessageQueue.add(message.clone());
		return clonedMessageQueue;
	}

	public static ActivationRecord cloneEnvironment(ActivationRecord environment) {
		if(clonedEnvironment == null) {
			clonedEnvironment = new ActivationRecord();
			for(Entry<String, Object> entry : environment.getActivationRecord().entrySet()) {
				clonedEnvironment.setVariableValue(entry.getKey(), 
						CloningRepository.cloneObject(entry.getValue()));
			}
		}			
		return clonedEnvironment;
	}

	public static void addActor(AbstractActorState actorState) {
		clonedActors.put(actorState.getId(), actorState);
	}
	
	public static AbstractActorState getActor(int id) {
		return clonedActors.get(id);
	}
}
