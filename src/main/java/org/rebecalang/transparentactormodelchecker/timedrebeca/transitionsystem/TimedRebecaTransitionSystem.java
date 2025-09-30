package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystem;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemTransition;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;

public class TimedRebecaTransitionSystem extends TransparentActorTransitionSystem<TimedRebecaSystemState>{

	public Pair<Boolean, TransparentActorTransitionSystemState<TimedRebecaSystemState>> addIfNotExists(
			TransparentActorTransitionSystemState<TimedRebecaSystemState> previousSystemState,
			TimedRebecaSystemState systemState) {
		int hashCode = systemState.hashCode();
		ArrayList<TransparentActorTransitionSystemState<TimedRebecaSystemState>> result = 
				transitionSystemStates.get(hashCode);
		if(result == null) {
			result = new ArrayList<TransparentActorTransitionSystemState<TimedRebecaSystemState>>();
			transitionSystemStates.put(hashCode, result);			
		} else {
			for(TransparentActorTransitionSystemState<TimedRebecaSystemState> state : result) {
				Pair<Boolean, Integer> shiftEquivalency = state.getState().shiftEquals(systemState);
				if(shiftEquivalency.getFirst()) {
					previousSystemState.addNextState(state);
					state.addPreviousState(previousSystemState);
					System.out.println(state.getId() + "->" + previousSystemState.getId() + " >>" + shiftEquivalency.getSecond());
					return new Pair<Boolean, TransparentActorTransitionSystemState<TimedRebecaSystemState>>(false, state);
				}
			}
			collisions++;
		}
		
		TransparentActorTransitionSystemState<TimedRebecaSystemState> newState = 
				new TransparentActorTransitionSystemState<TimedRebecaSystemState>(size);
		newState.setNextStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		newState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		newState.setState(systemState);
		previousSystemState.addNextState(newState);
		newState.addPreviousState(previousSystemState);
		result.add(newState);
		
		size++;
		return new Pair<Boolean, TransparentActorTransitionSystemState<TimedRebecaSystemState>>(true, newState); 
	}
}