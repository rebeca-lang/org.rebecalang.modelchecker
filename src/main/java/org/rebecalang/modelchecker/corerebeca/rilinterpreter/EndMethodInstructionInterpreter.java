package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.AbstractExpressionTranslator;

public class EndMethodInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		Object retreivedReturnVariableValue = actorState.retreiveVariableValue(AbstractExpressionTranslator.RETURN_VALUE);
		actorState.popFromActorScope();
		actorState.setVariableValue(AbstractExpressionTranslator.RETURN_VALUE, retreivedReturnVariableValue);
	}

	@Override
	public String toString() {
		return "endMethod";
	}
}
