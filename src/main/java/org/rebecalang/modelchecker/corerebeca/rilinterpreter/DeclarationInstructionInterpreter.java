package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.rilinstructions.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;

public class DeclarationInstructionInterpreter extends InstructionInterpreter{

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		DeclarationInstructionBean dib = (DeclarationInstructionBean) ib;
		actorState.addVariableToRecentScope(dib.getVarName(), 0);
		actorState.increasePC();
	}
}
