package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.ArrayList;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class CallMsgSrvInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		CallMsgSrvInstructionBean cmib = (CallMsgSrvInstructionBean) ib;
		ActorState receiverState = (ActorState) actorState.retrieveVariableValue(cmib.getReceiver());
		String msgSrvName = receiverState.getTypeName() + "." + cmib.getMsgsrvName().split("\\.")[1];
		MessageSpecification msgSpec = new MessageSpecification( msgSrvName, new ArrayList<Object>(), actorState);
		receiverState.addToQueue(msgSpec);
		actorState.increasePC();
	}
}