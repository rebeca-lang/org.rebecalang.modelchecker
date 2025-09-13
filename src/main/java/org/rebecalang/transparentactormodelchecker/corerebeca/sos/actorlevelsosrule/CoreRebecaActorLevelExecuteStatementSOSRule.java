package org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.AssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.ConditionalSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.EndMSGSrvSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.EndMethodCallSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.MethodCallSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.PopSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.PushSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.SendMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.VariableDeclarationSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.AbstractTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule.CoreRebecaRebecInstantiationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelExecuteStatementSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Autowired
	AssignmentSOSRule assignmentSOSRule;

	@Autowired
	ConditionalSOSRule conditionalSOSRule;
	
	@Autowired
	SendMessageSOSRule sendMessageSOSRule;

	@Autowired
	VariableDeclarationSOSRule variableDeclarationSOSRule;

	@Autowired
	PopSOSRule popSOSRule;

	@Autowired
	PushSOSRule pushSOSRule;

	@Autowired
	EndMSGSrvSOSRule endMSGSrvSOSRule;

	@Autowired
	CoreRebecaRebecInstantiationSOSRule rebecInstantiationSOSRule;
	
	@Autowired
	MethodCallSOSRule methodCallSOSRule;

	@Autowired
	EndMethodCallSOSRule endMethodCallSOSRule;

	List<Pair<? extends Action, CoreRebecaActorState>> decorateStatementExecutionResult(
			Pair<? extends Action, Pair<CoreRebecaActorState, InstructionBean>> statementExecutionResult) {
		return Arrays.asList(
				new Pair<Action, CoreRebecaActorState>(TauAction.TAU, statementExecutionResult.getSecond().getFirst()));
	}

	DeterministicTransition<CoreRebecaActorState> convertStatementResultToActorResult(
			DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result) {
		CoreRebecaActorState state = (CoreRebecaActorState) result.getDestination().getFirst();
		return new DeterministicTransition<CoreRebecaActorState>(result.getAction(),
				state);
	}

	@Override
	public AbstractTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState base, CoreRebecaActorState state) throws RuleIsDisabledException {
		AbstractTransition<CoreRebecaActorState> destinations = null;
		do {
			InstructionBean instruction = state.getEnabledInstruction();
			if(instruction == null)
				throw new RuleIsDisabledException();
			String label = state.getPC().toString();

			Pair<CoreRebecaActorState, InstructionBean> pair = new Pair<>(state, instruction);
			if (instruction instanceof DeclarationInstructionBean) {
				variableDeclarationSOSRule.applyRule(pair, pair);
			} else if (instruction instanceof PushARInstructionBean) {
				pushSOSRule.applyRule(pair, pair);
			} else if (instruction instanceof PopARInstructionBean) {
				popSOSRule.applyRule(pair, pair);
			} else if (instruction instanceof AssignmentInstructionBean) {
				AbstractTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = assignmentSOSRule
						.applyRule(pair, pair);
				if(executionResult instanceof DeterministicTransition) {
					((DeterministicTransition<?>)executionResult).setAction(new TauAction(label));
					destinations = convertStatementResultToActorResult(
							(DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>) executionResult);
				} else {
					NondeterministicTransition<AbstractActorState> nondetTrans = new NondeterministicTransition<AbstractActorState>();
					for (Pair<? extends Action, Pair<? extends AbstractActorState, InstructionBean>> choice : ((NondeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>)executionResult).getDestinations()) {
						nondetTrans.addDestination(new TauAction(label), choice.getSecond().getFirst());
					}
				}
			} else if (instruction instanceof JumpIfNotInstructionBean) {
					DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = conditionalSOSRule
							.applyRule(pair, pair);
					executionResult.setAction(new TauAction(label));
					destinations = convertStatementResultToActorResult(executionResult);
			} else if (instruction instanceof MsgsrvCallInstructionBean) {
				DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = sendMessageSOSRule
						.applyRule(pair, pair);
				destinations = convertStatementResultToActorResult(executionResult);
			} else if (instruction instanceof EndMsgSrvInstructionBean) {
				DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = endMSGSrvSOSRule
						.applyRule(pair, pair);
				executionResult.setAction(new TauAction(label));
				destinations = convertStatementResultToActorResult(executionResult);
			} else if (instruction instanceof RebecInstantiationInstructionBean) {
				DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = rebecInstantiationSOSRule
						.applyRule(pair, pair);
				destinations = convertStatementResultToActorResult(executionResult);
			} else if (instruction instanceof MethodCallInstructionBean) {
				DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = methodCallSOSRule
						.applyRule(pair, pair);
				destinations = convertStatementResultToActorResult(executionResult);
			} else if (instruction instanceof EndMethodInstructionBean) {
				DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> executionResult = endMethodCallSOSRule
						.applyRule(pair, pair);
				executionResult.setAction(new TauAction(label));
				destinations = convertStatementResultToActorResult(executionResult);
			} else {
				throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
			}
		} while(destinations == null);
		return destinations;
	}

	@Override
	public boolean isEnabled(CoreRebecaActorState source) {
		return !source.hasVariableInScope(CoreRebecaActorState.PC) && !source.messageQueueIsEmpty();
	}

	@Override
	public AbstractTransition<CoreRebecaActorState> applyRule(
			CoreRebecaActorState base, Action action,
			CoreRebecaActorState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Actor Level execute statement rule does not accept input action");
	}

	public void setMethodLookupTable(HashMap<String, String> methodLookupTable) {
		methodCallSOSRule.setMethodLookupTable(methodLookupTable);
		
	}

}
