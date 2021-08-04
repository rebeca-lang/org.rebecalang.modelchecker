package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class InstructionUtilities {
	public static final String PC_STRING = "$PC$";

	public static Object getValue(Object operand, ActorState actorState) {
		if (operand instanceof Variable)
			return actorState.retrieveVariableValue(((Variable)operand).getVarName());
		if (operand instanceof NonDetValue) {
			NonDetValue nonDetValue = (NonDetValue) operand;
			Object value = nonDetValue.getValue();
			if (!StatementInterpreterContainer.getInstance().hasNondeterminism()) {
				if(nonDetValue.hasNext()) {
					nonDetValue.next();
					StatementInterpreterContainer.getInstance().reportNondeterminism();
				} else
					nonDetValue.reset();
			}
			return getValue(value, actorState);
		}
		return operand;
	}
	
}
