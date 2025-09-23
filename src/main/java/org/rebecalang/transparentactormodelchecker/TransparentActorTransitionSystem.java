package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;

public abstract class TransparentActorTransitionSystem<T> {

	protected TransparentActorTransitionSystemState<T> initialState;
	
	protected HashMap<Integer, ArrayList<TransparentActorTransitionSystemState<T>>> transitionSystemStates;
	
	protected int size;
	
	protected int collisions;
	
	public void setInitialState(TransparentActorTransitionSystemState<T> initialState) {
		this.transitionSystemStates = new HashMap<Integer, ArrayList<TransparentActorTransitionSystemState<T>>>();
		this.initialState = initialState;
		int hashCode = initialState.hashCode();
		ArrayList<TransparentActorTransitionSystemState<T>> temp = 
				new ArrayList<TransparentActorTransitionSystemState<T>>();
		initialState.setNextStates(new ArrayList<TransparentActorTransitionSystemState<T>>());
		initialState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemState<T>>());

		temp.add(initialState);
		
		transitionSystemStates.put(hashCode, temp);
		size = 1;
		collisions = 0;
	}
	
	public TransparentActorTransitionSystemState<T> getInitialState() {
		return initialState;
	}
	
	public abstract Pair<Boolean, TransparentActorTransitionSystemState<T>> addIfNotExists(
			TransparentActorTransitionSystemState<T> previousSystemState, 
			T systemState);

	public int size() {
		return size;
	}

	public int getCollisions() {
		return collisions;
	}
}
