package org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule.CoreRebecaActorLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelTakeMessageSOSRule extends AbstractSOSRule<CoreRebecaSystemState> {

	@Autowired
	CoreRebecaActorLevelTakeMessageSOSRule coreRebecaActorLevelTakeAMessageSOSRule;

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState source) throws RuleIsDisabledException {
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = new CoreRebecaNondeterministicTransition<CoreRebecaSystemState>();

		CoreRebecaSystemState backup = RebecaStateSerializationUtils.clone(source);
		for (int actorId : backup.getActorsIds()) {
			CoreRebecaActorState coreRebecaActorState = source.getActorState(actorId);
			if (coreRebecaActorLevelTakeAMessageSOSRule.isEnable(coreRebecaActorState)) {

				CoreRebecaDeterministicTransition<CoreRebecaActorState> result = 
						coreRebecaActorLevelTakeAMessageSOSRule.applyRule(coreRebecaActorState);
				transitions.addDestination(result.getAction(), source);
				source = RebecaStateSerializationUtils.clone(backup);
			}
		}
		
		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

	@Override
	public CoreRebecaNondeterministicTransition<CoreRebecaSystemState> applyRule(Action action,
			CoreRebecaSystemState source) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Composition level take message rule does not accept input action");
	}
}