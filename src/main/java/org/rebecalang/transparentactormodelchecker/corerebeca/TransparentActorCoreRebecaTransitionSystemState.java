package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

class TransparentActorCoreRebecaTransitionSystemState {
	

	public TransparentActorCoreRebecaTransitionSystemState(int id) {
		this.id = id;
		nextStates = new ArrayList<TransparentActorCoreRebecaTransitionSystemState>();
		previousStates = new ArrayList<TransparentActorCoreRebecaTransitionSystemState>();
	}

	private int id;
	private CoreRebecaSystemState state;
	private ArrayList<TransparentActorCoreRebecaTransitionSystemState> nextStates;
	private ArrayList<TransparentActorCoreRebecaTransitionSystemState> previousStates;

	public CoreRebecaSystemState getState() {
		return state;
	}
	
//	public List<String> getActorsIds() {
//		return Collections.sort(state.getActorsIds());
//				
//	}
	
	public CoreRebecaActorState getActorState(int id) {
		return state.getActorState(id);
	}
	
	public void setState(CoreRebecaSystemState state) {
		this.state = state;
	}
	
	public ArrayList<TransparentActorCoreRebecaTransitionSystemState> getNextStates() {
		return nextStates;
	}
	
	public void setNextStates(ArrayList<TransparentActorCoreRebecaTransitionSystemState> nextStates) {
		this.nextStates = nextStates;
	}
	
	public ArrayList<TransparentActorCoreRebecaTransitionSystemState> getPreviousStates() {
		return previousStates;
	}
	
	public void setPreviousStates(ArrayList<TransparentActorCoreRebecaTransitionSystemState> previousStates) {
		this.previousStates = previousStates;
	}
	
	public void addPreviousState(TransparentActorCoreRebecaTransitionSystemState previousState) {
		previousStates.add(previousState);
	}
	
	public void addNextState(TransparentActorCoreRebecaTransitionSystemState nextState) {
		nextStates.add(nextState);
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "\n-------------\n" + id + "->" + state.toString() + "\n-------------\n";
	}
}