package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.HashMap;

public class TransparentActorCoreRebecaTransitionSystem {

	TransparentActorCoreRebecaTransitionSystemState initialState;
	
	HashMap<Long, TransparentActorCoreRebecaTransitionSystemState> transitionSystemStates;
	
	public void setInitialState(TransparentActorCoreRebecaTransitionSystemState initialState) {
		this.initialState = initialState;
	}
	
	public TransparentActorCoreRebecaTransitionSystemState getInitialState() {
		return initialState;
	}
}
