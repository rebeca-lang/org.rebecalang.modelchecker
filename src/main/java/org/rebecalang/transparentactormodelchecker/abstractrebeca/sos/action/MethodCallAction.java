package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

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
	public String getActionLabel() {
		return message;
	}
	
	public String toString() {
		return message;
	}

}
