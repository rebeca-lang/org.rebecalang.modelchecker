package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule;

import java.util.ArrayList;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.TakeMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaTakeMessageRule extends TakeMessageRule {

	@Override
	public Transition<AbstractActorState> applyRule(AbstractActorState base, AbstractActorState state, Object... additional) throws RuleIsDisabledException {
		TimedRebecaActorState timedRebecaActorState = (TimedRebecaActorState)state;
		if(timedRebecaActorState.messageQueueIsEmpty())
			throw new RuleIsDisabledException();
		Transition<AbstractActorState> transition = new Transition<AbstractActorState>();
		
		int time = (int) timedRebecaActorState.getVariableValue(TimedActorScope.TIME_VARIABLE);
		ArrayList<Integer> indeces = timedRebecaActorState.getEnableMessagesIndeces(time);
		
		for(int cnt = 0; cnt < indeces.size(); cnt++) {
			Integer index = indeces.get(cnt);
			TimedRebecaMessageState message = timedRebecaActorState.getEnableMessage(index);
			prepareScope(timedRebecaActorState, message);
			transition.addDestination(new TakeMessageAction(message), timedRebecaActorState);
			if(cnt != indeces.size() - 1) {
				timedRebecaActorState = (TimedRebecaActorState) base.clone();
			}
		}
		if(transition.isEmpty())
			throw new RuleIsDisabledException();
		return transition;
	}
}
