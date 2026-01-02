package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.TakeMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;

public class CompositionLevelTakeMessageRule extends AbstractSOSRule<AbstractSystemState> {

	TakeMessageRule actorLevelTakeMessageRule;

	public void setActorLevelTakeMessageRule(TakeMessageRule actorLevelTakeMessageRule) {
		this.actorLevelTakeMessageRule = actorLevelTakeMessageRule;
	}
	
	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state, Object... additional) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = new Transition<AbstractSystemState>();

		boolean firstRound = true;
		for(int actorId : base.getActorsIds()) {
			if (actorLevelTakeMessageRule.isEnabled(base.getActorState(actorId))) {
				if(!firstRound)
					state = base.clone();
				firstRound = false;
				AbstractActorState actorState = state.getActorState(actorId);
				Transition<? extends AbstractActorState> result = 
						actorLevelTakeMessageRule.applyRule(
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