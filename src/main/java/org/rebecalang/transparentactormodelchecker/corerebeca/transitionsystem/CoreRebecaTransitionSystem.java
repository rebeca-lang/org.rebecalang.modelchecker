package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystem;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

public class CoreRebecaTransitionSystem extends TransparentActorTransitionSystem<CoreRebecaSystemState>{

	public Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> addIfNotExists(
			TransparentActorTransitionSystemState<CoreRebecaSystemState> previousSystemState, 
			CoreRebecaSystemState systemState) {
		int hashCode = systemState.hashCode();
		ArrayList<TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
				transitionSystemStates.get(hashCode);
		if(result == null) {
			result = new ArrayList<TransparentActorTransitionSystemState<CoreRebecaSystemState>>();
			transitionSystemStates.put(hashCode, result);			
		} else {
			for(TransparentActorTransitionSystemState<CoreRebecaSystemState> state : result) {
				if(state.getState().equals(systemState)) {
					previousSystemState.addNextState(state);
					state.addPreviousState(previousSystemState);
					return new Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>>(false, state);
				}
			}
			collisions++;
		}
		
		TransparentActorTransitionSystemState<CoreRebecaSystemState> newState = 
				new TransparentActorTransitionSystemState<CoreRebecaSystemState>(size);
		newState.setNextStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		newState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		newState.setState(systemState);
		previousSystemState.addNextState(newState);
		newState.addPreviousState(previousSystemState);
		result.add(newState);
		
		size++;
		return new Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>>(true, newState); 
	}
}
