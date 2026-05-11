package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import java.util.Map.Entry;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorStateRepresentor;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;

public class SendMessageRule extends AbstractSOSRule<AbstractActorState>  {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {
		MsgsrvCallInstructionBean msgsrvCall = (MsgsrvCallInstructionBean) additional[0];
		AbstractMessageState message = state.getNewMessageState();
		message.setName(msgsrvCall.getMethodName());
		message.setSenderId(state.getId());
		if(msgsrvCall.getBase() != null)
			message.setReceiverId(((AbstractActorState) state.getVariableValue(msgsrvCall.getBase())).getId());
		else
			message.setReceiverId(state.getId());
		for(Entry<String, Object> entry : msgsrvCall.getParameters().entrySet()) {
			Object value = entry.getValue();
			if(value instanceof Variable) {
				value = state.getVariableValue((Variable) value);
			}
			if(value instanceof AbstractActorState) {
				ActorStateRepresentor asr = new ActorStateRepresentor((AbstractActorState)value);
				message.addParameter(entry.getKey(), asr);
			} else
				message.addParameter(entry.getKey(), value);
		}
		MessageAction action = new MessageAction(message);
		state.movePCtoTheNextInstruction();

		return Transition.createDeterministicTransition(action, state);
	}
}