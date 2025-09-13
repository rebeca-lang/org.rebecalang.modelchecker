package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;

public class MessageAction extends Action {

	private CoreRebecaMessageState message;
	
	public MessageAction(CoreRebecaMessageState message) {
		this.message = message;
	}
	public CoreRebecaMessageState getMessage() {
		return message;
	}
	public void setMessage(CoreRebecaMessageState message) {
		this.message = message;
	}
	
	@Override
	public String getActionLabel() {
		return "tau[" + message.getSender().getId() + "->" + 
				message.getReceiver().getId() + "(" + message.getName() + ")]";
	}
	
	public String toString() {
		return message.toString();
	}

}
