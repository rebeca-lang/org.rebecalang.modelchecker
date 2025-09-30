package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;

public class TransparentActorTransitionSystemState<T> {
	
	private int id;
	private T state;
	private ArrayList<TransparentActorTransitionSystemTransition> nextStates;
	private ArrayList<TransparentActorTransitionSystemTransition> previousStates;


	public TransparentActorTransitionSystemState(int id) {
		this.id = id;
		nextStates = new ArrayList<TransparentActorTransitionSystemTransition>();
		previousStates = new ArrayList<TransparentActorTransitionSystemTransition>();
	}

	public T getState() {
		return state;
	}
	
//	public T getActorState(int id) {
//		return state.getActorState(id);
//	}
	
	public void setState(T state) {
		this.state = state;
	}
	
	public ArrayList<TransparentActorTransitionSystemTransition> getNextStates() {
		return nextStates;
	}
	
	public void setNextStates(ArrayList<TransparentActorTransitionSystemTransition> nextStates) {
		this.nextStates = nextStates;
	}
	
	public ArrayList<TransparentActorTransitionSystemTransition> getPreviousStates() {
		return previousStates;
	}
	
	public void setPreviousStates(ArrayList<TransparentActorTransitionSystemTransition> previousStates) {
		this.previousStates = previousStates;
	}
	
	public void addPreviousState(TransparentActorTransitionSystemState<T> previousState) {
		addPreviousState(previousState, TauAction.TAU);
	}
	
	public void addPreviousState(TransparentActorTransitionSystemState<T> previousState, Action action) {
		previousStates.add(new TransparentActorTransitionSystemTransition(previousState, action));
	}
	
	public void addNextState(TransparentActorTransitionSystemState<T> nextState) {
		addNextState(nextState, TauAction.TAU);
	}
	
	public void addNextState(TransparentActorTransitionSystemState<T> nextState, Action action) {
		nextStates.add(new TransparentActorTransitionSystemTransition(nextState, action));
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "\n-------------\n" + id + "->" + state.toString() + "\n-------------\n";
	}
}