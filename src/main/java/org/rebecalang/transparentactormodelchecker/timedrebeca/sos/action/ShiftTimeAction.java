package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.action;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;

public class ShiftTimeAction extends Action {

	private Action action;
	private int shift;
	
	public ShiftTimeAction(Action action, int shift) {
		this.action = action;
		this.shift = shift;
	}

	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public int getShift() {
		return shift;
	}
	public void setShift(int shift) {
		this.shift = shift;
	}
	
	@Override
	public String getActionLabel() {
		return action.getActionLabel() + ">>" + shift;
	}
	
	public String toString() {
		return action.toString() + ">>" + shift;
	}

}
