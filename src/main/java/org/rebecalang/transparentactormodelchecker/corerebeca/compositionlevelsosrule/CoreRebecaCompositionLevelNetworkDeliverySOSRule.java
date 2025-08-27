package org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule.CoreRebecaActorLevelReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.networklevelsosrule.CoreRebecaNetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.NetworkDeliveryAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelNetworkDeliverySOSRule extends AbstractSOSRule<CoreRebecaSystemState> {


	@Autowired
	CoreRebecaNetworkLevelDeliverMessage coreRebecaNetworkLevelDeliverMessage;
	@Autowired
	CoreRebecaActorLevelReceiveSOSRule coreRebecaActorLevelReceiveSOSRule;
	
	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(Action action, CoreRebecaSystemState source) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Composition level network delivery rule does not accept input action");	
	}

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState source) throws RuleIsDisabledException {
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = new 
				CoreRebecaNondeterministicTransition<CoreRebecaSystemState>();
		
		if(source.getNetworkState().getReceivedMessages().size() == 0)
			throw new RuleIsDisabledException();
		
		CoreRebecaSystemState backup = null;
		CoreRebecaNondeterministicTransition<CoreRebecaNetworkState> deliveredMessageTransitions = 
				coreRebecaNetworkLevelDeliverMessage.applyRule(source.getNetworkState());
		
		for(Pair<? extends Action, CoreRebecaNetworkState> deliverable : deliveredMessageTransitions.getDestinations()) {
			backup = RebecaStateSerializationUtils.clone(source);
			MessageAction action = (MessageAction) deliverable.getFirst();
			source.setNetworkState(deliverable.getSecond());
			source.getActorState(action.getMessage().getReceiver().getId()).
				receiveMessage(action.getMessage());
			transitions.addDestination(new NetworkDeliveryAction(action.getMessage()), source);
			source = backup;
		}
			
		return transitions;
	}

}
