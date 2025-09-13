package org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;

public class DeterministicTransition<T> extends AbstractTransition<T> {
		
	private T destination;
	private Action action;

	public DeterministicTransition() {
		action = TauAction.TAU;
	}
	
	public DeterministicTransition(T destination) {
		super();
		this.destination = destination;
	}
	
	public DeterministicTransition(Action action, T destination) {
		this.action = action;
		this.destination = destination;
	}

	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}

	public T getDestination() {
		return destination;
	}
	public void setDestination(T destination) {
		this.destination = destination;
	}
}
