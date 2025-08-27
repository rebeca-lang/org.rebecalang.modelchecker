package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.corerebeca.TransparentActorCoreRebecaTransitionSystem;

public class TransparentActorModelCheckingResult {

	public static final String DEADLOCK = "Deadlock";
	public static final String SATISFIED = "Satisfied";
	
	private String message;
	private TransparentActorCoreRebecaTransitionSystem transitionSystem;

	public TransparentActorModelCheckingResult(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public TransparentActorCoreRebecaTransitionSystem getTransitionSystem() {
		return transitionSystem;
	}
	public void setTransitionSystem(TransparentActorCoreRebecaTransitionSystem transitionSystem) {
		this.transitionSystem = transitionSystem;
	}
}
