package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule.statementlevelrule;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.TimedMsgsrvCallInstructionBean;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.SendMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaSendMessageSOSRule extends SendMessageRule  {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {
		TimedMsgsrvCallInstructionBean msgsrvCall = (TimedMsgsrvCallInstructionBean) additional[0];
		Transition<AbstractActorState> result = super.applyRule(base, state, additional);
		MessageAction action = (MessageAction) result.getDestinationsActions().get(0);
		TimedRebecaMessageState message = (TimedRebecaMessageState) action.getMessage();

		Object value = msgsrvCall.getAfter();
		if(value instanceof Variable) {
			value = state.getVariableValue(msgsrvCall.getBase());
		}
		message.setArrival(((Number)value).intValue());
		
		value = msgsrvCall.getDeadline();
		if(value instanceof Variable) {
			value = state.getVariableValue(msgsrvCall.getBase());
		}
		message.setDeadline(((Number)value).intValue());

		return result;
	}
	
	protected AbstractMessageState createMessageState() {
		return new TimedRebecaMessageState();
	}
}