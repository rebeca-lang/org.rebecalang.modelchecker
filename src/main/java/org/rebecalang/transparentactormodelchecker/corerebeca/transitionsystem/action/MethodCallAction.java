package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

public class MethodCallAction extends Action {

	private String message;
	
	public MethodCallAction(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String getActionLable() {
		return message;
	}
	
	public String toString() {
		return message;
	}

}
