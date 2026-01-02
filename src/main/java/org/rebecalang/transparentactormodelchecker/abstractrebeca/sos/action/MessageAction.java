package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;

public class MessageAction extends Action {

	private AbstractMessageState message;
	
	public MessageAction(AbstractMessageState message) {
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
		return "tau[" + message.getSenderId() + "->" + 
				message.getReceiverId() + "(" + message.getName() + ")]";
	}
	
	public String toString() {
		return message.toString();
	}

}
