package org.rebecalang.modelchecker.corerebeca;

import java.util.Hashtable;

public class StateSpace {
	Hashtable<Long, State> statespace;
	State initialState;
	
	public StateSpace() {
		statespace = new Hashtable<Long, State>();
	}

	public void addState(State state) {
		addState(Long.valueOf(state.hashCode()), state);
	}
	
	public void addInitialState(State initialState) {
		this.initialState = initialState;
		addState(initialState);
	}

	public void addState(Long stateKey, State state) {
		statespace.put(stateKey, state);
		
	}

	public State getState(Long stateKey) {
		return statespace.get(stateKey);
	}

	public boolean hasStateWithKey(Long stateKey) {
		return statespace.containsKey(stateKey);
	}

	public State getInitialState() {
		return initialState;
	}

	public int size() {
		return statespace.size();
	}
}
