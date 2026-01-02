package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.Map;
import java.util.TreeMap;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class InstructionInterpreter {
	
	@Autowired
	protected StatementInterpreterContainer statementInterpreterContainer;

	public abstract void interpret (InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel);

	public Map<String, Object> setMsgSrvParameters(BaseActorState<?> baseActorState, Map<String, Object> parameters2) {
		Map<String, Object> parameters = new TreeMap<String, Object>();
		for (Map.Entry<String, Object> entry : parameters2.entrySet()) {
			String paramName = entry.getKey();
			Object paramValue = entry.getValue();
			if (paramValue instanceof Variable)
				paramValue = baseActorState.retrieveVariableValue((Variable) paramValue);
			parameters.put(paramName, paramValue);
		}

		return parameters;
	}
}
