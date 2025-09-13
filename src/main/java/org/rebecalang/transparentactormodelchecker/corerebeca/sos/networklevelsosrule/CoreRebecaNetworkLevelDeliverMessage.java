package org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelsosrule;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaNetworkLevelDeliverMessage extends AbstractSOSRule<CoreRebecaNetworkState> {

	@SuppressWarnings("unchecked")
	@Override
	public NondeterministicTransition<CoreRebecaNetworkState> applyRule(CoreRebecaNetworkState base, CoreRebecaNetworkState state) throws RuleIsDisabledException {
		NondeterministicTransition<CoreRebecaNetworkState> transitions = new NondeterministicTransition<CoreRebecaNetworkState>();
		
		Object[] entriesArray = base.getReceivedMessages().keySet().toArray();
		for(int cnt = 0; cnt < entriesArray.length; cnt++) {
			Pair<Integer, Integer> key = (Pair<Integer, Integer>) entriesArray[cnt];
			ArrayList<CoreRebecaMessageState> messages = state.getReceivedMessages().get(key);
			if(messages.isEmpty())
				continue;
			CoreRebecaMessageState message = messages.remove(0);
			if(messages.isEmpty())
				state.getReceivedMessages().remove(key);
			MessageAction action = new MessageAction(message);
			transitions.addDestination(action, state);
			if(cnt != entriesArray.length - 1)
				state = base.clone();
		}
		
		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

	@Override
	public NondeterministicTransition<CoreRebecaNetworkState> applyRule(
			CoreRebecaNetworkState base, 
			Action action, CoreRebecaNetworkState source) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Network level deliver message rule does not accept input action");
	}

}
