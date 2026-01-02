package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExternalMethodCallInterpreter extends InstructionInterpreter {

	@Autowired
	ExternalMethodRepository externalMethodRepository;
	
	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState,
			State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		ExternalMethodCallInstructionBean bimcib = (ExternalMethodCallInstructionBean) ib;
		externalMethodRepository.execute(bimcib, baseActorState, globalState);
		
		baseActorState.increasePC();
	}
}
