package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EndMethodInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		try {
			Object retreivedReturnVariableValue = baseActorState.retrieveVariableValue(InstructionUtilities.RETURN_VALUE);
			Object callResultVariable = baseActorState.retrieveVariableValue(MethodCallInstructionInterpreter.CALL_RESULT);
			baseActorState.popFromActorScope();
			if(callResultVariable != null)
				baseActorState.setVariableValue((String)callResultVariable, retreivedReturnVariableValue);
		} catch(RebecaRuntimeInterpreterException re) {
			baseActorState.popFromActorScope();
		}
	}

	@Override
	public String toString() {
		return "endMethod";
	}
}
