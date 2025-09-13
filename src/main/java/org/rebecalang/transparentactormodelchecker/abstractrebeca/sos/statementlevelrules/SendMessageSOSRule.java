package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.springframework.stereotype.Component;

@Component
public class SendMessageSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>>  {

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {
		MsgsrvCallInstructionBean msgsrvCall = (MsgsrvCallInstructionBean) state.getSecond();
		CoreRebecaMessageState message = new CoreRebecaMessageState();
		message.setName(msgsrvCall.getMethodName());
		AbstractActorState senderActor = state.getFirst();
		message.setSender(senderActor);
		if(msgsrvCall.getBase() != null)
			message.setReceiver((AbstractActorState) senderActor.getVariableValue(msgsrvCall.getBase().getVarName()));
		else
			message.setReceiver(senderActor);
		for(Entry<String, Object> entry : msgsrvCall.getParameters().entrySet()) {
			Object value = entry.getValue();
			if(value instanceof Variable) {
				value = senderActor.getVariableValue(msgsrvCall.getBase().getVarName());
			}
			message.addParameter(entry.getKey(), value);
		}
		MessageAction action = new MessageAction(message);
		senderActor.movePCtoTheNextInstruction();

		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
				new DeterministicTransition<Pair<? extends AbstractActorState,InstructionBean>>();
		result.setDestination(state);
		result.setAction(action);

		return result;
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Action action,
			Pair<? extends AbstractActorState, InstructionBean> state) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}


}
