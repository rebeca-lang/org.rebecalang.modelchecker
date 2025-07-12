package org.rebecalang.transparentactormodelchecker.corerebeca.networklevelsosrule;

import java.util.ArrayList;
import java.util.Iterator;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaNetworkLevelDeliverMessage extends AbstractSOSRule<CoreRebecaNetworkState> {

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaNetworkState> applyRule(CoreRebecaNetworkState source) {
		CoreRebecaNondeterministicTransition<CoreRebecaNetworkState> transitions = new CoreRebecaNondeterministicTransition<CoreRebecaNetworkState>();
		CoreRebecaNetworkState backup = RebecaStateSerializationUtils.clone(source);
		Iterator<Pair<Integer, Integer>> entrySetIterator = backup.getReceivedMessages().keySet().iterator();
		while(entrySetIterator.hasNext()) {
			Pair<Integer, Integer> key = entrySetIterator.next();
			ArrayList<CoreRebecaMessageState> messages = source.getReceivedMessages().get(key);
			if(messages.isEmpty())
				continue;
			CoreRebecaMessageState message = messages.remove(0);
			if(messages.isEmpty())
				source.getReceivedMessages().remove(key);
			MessageAction action = new MessageAction(message);
			transitions.addDestination(action, source);
			source = RebecaStateSerializationUtils.clone(backup);
		}
		return transitions;
	}

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaNetworkState> applyRule(Action action, CoreRebecaNetworkState source) {
		throw new RebecaRuntimeInterpreterException("Network level deliver message rule does not accept input action");
	}

}
