package org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelrule;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule.NetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaNetworkLevelDeliverMessage extends NetworkLevelDeliverMessage {

	@SuppressWarnings("unchecked")
	@Override
	public Transition<AbstractNetworkState> applyRule(AbstractNetworkState base, AbstractNetworkState state, Object... additional) throws RuleIsDisabledException {
		Transition<AbstractNetworkState> transitions = new Transition<AbstractNetworkState>();
		CoreRebecaNetworkState coreRebecaNetworkState = (CoreRebecaNetworkState)state;
		CoreRebecaNetworkState coreRebecaBaseNetworkState = (CoreRebecaNetworkState)base;
		
		Object[] entriesArray = coreRebecaNetworkState.getReceivedMessages().keySet().toArray();
		for(int cnt = 0; cnt < entriesArray.length; cnt++) {
			Pair<Integer, Integer> key = (Pair<Integer, Integer>) entriesArray[cnt];
			ArrayList<CoreRebecaMessageState> messages = coreRebecaNetworkState.getReceivedMessages().get(key);
			if(messages.isEmpty())
				continue;
			CoreRebecaMessageState message = messages.remove(0);
			if(messages.isEmpty())
				coreRebecaNetworkState.getReceivedMessages().remove(key);
			MessageAction action = new MessageAction(message);
			transitions.addDestination(action, state);
			if(cnt != entriesArray.length - 1)
				coreRebecaNetworkState = coreRebecaBaseNetworkState.clone();
		}
		
		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}
}