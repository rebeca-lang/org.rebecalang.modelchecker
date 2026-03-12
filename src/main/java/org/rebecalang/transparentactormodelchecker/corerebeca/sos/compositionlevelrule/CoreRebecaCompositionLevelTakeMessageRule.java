package org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelrule.CoreRebecaTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelTakeMessageRule extends AbstractSOSRule<AbstractSystemState> {

	@Autowired
	CoreRebecaTakeMessageRule takeMessageRule;

	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state, Object... additional) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = new Transition<AbstractSystemState>();

		boolean firstRound = true;
		for(int actorId : base.getActorsIds()) {
			if (takeMessageRule.isEnabled(base.getActorState(actorId))) {
				if(!firstRound)
					state = base.clone();
				firstRound = false;
				AbstractActorState actorState = state.getActorState(actorId);
				Transition<? extends AbstractActorState> result = 
						takeMessageRule.applyRule(
								base.getActorState(actorId), 
								actorState);
				TakeMessageAction action = (TakeMessageAction) result.getDestinationsActions().get(0);
				actorState.setVariableValue(AbstractMessageState.SENDER, 
						state.getActorState(action.getMessage().getSenderId()));
				transitions.addDestination(result.getDestinationsActions().get(0), state);
			}
		}
		
		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

}