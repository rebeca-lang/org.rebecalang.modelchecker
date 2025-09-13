package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;

public class TransparentActorTransitionSystem<T> {

	TransparentActorTransitionSystemState<T> initialState;
	
	HashMap<Integer, ArrayList<TransparentActorTransitionSystemState<T>>> transitionSystemStates;
	
	int size;
	
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
	}
	
	public TransparentActorTransitionSystemState<T> getInitialState() {
		return initialState;
	}
	
	public Pair<Boolean, TransparentActorTransitionSystemState<T>> addIfNotExists(
			TransparentActorTransitionSystemState<T> previousSystemState, 
			T systemState) {
		int hashCode = systemState.hashCode();
		ArrayList<TransparentActorTransitionSystemState<T>> result = 
				transitionSystemStates.get(hashCode);
		if(result == null) {
			result = new ArrayList<TransparentActorTransitionSystemState<T>>();
			transitionSystemStates.put(hashCode, result);			
		} else {
			for(TransparentActorTransitionSystemState<T> state : result) {
				if(state.getState().equals(systemState)) {
					previousSystemState.addNextState(state);
					state.addPreviousState(previousSystemState);
					return new Pair<Boolean, TransparentActorTransitionSystemState<T>>(false, state);
				}
			}
		}
		
		TransparentActorTransitionSystemState<T> newState = 
				new TransparentActorTransitionSystemState<T>(size);
		newState.setNextStates(new ArrayList<TransparentActorTransitionSystemState<T>>());
		newState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemState<T>>());
		newState.setState(systemState);
		previousSystemState.addNextState(newState);
		newState.addPreviousState(previousSystemState);
		result.add(newState);
		
		size++;
		return new Pair<Boolean, TransparentActorTransitionSystemState<T>>(true, newState); 
	}

	public int size() {
		return size;
	}
}
