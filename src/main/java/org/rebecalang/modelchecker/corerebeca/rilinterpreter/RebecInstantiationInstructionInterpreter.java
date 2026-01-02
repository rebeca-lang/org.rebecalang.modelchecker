package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RebecInstantiationInstructionInterpreter extends InstructionInterpreter {

	
    @Override
    public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
        baseActorState.increasePC();
        MethodCallInstructionBean mcib = (MethodCallInstructionBean) ib;
        baseActorState.pushInScopeStackForMethodCallInitialization(mcib.getMethodName().split("\\.")[0]);
        for (Entry<String, Object> entry : mcib.getParameters().entrySet()) {
            String paramName = entry.getKey();
			Object paramValue = entry.getValue();
            if (paramValue instanceof Variable)
                paramValue = baseActorState.retrieveVariableValue((Variable) paramValue);
			baseActorState.addVariableToRecentScope(paramName, paramValue);
        }
        baseActorState.initializePC(mcib.getMethodName(), 0);
    }
}
