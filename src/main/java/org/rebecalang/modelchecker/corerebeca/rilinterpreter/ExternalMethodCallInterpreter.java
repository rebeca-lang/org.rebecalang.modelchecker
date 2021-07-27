package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class ExternalMethodCallInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		ExternalMethodCallInstructionBean bimcib = (ExternalMethodCallInstructionBean) ib;
		ExternalMethodRepository.getInstance().execute(bimcib, actorState, globalState);
		
		actorState.increasePC();
	}
}
