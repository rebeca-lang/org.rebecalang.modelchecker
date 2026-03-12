package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.ReceiveMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule.NetworkLevelDeliverMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;

public class CompositionLevelNetworkDeliveryRule extends AbstractSOSRule<AbstractSystemState> {

	@Autowired
	ReceiveMessageRule actorLevelReceiveMessageRule;
	
	NetworkLevelDeliverMessageRule networkLevelDeliverMessage;
	
	public void setNetworkLevelDeliverMessage(NetworkLevelDeliverMessageRule networkLevelDeliverMessage) {
		this.networkLevelDeliverMessage = networkLevelDeliverMessage;
	}

	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state, Object... additional) throws RuleIsDisabledException {
		
		if(!state.getNetworkState().hasMessage())
			throw new RuleIsDisabledException();
		
		Transition<AbstractNetworkState> deliveredMessages = 
				networkLevelDeliverMessage.applyRule(base.getNetworkState(), state.getNetworkState());
		if(deliveredMessages.isEmpty())
			throw new RuleIsDisabledException();

		Transition<AbstractSystemState> transitions = 
				new Transition<AbstractSystemState>();
		for(int cnt = 0; cnt < deliveredMessages.size(); cnt++) {
			MessageAction action = (MessageAction) deliveredMessages.getDestinationsActions().get(cnt);
			AbstractMessageState message = action.getMessage();
			AbstractActorState actorState = state.getActorState(message.getReceiverId());
			actorLevelReceiveMessageRule.applyRule(actorState, actorState, action);
			transitions.addDestination(action, state);
			if(cnt != deliveredMessages.size() - 1)
				state = base.clone();
		}
		
		return transitions;
	}
}