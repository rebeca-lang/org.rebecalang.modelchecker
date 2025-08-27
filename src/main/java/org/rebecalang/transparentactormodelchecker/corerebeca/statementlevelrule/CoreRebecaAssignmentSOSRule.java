package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.TauAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaAssignmentSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	public static Object getValue(Object reference, CoreRebecaActorState actorState) {
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
		Action resultAction = TauAction.TAU;
		CoreRebecaActorState actorState = source.getFirst();

		AssignmentInstructionBean aib = (AssignmentInstructionBean) source.getSecond();
		Object valueFirst = getValue(aib.getFirstOperand(), actorState);
		Object rightSideResult = valueFirst;

		String operator = aib.getOperator();
		if (operator != null) {
			Object valueSecond = getValue(aib.getSecondOperand(), actorState);
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

		actorState.setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);
		actorState.movePCtoTheNextInstruction();

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
