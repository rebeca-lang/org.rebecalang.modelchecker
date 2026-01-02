package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class InstructionUtilities {
	public static final String PC_STRING = "$PC$";
	public static final String RETURN_VALUE = "$RETURN_VALUE$";

	public static Object getValue(StatementInterpreterContainer statementInterpreterContainer,
			Object operand, BaseActorState<?> baseActorState) {
		if (operand instanceof Variable)
			return baseActorState.retrieveVariableValue(((Variable)operand).getVarName());
		if (operand instanceof NonDetValue) {
			return getValue(statementInterpreterContainer, getNonDetValue(statementInterpreterContainer, operand), baseActorState);
		}
		return operand;
	}

	public static Object getNonDetValue(StatementInterpreterContainer statementInterpreterContainer, Object operand) {
		NonDetValue nonDetValue = (NonDetValue) operand;
		Object value = nonDetValue.getValue();

		if (!statementInterpreterContainer.hasNondeterminism()) {
			if(nonDetValue.hasNext()) {
				nonDetValue.next();
				statementInterpreterContainer.reportNondeterminism();
			} else
				nonDetValue.reset();
		}

		return value;
	}
}
