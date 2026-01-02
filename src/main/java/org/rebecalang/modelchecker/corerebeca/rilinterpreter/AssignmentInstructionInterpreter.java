package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssignmentInstructionInterpreter extends InstructionInterpreter {

	CoreRebecaTypeSystem coreRebecaTypeSystem;

	@Autowired
	public AssignmentInstructionInterpreter(CoreRebecaTypeSystem coreRebecaTypeSystem) {
		this.coreRebecaTypeSystem = coreRebecaTypeSystem;
	}
	
	@Override
	public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
		AssignmentInstructionBean aib = (AssignmentInstructionBean) ib;
		Object valueFirst = InstructionUtilities.getValue(statementInterpreterContainer, aib.getFirstOperand(), baseActorState);
		Object valueSecond = InstructionUtilities.getValue(statementInterpreterContainer, aib.getSecondOperand(), baseActorState);
		Object result = valueFirst;
		String operator = aib.getOperator();
		if (operator != null) {
			if (valueFirst instanceof BaseActorState) {
				if (operator.equals("=="))
					result = (((BaseActorState<?>) valueFirst).getName().
							equals(((BaseActorState<?>) valueSecond).getName()));
				else if (operator.equals("!="))
					result = !(((BaseActorState<?>) valueFirst).getName().
							equals(((BaseActorState<?>) valueSecond).getName()));
				else if (operator.equals("instanceof")) {
					try {
						result = coreRebecaTypeSystem.
								getType(((BaseActorState<?>) valueFirst).getTypeName()).
								canTypeDownCastTo(coreRebecaTypeSystem.getType((String)valueSecond));
					} catch (CodeCompilationException e) {
						result = false;
						e.printStackTrace();
					}
				}
				else
					throw new RebecaRuntimeInterpreterException(
							"this case should not happen!! should've been reported as an error by compiler!");
			} else
				result = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
		}
		baseActorState.setVariableValue(((Variable) aib.getLeftVarName()).getVarName(), result);
		baseActorState.increasePC();
	}
}
