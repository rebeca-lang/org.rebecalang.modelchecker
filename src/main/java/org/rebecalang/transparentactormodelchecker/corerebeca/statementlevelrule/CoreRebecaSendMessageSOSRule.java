package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaSendMessageSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>>  {

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(
			Pair<CoreRebecaActorState, InstructionBean> source) {
		MsgsrvCallInstructionBean msgsrvCall = (MsgsrvCallInstructionBean) source.getSecond();
		CoreRebecaMessageState message = new CoreRebecaMessageState();
		message.setName(msgsrvCall.getMethodName());
		CoreRebecaActorState senderActor = source.getFirst();
		message.setSender(senderActor);
		if(msgsrvCall.getBase() != null)
			message.setReceiver((CoreRebecaActorState) senderActor.getVariableValue(msgsrvCall.getBase().getVarName()));
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

		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = 
				new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState,InstructionBean>>();
		result.setDestination(source);
		result.setAction(action);

		return result;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(Action action,
			Pair<CoreRebecaActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}


}
