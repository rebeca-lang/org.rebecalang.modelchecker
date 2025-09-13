package org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule;

import java.util.Iterator;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NetworkDeliveryAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule.CoreRebecaActorLevelReceiveSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelsosrule.CoreRebecaNetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelNetworkDeliverySOSRule extends AbstractSOSRule<CoreRebecaSystemState> {


	@Autowired
	CoreRebecaNetworkLevelDeliverMessage coreRebecaNetworkLevelDeliverMessage;
	@Autowired
	CoreRebecaActorLevelReceiveSOSRule coreRebecaActorLevelReceiveSOSRule;
	
	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState base, Action action, CoreRebecaSystemState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Composition level network delivery rule does not accept input action");	
	}

	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState base, CoreRebecaSystemState state) throws RuleIsDisabledException {
		NondeterministicTransition<CoreRebecaSystemState> transitions = new 
				NondeterministicTransition<CoreRebecaSystemState>();
		
		if(state.getNetworkState().getReceivedMessages().size() == 0)
			throw new RuleIsDisabledException();
		
		NondeterministicTransition<CoreRebecaNetworkState> deliveredMessageTransitions = 
				coreRebecaNetworkLevelDeliverMessage.applyRule(base.getNetworkState(), state.getNetworkState());
		
		
		for(Iterator<Pair<? extends Action, CoreRebecaNetworkState>> iterator = deliveredMessageTransitions.getDestinations().iterator(); iterator.hasNext();) {
			Pair<? extends Action, CoreRebecaNetworkState> deliverable = iterator.next(); 
			MessageAction action = (MessageAction) deliverable.getFirst();
			state.setNetworkState(deliverable.getSecond());
			((CoreRebecaActorState)state.getActorState(action.getMessage().getReceiver().getId())).
				receiveMessage(action.getMessage());
			transitions.addDestination(new NetworkDeliveryAction(action.getMessage()), state);
			if(iterator.hasNext())
				state = base.clone();
		}
			
		if(transitions.getDestinations().size() == 0)
			throw new RuleIsDisabledException();

		return transitions;
	}

}
