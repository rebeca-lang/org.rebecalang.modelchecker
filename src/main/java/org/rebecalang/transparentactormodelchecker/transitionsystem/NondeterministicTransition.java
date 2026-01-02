package org.rebecalang.transparentactormodelchecker.transitionsystem;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;

@Deprecated
public class NondeterministicTransition<T> extends AbstractTransition<T> {
	private List<Pair<? extends Action, T>> destinations;
	
	public NondeterministicTransition() {
		destinations = new ArrayList<Pair<? extends Action,T>>();
	}

	public List<Pair<? extends Action, T>> getDestinations() {
		return destinations;
	}
	
	public void addDestination(Action action, T destination) {
		destinations.add(new Pair<Action, T>(action, destination));
	}
	
	public void addAllDestinations(List<Pair<? extends Action, T>> destinations) {
		this.destinations.addAll(destinations);
	}
}
