package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

public class TransparentActorCoreRebecaTransitionSystem {

	TransparentActorCoreRebecaTransitionSystemState initialState;
	
	HashMap<Integer, ArrayList<TransparentActorCoreRebecaTransitionSystemState>> transitionSystemStates;
	
	int size;
	
	public void setInitialState(TransparentActorCoreRebecaTransitionSystemState initialState) {
		this.transitionSystemStates = new HashMap<Integer, ArrayList<TransparentActorCoreRebecaTransitionSystemState>>();
		this.initialState = initialState;
		int hashCode = initialState.hashCode();
		ArrayList<TransparentActorCoreRebecaTransitionSystemState> temp = 
				new ArrayList<TransparentActorCoreRebecaTransitionSystemState>();
		initialState.setNextStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());
		initialState.setPreviousStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());

		temp.add(initialState);
		
		transitionSystemStates.put(hashCode, temp);
		size = 1;
	}
	
	public TransparentActorCoreRebecaTransitionSystemState getInitialState() {
		return initialState;
	}
	
	public Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState> addIfNotExists(
			TransparentActorCoreRebecaTransitionSystemState previousSystemState, 
			CoreRebecaSystemState systemState) {
		int hashCode = systemState.hashCode();
		ArrayList<TransparentActorCoreRebecaTransitionSystemState> result = 
				transitionSystemStates.get(hashCode);
		if(result == null) {
			result = new ArrayList<TransparentActorCoreRebecaTransitionSystemState>();
			transitionSystemStates.put(hashCode, result);			
		} else {
			for(TransparentActorCoreRebecaTransitionSystemState state : result) {
				if(state.getState().equals(systemState)) {
					previousSystemState.addNextState(state);
					state.addPreviousState(previousSystemState);
					return new Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState>(false, state);
				}
			}
		}
		
		TransparentActorCoreRebecaTransitionSystemState newState = 
				new TransparentActorCoreRebecaTransitionSystemState(size);
		newState.setNextStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());
		newState.setPreviousStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());
		newState.setState(systemState);
		previousSystemState.addNextState(newState);
		newState.addPreviousState(previousSystemState);
		result.add(newState);
		
		size++;
		return new Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState>(true, newState); 
	}

	public int size() {
		return size;
	}
}
