package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.networklevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule.NetworkLevelDeliverMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaNetworkLevelDeliverMessage extends NetworkLevelDeliverMessageRule {

	@Override
	public Transition<AbstractNetworkState> applyRule(AbstractNetworkState base, AbstractNetworkState state,
			Object... additional) throws RuleIsDisabledException {
		Transition<AbstractNetworkState> transitions = new Transition<AbstractNetworkState>();
		TimedRebecaNetworkState timedRebecaNetworkState = (TimedRebecaNetworkState)state;
		TimedRebecaNetworkState timedRebecaBaseNetworkState = (TimedRebecaNetworkState)base;
		int time = (int) additional[0];
		
//		timedRebecaNetworkState.getReceivedMessages();
//		Object[] entriesArray = timedRebecaNetworkState.getReceivedMessages().keySet().toArray();
//		for(int cnt = 0; cnt < entriesArray.length; cnt++) {
//			Pair<Integer, Integer> key = (Pair<Integer, Integer>) entriesArray[cnt];
//			ArrayList<CoreRebecaMessageState> messages = timedRebecaNetworkState.getReceivedMessages().get(key);
//			if(messages.isEmpty())
//				continue;
//			CoreRebecaMessageState message = messages.remove(0);
//			if(messages.isEmpty())
//				timedRebecaNetworkState.getReceivedMessages().remove(key);
//			MessageAction action = new MessageAction(message);
//			transitions.addDestination(action, state);
//			if(cnt != entriesArray.length - 1)
//				timedRebecaNetworkState = timedRebecaBaseNetworkState.clone();
//		}
		
		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

}