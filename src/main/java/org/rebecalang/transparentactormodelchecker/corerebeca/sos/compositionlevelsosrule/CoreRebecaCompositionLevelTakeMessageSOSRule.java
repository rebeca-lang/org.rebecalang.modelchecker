package org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule.CoreRebecaActorLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelTakeMessageSOSRule extends AbstractSOSRule<CoreRebecaSystemState> {

	@Autowired
	CoreRebecaActorLevelTakeMessageSOSRule coreRebecaActorLevelTakeAMessageSOSRule;

	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState base, CoreRebecaSystemState state) throws RuleIsDisabledException {
		NondeterministicTransition<CoreRebecaSystemState> transitions = new NondeterministicTransition<CoreRebecaSystemState>();

		boolean firstRound = true;
		for(int actorId : base.getActorsIds()) {
			if (coreRebecaActorLevelTakeAMessageSOSRule.isEnabled((CoreRebecaActorState) base.getActorState(actorId))) {
				if(!firstRound)
					state = base.clone();
				firstRound = false;
				DeterministicTransition<CoreRebecaActorState> result = 
						coreRebecaActorLevelTakeAMessageSOSRule.applyRule(
								(CoreRebecaActorState) base.getActorState(actorId), 
								(CoreRebecaActorState) state.getActorState(actorId));
				transitions.addDestination(result.getAction(), state);
			}
		}
		
		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(
			CoreRebecaSystemState base, Action action,
			CoreRebecaSystemState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Composition level take message rule does not accept input action");
	}
}