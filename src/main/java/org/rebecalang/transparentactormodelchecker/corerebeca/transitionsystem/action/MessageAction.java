package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

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
	public String getActionLable() {
		return message.getName();
	}
	
	public String toString() {
		return message.toString();
	}

}
