package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;

public class TransparentActorTransitionSystemTransition {

	private TransparentActorTransitionSystemState<?> target;
	private Action action;
	
	public TransparentActorTransitionSystemTransition(TransparentActorTransitionSystemState<?> target,
			Action action) {
		this.target = target;
		this.action = action;
	}

	public TransparentActorTransitionSystemState<?> getTarget() {
		return target;
	}
	
	public void setTarget(TransparentActorTransitionSystemState<?> target) {
		this.target = target;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
}
