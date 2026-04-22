package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.compositionlevel;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule.TimedRebecaTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaCompositionLevelTakeMessageRule extends AbstractSOSRule<AbstractSystemState> {

	@Autowired
	TimedRebecaTakeMessageRule takeMessageRule;

	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state, Object... additional) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = new Transition<AbstractSystemState>();

		for(int actorId : base.getActorsIds()) {
			try {
				AbstractActorState actorState = state.getActorState(actorId);
				Transition<? extends AbstractActorState> result = 
						takeMessageRule.applyRule(
								base.getActorState(actorId), 
								actorState);
				TakeMessageAction action = (TakeMessageAction) result.getDestinationsActions().get(0);
				actorState.setVariableValue(AbstractMessageState.SENDER, 
						state.getActorState(action.getMessage().getSenderId()));
				transitions.addDestination(result.getDestinationsActions().get(0), state);
				state = base.clone();
			} catch(RuleIsDisabledException ride) {
			}
		}
		
		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}


}