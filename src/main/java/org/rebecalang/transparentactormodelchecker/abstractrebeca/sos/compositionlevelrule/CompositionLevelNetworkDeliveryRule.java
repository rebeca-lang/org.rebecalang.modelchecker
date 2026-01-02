package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.ReceiveMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelrule.CoreRebecaNetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompositionLevelNetworkDeliveryRule extends AbstractSOSRule<AbstractSystemState> {


	@Autowired
	ReceiveMessageRule actorLevelReceiveMessageRule;
	
	@Autowired
	CoreRebecaNetworkLevelDeliverMessage coreRebecaNetworkLevelDeliverMessage;

	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state, Object... additional) throws RuleIsDisabledException {
		
		if(!state.getNetworkState().hasMessage())
			throw new RuleIsDisabledException();
		
		Transition<AbstractNetworkState> deliveredMessages = 
				coreRebecaNetworkLevelDeliverMessage.applyRule(base.getNetworkState(), state.getNetworkState());
		Transition<AbstractSystemState> transitions = 
				new Transition<AbstractSystemState>();
		for(int cnt = 0; cnt < deliveredMessages.size(); cnt++) {
//			AbstractNetworkState networkState = deliveredMessages.getDestinationsStates().get(cnt);
			MessageAction action = (MessageAction) deliveredMessages.getDestinationsActions().get(cnt);
			AbstractMessageState message = action.getMessage();
			AbstractActorState actorState = state.getActorState(message.getReceiverId());
			actorLevelReceiveMessageRule.applyRule(actorState, actorState, action);
			transitions.addDestination(action, state);
			if(cnt != deliveredMessages.size() - 1)
				state = base.clone();
		}
		
//		for(Iterator<Pair<? extends Action, CoreRebecaNetworkState>> iterator = deliveredMessages.getDestinations().iterator(); iterator.hasNext();) {
//			Pair<? extends Action, CoreRebecaNetworkState> deliverable = iterator.next(); 
//			MessageAction action = (MessageAction) deliverable.getFirst();
//			state.setNetworkState(deliverable.getSecond());
//			((CoreRebecaActorState)state.getActorState(action.getMessage().getReceiver().getId())).
//				receiveMessage(action.getMessage());
//			transitions.addDestination(new NetworkDeliveryAction(action.getMessage()), state);
//			if(iterator.hasNext())
//				state = base.clone();
//		}
			
		if(transitions.size() == 0)
			throw new RuleIsDisabledException();

		return transitions;
	}


}
