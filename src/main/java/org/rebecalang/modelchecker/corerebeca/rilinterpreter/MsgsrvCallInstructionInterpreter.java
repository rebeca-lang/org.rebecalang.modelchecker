package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.Map;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsgsrvCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		MsgsrvCallInstructionBean mcib = (MsgsrvCallInstructionBean) ib;
		Map<String, Object> parameters = setMsgSrvParameters(baseActorState, mcib.getParameters());

		MessageSpecification msgSpec = new MessageSpecification(mcib.getMethodName(), parameters, baseActorState);

		@SuppressWarnings("unchecked")
		BaseActorState<MessageSpecification> receiverState = (BaseActorState<MessageSpecification>) baseActorState.retrieveVariableValue(mcib.getBase());
		receiverState.addToQueue(msgSpec);
		baseActorState.increasePC();
	}
}