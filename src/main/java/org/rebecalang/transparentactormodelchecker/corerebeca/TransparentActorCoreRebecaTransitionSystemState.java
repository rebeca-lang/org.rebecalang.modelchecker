package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

class TransparentActorCoreRebecaTransitionSystemState {
	
	public static TransparentActorCoreRebecaTransitionSystemState createEmptyState() {
		TransparentActorCoreRebecaTransitionSystemState state = 
				new TransparentActorCoreRebecaTransitionSystemState();
		state.setNextStates(new ArrayList<CoreRebecaSystemState>());
		state.setPreviousStates(new ArrayList<CoreRebecaSystemState>());
		return state;
	}
	
	private CoreRebecaSystemState state;
	private ArrayList<CoreRebecaSystemState> nextStates;
	private ArrayList<CoreRebecaSystemState> previousStates;

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
	
	public ArrayList<CoreRebecaSystemState> getNextStates() {
		return nextStates;
	}
	
	public void setNextStates(ArrayList<CoreRebecaSystemState> nextStates) {
		this.nextStates = nextStates;
	}
	
	public ArrayList<CoreRebecaSystemState> getPreviousStates() {
		return previousStates;
	}
	
	public void setPreviousStates(ArrayList<CoreRebecaSystemState> previousStates) {
		this.previousStates = previousStates;
	}
}