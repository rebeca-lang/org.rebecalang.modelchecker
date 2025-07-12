package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaAssignmentSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	private Object getValue(Object reference, CoreRebecaActorState actorState) {
		if (reference instanceof Variable) {
			String varName = ((Variable) reference).getVarName();
			return actorState.getVariableValue(varName);
//		} else if (reference instanceof RebecInstantiationInstructionBean) {
//			return CoreRebecaActorState.createTempCoreRebecaActorState();
		}
		return reference;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(
			Pair<CoreRebecaActorState, InstructionBean> source) {
		Action resultAction = Action.TAU;
		AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
		Object valueFirst = getValue(aib.getFirstOperand(), source.getFirst());
		Object rightSideResult = valueFirst;

		Object valueSecond = getValue(aib.getSecondOperand(), source.getFirst());
		String operator = aib.getOperator();
		if (operator != null) {
			if (valueFirst instanceof CoreRebecaActorState) {
				if (operator.equals("=="))
					rightSideResult = ((CoreRebecaActorState) valueFirst)
							.getId() == ((CoreRebecaActorState) valueSecond).getId();
				else if (operator.equals("!="))
					rightSideResult = ((CoreRebecaActorState) valueFirst)
							.getId() != ((CoreRebecaActorState) valueSecond).getId();
//					else if (operator.equals("instanceof")) {
//						try {
//							result = coreRebecaTypeSystem.
//									getType(((BaseActorState<?>) valueFirst).getTypeName()).
//									canTypeDownCastTo(coreRebecaTypeSystem.getType((String)valueSecond));
//						} catch (CodeCompilationException e) {
//							result = false;
//							e.printStackTrace();
//						}
//					}
				else
					throw new RebecaRuntimeInterpreterException(
							"this case should have been reported as an error by the compiler.");
			} else
				rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
		}

		source.getFirst().setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
		source.getFirst().movePCtoTheNextInstruction();

		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>>();
		result.setDestination(source);
		result.setAction(resultAction);
		return result;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> applyRule(Action action,
			Pair<CoreRebecaActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}
