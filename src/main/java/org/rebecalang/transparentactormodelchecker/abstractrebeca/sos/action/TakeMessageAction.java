package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;

public class TakeMessageAction extends Action {

	private AbstractMessageState message;
	
	public TakeMessageAction(AbstractMessageState message) {
		this.message = message;
	}
	public AbstractMessageState getMessage() {
		return message;
	}
	public void setMessage(AbstractMessageState message) {
		this.message = message;
	}
	
	@Override
	public String getActionLabel() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(").append(message.getSenderId()).
			append("->").
			append(message.getReceiverId()).
			append(")::").
			append(message.getName());
		return stringBuilder.toString();
	}
	
	public String toString() {
		return message.toString();
	}

}
