package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class AssignmentInstructionInterpreter extends InstructionInterpreter {

	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		AssignmentInstructionBean aib = (AssignmentInstructionBean) ib;
		Object valueFirst = InstructionUtilities.getValue(aib.getFirstOperand(), actorState);
		Object valueSecond = InstructionUtilities.getValue(aib.getSecondOperand(), actorState);
		Object result = valueFirst;
		String operator = aib.getOperator();
		if (operator != null) {
			if (valueFirst instanceof ActorState) {
				if (operator.equals("=="))
					result = (((ActorState) valueFirst).getName().
							equals(((ActorState) valueSecond).getName()));
				else if (operator.equals("!="))
					result = !(((ActorState) valueFirst).getName().
							equals(((ActorState) valueSecond).getName()));
				else
					throw new RebecaRuntimeInterpreterException(
							"this case should not happen!! should've been reported as an error by compiler!");
			} else
				result = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
		}
		actorState.setVariableValue(((Variable) aib.getLeftVarName()).getVarName(), result);
		actorState.increasePC();
	}
}
