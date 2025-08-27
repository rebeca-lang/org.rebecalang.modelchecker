package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;

public class TakeMessageAction extends Action {

	private CoreRebecaMessageState message;
	
	public TakeMessageAction(CoreRebecaMessageState message) {
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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(").append(message.getSender().getId()).
			append("->").
			append(message.getReceiver().getId()).
			append(")::").
			append(message.getName());
		return stringBuilder.toString();
	}
	
	public String toString() {
		return message.toString();
	}

}
