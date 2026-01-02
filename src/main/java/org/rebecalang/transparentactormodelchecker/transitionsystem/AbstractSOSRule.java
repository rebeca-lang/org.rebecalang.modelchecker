package org.rebecalang.transparentactormodelchecker.transitionsystem;

public abstract class AbstractSOSRule<T> {

//	public boolean isEnabled(T source) {
//		return true;
//	}
	
	public abstract Transition<T> applyRule(T base, T state, Object ... additional) throws RuleIsDisabledException;

}
