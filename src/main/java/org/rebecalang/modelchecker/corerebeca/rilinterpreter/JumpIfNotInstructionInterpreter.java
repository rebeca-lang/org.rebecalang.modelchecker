package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.Variable;

public class JumpIfNotInstructionInterpreter extends InstructionInterpreter {

	
	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
	JumpIfNotInstructionBean jiib = (JumpIfNotInstructionBean) ib;
		if (jiib.getCondition() == null) {
			actorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
			return;
		}	
		Object tempCond = jiib.getCondition();
		if ((jiib.getCondition() instanceof Variable)) {
			tempCond = (Boolean) actorState.retreiveVariableValue((Variable) jiib.getCondition());
		}
		if (tempCond == Boolean.FALSE) {
			actorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
		} else {
			actorState.increasePC();
		}

	}

}
