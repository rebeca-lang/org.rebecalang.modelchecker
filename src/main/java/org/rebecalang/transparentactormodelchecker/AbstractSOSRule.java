package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.AbstractTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;

public abstract class AbstractSOSRule<T> {
	public boolean isEnabled(CoreRebecaActorState source) {
		return true;
	}
	
	public abstract AbstractTransition<T> applyRule(T base, T state) throws RuleIsDisabledException;

	public abstract AbstractTransition<T> applyRule(T base, Action synchAction, T state) throws RuleIsDisabledException;

}
