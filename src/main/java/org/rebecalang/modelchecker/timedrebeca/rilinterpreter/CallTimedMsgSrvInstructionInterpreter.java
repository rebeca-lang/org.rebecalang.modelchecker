package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import java.util.ArrayList;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class CallTimedMsgSrvInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		CallMsgSrvInstructionBean cmib = (CallMsgSrvInstructionBean) ib;
		MessageSpecification msgSpec = new MessageSpecification(cmib.getMsgsrvName(), new ArrayList<Object>(), actorState);
		ActorState receiverState = (ActorState) actorState.retreiveVariableValue(cmib.getReceiver());
		receiverState.addToQueue(msgSpec);
		actorState.increasePC();
	}
}