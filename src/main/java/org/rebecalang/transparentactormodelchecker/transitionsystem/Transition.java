package org.rebecalang.transparentactormodelchecker.transitionsystem;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;

public class Transition<T> {
	private List<Action> destinationsActions;
	private List<T> destinationsStates;
	
	public static <T> Transition<T> createDeterministicTransition(Action action, T state) {
		List<Action> destinationsActions = List.of(action);
		List<T> destinationsStates = List.of(state);
		Transition<T> retValue = new Transition<T>(destinationsActions, destinationsStates);
		return retValue;
	}
	
	public static <T> Transition<T> createDeterministicTauTransition(T state) {
		List<Action> destinationsActions = List.of(TauAction.TAU);
		List<T> destinationsStates = List.of(state);
		Transition<T> retValue = new Transition<T>(destinationsActions, destinationsStates);
		return retValue;
	}
	
	private Transition(List<Action> destinationsActions, List<T> destinationsStates) {
		this.destinationsActions = destinationsActions;
		this.destinationsStates = destinationsStates;
	}
	
	public Transition() {
		destinationsActions = new ArrayList<Action>();
		destinationsStates = new ArrayList<T>();
	}

	public List<Action> getDestinationsActions() {
		return destinationsActions;
	}
	
	public List<T> getDestinationsStates() {
		return destinationsStates;
	}
	
	public void addDestination(Action action, T state) {
		destinationsActions.add(action);
		destinationsStates.add(state);
	}
	
	public int size() {
		return destinationsStates.size();
	}
//	public void addAllDestinations(List<Pair<? extends Action, T>> destinations) {
//		this.destinations.addAll(destinations);
//	}

	public Transition<T> merge(Transition<T> tranistion) {
		destinationsActions.addAll(tranistion.getDestinationsActions());
		destinationsStates.addAll(tranistion.getDestinationsStates());
		return this;
	}

	public boolean isEmpty() {
		return destinationsStates.isEmpty();
	}
}
