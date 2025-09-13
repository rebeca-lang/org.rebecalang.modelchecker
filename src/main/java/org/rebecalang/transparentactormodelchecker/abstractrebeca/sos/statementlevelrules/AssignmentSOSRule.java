package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.AbstractTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.springframework.stereotype.Component;

@Component
public class AssignmentSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>> {

	public static Object getValue(Object reference, AbstractActorState actorState) {
		if (reference instanceof Variable) {
			String varName = ((Variable) reference).getVarName();
			return actorState.getVariableValue(varName);
//		} else if (reference instanceof RebecInstantiationInstructionBean) {
//			return CoreRebecaActorState.createTempCoreRebecaActorState();
		}
		return reference;
	}

	@Override
	public AbstractTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {
		Action resultAction = TauAction.TAU;

		AbstractActorState actorState = state.getFirst();

		AssignmentInstructionBean aib = (AssignmentInstructionBean) state.getSecond();
		Object valueFirst = getValue(aib.getFirstOperand(), actorState);
		Object rightSideResult = valueFirst;

		if(rightSideResult instanceof NonDetValue) {
			NondeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = new NondeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>();
			NonDetValue ndv = (NonDetValue) rightSideResult;
			Variable leftVarName = (Variable) aib.getLeftVarName();

			Object[] values = ndv.getNonDetValues().toArray();
			for(int cnt = 0; cnt < values.length; cnt++)
				values[cnt]=getValue(values[cnt], actorState);
			for(int cnt = 0; cnt < values.length; cnt++) {
				actorState.setVariableValue(
						leftVarName, 
						getValue(values[cnt], actorState));
				result.addDestination(resultAction, state);
				if(cnt != values.length - 1) {
					CloningRepository.resetRepository();
					state = new Pair<AbstractActorState, InstructionBean>(base.getFirst().clone(), aib);
					actorState.movePCtoTheNextInstruction();
					actorState = state.getFirst();
				}
			}
			return result;
		} else {
			String operator = aib.getOperator();
			if (operator != null) {
				Object valueSecond = getValue(aib.getSecondOperand(), actorState);
				if (valueFirst instanceof AbstractActorState) {
					if (operator.equals("=="))
						rightSideResult = ((AbstractActorState) valueFirst)
								.getId() == ((AbstractActorState) valueSecond).getId();
					else if (operator.equals("!="))
						rightSideResult = ((AbstractActorState) valueFirst)
								.getId() != ((AbstractActorState) valueSecond).getId();
//						else if (operator.equals("instanceof")) {
//							try {
//								result = coreRebecaTypeSystem.
//										getType(((BaseActorState<?>) valueFirst).getTypeName()).
//										canTypeDownCastTo(coreRebecaTypeSystem.getType((String)valueSecond));
//							} catch (CodeCompilationException e) {
//								result = false;
//								e.printStackTrace();
//							}
//						}
					else
						throw new RebecaRuntimeInterpreterException(
								"this case should have been reported as an error by the compiler.");
				} else
					rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
			}
			actorState.setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);			
			actorState.movePCtoTheNextInstruction();
			DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = new DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>();
			result.setDestination(state);
			result.setAction(resultAction);
			return result;
		}
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Action action,
			Pair<? extends AbstractActorState, InstructionBean> state) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}
