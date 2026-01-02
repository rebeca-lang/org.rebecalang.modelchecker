package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PopARInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {

		PopARInstructionBean paib = (PopARInstructionBean) ib;
		for (int i = 0; i < paib.getNumberOfPops(); i++)
			baseActorState.popFromActorScope();
		baseActorState.increasePC();
	}

}
