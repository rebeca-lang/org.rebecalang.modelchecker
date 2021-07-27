package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class NOPInstructionInterpreter extends InstructionInterpreter{

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		actorState.increasePC();
	}



}
