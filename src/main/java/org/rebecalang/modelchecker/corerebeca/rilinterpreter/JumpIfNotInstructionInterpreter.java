package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JumpIfNotInstructionInterpreter extends InstructionInterpreter {
	
	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
	JumpIfNotInstructionBean jiib = (JumpIfNotInstructionBean) ib;
		if (jiib.getCondition() == null) {
			baseActorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
			return;
		}	
		Object tempCond = jiib.getCondition();
		if ((jiib.getCondition() instanceof Variable)) {
			tempCond = baseActorState.retrieveVariableValue((Variable) jiib.getCondition());
		}
		if (tempCond.equals(Boolean.FALSE)) {
			baseActorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
		} else {
			baseActorState.increasePC();
		}

	}

}
