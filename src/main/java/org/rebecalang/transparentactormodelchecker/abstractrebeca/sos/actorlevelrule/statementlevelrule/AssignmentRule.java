package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class AssignmentRule extends AbstractSOSRule<AbstractActorState> {

	public static Object getValue(Object reference, AbstractActorState actorState) {
		if (reference instanceof Variable) {
//			if(((Variable) reference).getIndeces().size() != 0) {
//				Object j = actorState.getVariableValue(((Variable) reference));
//				System.out.println(j);
//			}
			return actorState.getVariableValue(((Variable) reference));
//		} else if (reference instanceof RebecInstantiationInstructionBean) {
//			return CoreRebecaActorState.createTempCoreRebecaActorState();
		}
		return reference;
	}

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {
		Action resultAction = TauAction.TAU;


		AssignmentInstructionBean aib = (AssignmentInstructionBean) additional[0];
		Object valueFirst = getValue(aib.getFirstOperand(), state);
		Object rightSideResult = valueFirst;

//		System.out.println(valueFirst + " ... " + rightSideResult);
		if(rightSideResult instanceof NonDetValue) {
			Transition<AbstractActorState> result = new Transition<AbstractActorState>();
			NonDetValue ndv = (NonDetValue) rightSideResult;
			Variable leftVarName = (Variable) aib.getLeftVarName();

			Object[] values = ndv.getNonDetValues().toArray();
			for(int cnt = 0; cnt < values.length; cnt++)
				values[cnt]=getValue(values[cnt], state);
			for(int cnt = 0; cnt < values.length; cnt++) {
				state.setVariableValue(
						leftVarName, 
						getValue(values[cnt], state));
				result.addDestination(resultAction, state);
				if(cnt != values.length - 1) {
					CloningRepository.resetRepository();
					state.movePCtoTheNextInstruction();
					state = base.clone();
				}
			}
			return result;
		} else {
			String operator = aib.getOperator();
			if (operator != null) {
				Object valueSecond = getValue(aib.getSecondOperand(), state);
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
				} else {
					rightSideResult = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
//					if(rightSideResult == AbstractSemanticCheck.NO_VALUE)
//						System.out.println(valueFirst + operator + valueSecond + "  ...   " + rightSideResult);
				}
			}
			state.setVariableValue((Variable) aib.getLeftVarName(), rightSideResult);			
			state.movePCtoTheNextInstruction();
			return Transition.createDeterministicTransition(resultAction, state);
		}
	}
}
