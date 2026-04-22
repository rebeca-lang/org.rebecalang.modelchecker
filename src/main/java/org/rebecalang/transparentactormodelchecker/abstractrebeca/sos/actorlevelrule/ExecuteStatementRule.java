package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ReturnInstructionBean;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.MethodLookup;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.AssignmentRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ConditionalRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.EndMSGSrvRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.EndMethodCallRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.MethodCallRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.PopRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.PushRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.RebecInstantiationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ReturnRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.SendMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.VariableDeclarationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExecuteStatementRule extends AbstractSOSRule<AbstractActorState> {

	@Autowired
	AssignmentRule assignmentSOSRule;

	@Autowired
	ConditionalRule conditionalSOSRule;
	
	@Autowired
	VariableDeclarationRule variableDeclarationSOSRule;

	@Autowired
	PopRule popSOSRule;

	@Autowired
	PushRule pushSOSRule;

	@Autowired
	EndMSGSrvRule endMSGSrvSOSRule;
	
	@Autowired
	MethodCallRule methodCallSOSRule;

	@Autowired
	EndMethodCallRule endMethodCallSOSRule;
		
	SendMessageRule sendMessageSOSRule;

	@Autowired
	RebecInstantiationRule rebecInstantiationSOSRule;

	@Autowired
	ReturnRule returnRule;

	Logger logger;
	
	public ExecuteStatementRule() {
		logger = LogManager.getRootLogger();
	}
	
	public void setSendMessageRule(SendMessageRule sendMessageSOSRule) {
		this.sendMessageSOSRule = sendMessageSOSRule;
	}
	
	protected AbstractSOSRule<AbstractActorState> getRule(InstructionBean instruction) throws RuleIsDisabledException {
		AbstractSOSRule<AbstractActorState> rule;
		
		if(instruction instanceof DeclarationInstructionBean)
			rule = variableDeclarationSOSRule;
		else if (instruction instanceof PushARInstructionBean)
			rule = pushSOSRule;
		else if (instruction instanceof PopARInstructionBean)
			rule = popSOSRule;
		else if (instruction instanceof AssignmentInstructionBean)
			rule = assignmentSOSRule;
		else if (instruction instanceof JumpIfNotInstructionBean)
			rule = conditionalSOSRule;
		else if (instruction instanceof MsgsrvCallInstructionBean)
			rule = sendMessageSOSRule;
		else if (instruction instanceof EndMsgSrvInstructionBean)
			rule = endMSGSrvSOSRule;
		else if (instruction instanceof MethodCallInstructionBean)
			rule = methodCallSOSRule;
		else if (instruction instanceof EndMethodInstructionBean)
			rule = endMethodCallSOSRule;
		else if (instruction instanceof RebecInstantiationInstructionBean)
			rule = rebecInstantiationSOSRule;
		else if (instruction instanceof ReturnInstructionBean)
			rule = returnRule;
		else
			throw new RebecaRuntimeInterpreterException("Unknown rule for the statement " + instruction);
		return rule;
	}
	
	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) throws RuleIsDisabledException {
		Transition<AbstractActorState> destinations = null;
		boolean ignorableInstructionIsExecuted = false;
		do {
			InstructionBean instruction = state.getEnabledInstruction();
			if(instruction == null)
				throw new RuleIsDisabledException();

			ignorableInstructionIsExecuted = 
					instruction instanceof DeclarationInstructionBean ||
					instruction instanceof PushARInstructionBean ||
					instruction instanceof PopARInstructionBean;
			AbstractSOSRule<AbstractActorState> rule = getRule(instruction);

//			logger.debug("Executing instruction " + instruction);
			destinations = rule.applyRule(state, state, instruction);
		} while(ignorableInstructionIsExecuted);
		return destinations;
	}

	public boolean isEnabled(AbstractActorState source) {
		return source.hasVariableInScope(AbstractActorState.PC);
	}

	public void setMethodLookup(MethodLookup methodLookup) {
		methodCallSOSRule.setMethodLookup(methodLookup);
		
	}

}
