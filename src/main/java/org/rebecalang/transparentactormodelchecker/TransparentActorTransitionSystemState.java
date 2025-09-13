package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;

public class TransparentActorTransitionSystemState<T> {
	
	private int id;
	private T state;
	private ArrayList<TransparentActorTransitionSystemState<T>> nextStates;
	private ArrayList<TransparentActorTransitionSystemState<T>> previousStates;


	public TransparentActorTransitionSystemState(int id) {
		this.id = id;
		nextStates = new ArrayList<TransparentActorTransitionSystemState<T>>();
		previousStates = new ArrayList<TransparentActorTransitionSystemState<T>>();
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
	
	public ArrayList<TransparentActorTransitionSystemState<T>> getNextStates() {
		return nextStates;
	}
	
	public void setNextStates(ArrayList<TransparentActorTransitionSystemState<T>> nextStates) {
		this.nextStates = nextStates;
	}
	
	public ArrayList<TransparentActorTransitionSystemState<T>> getPreviousStates() {
		return previousStates;
	}
	
	public void setPreviousStates(ArrayList<TransparentActorTransitionSystemState<T>> previousStates) {
		this.previousStates = previousStates;
	}
	
	public void addPreviousState(TransparentActorTransitionSystemState<T> previousState) {
		previousStates.add(previousState);
	}
	
	public void addNextState(TransparentActorTransitionSystemState<T> nextState) {
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