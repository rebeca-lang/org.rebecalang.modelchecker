package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;

public class NetworkDeliveryAction extends Action {

	private CoreRebecaMessageState coreRebecaMessageState;
	
	public NetworkDeliveryAction(CoreRebecaMessageState coreRebecaMessageState) {
		this.coreRebecaMessageState = coreRebecaMessageState;
	}
	
	@Override
	public String getActionLabel() {
		return "deliver[" + this.coreRebecaMessageState + "]";
	}
	
	public String toString() {
		return coreRebecaMessageState.toString();
	}

}
