package org.rebecalang.transparentactormodelchecker.abstractrebeca;

import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;

@SuppressWarnings("serial")
public class ModelCheckingException extends Exception {
	private TransparentActorTransitionSystemState<? extends AbstractSystemState> counterExampleFinalState;
	
	public TransparentActorTransitionSystemState<? extends AbstractSystemState> getCounterExampleFinalState() {
		return counterExampleFinalState;
	}

	public ModelCheckingException(TransparentActorTransitionSystemState<? extends AbstractSystemState> 
			counterExampleFinalState) {
		this.counterExampleFinalState = counterExampleFinalState;
	}

}
