package org.rebecalang.transparentactormodelchecker.abstractrebeca;

import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

@SuppressWarnings("serial")
public class ModelCheckingException extends Exception {
	private TransparentActorTransitionSystemState<CoreRebecaSystemState> counterExampleFinalState;
	
	public TransparentActorTransitionSystemState<CoreRebecaSystemState> getCounterExampleFinalState() {
		return counterExampleFinalState;
	}

	public ModelCheckingException(TransparentActorTransitionSystemState<CoreRebecaSystemState> 
			counterExampleFinalState) {
		this.counterExampleFinalState = counterExampleFinalState;
	}

}
