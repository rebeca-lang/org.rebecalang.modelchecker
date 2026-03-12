package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelNetworkDeliveryRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelrule.CoreRebecaCompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class NetworkAndActorSOSRulesTest {
	
	
	@Autowired
	@Qualifier("CORE_REBECA")
	CoreRebecaCompositionLevelTakeMessageRule takeMessageRule;
	
	@Autowired
	@Qualifier("CORE_REBECA")
	CompositionLevelExecuteStatementRule executeStatementRule;
	
	@Autowired
	CompositionLevelNetworkDeliveryRule networkDeliveryRule;
	
    CoreRebecaSystemState coreRebecaSystemState;
    
    public final static int ACTOR_1_ID = 0;
    public final static int ACTOR_2_ID = 1;
    
    @BeforeEach
    public void setup() {
    	coreRebecaSystemState = new CoreRebecaSystemState();
    	coreRebecaSystemState.setEnvironment(new ActivationRecord());
    	coreRebecaSystemState.setNetworkState(new CoreRebecaNetworkState());
    	CoreRebecaActorState actor1 = new CoreRebecaActorState(ACTOR_1_ID);
    	actor1.addVariableToScope("self", actor1);
    	
		coreRebecaSystemState.setActorState(ACTOR_1_ID, actor1);
    	CoreRebecaActorState actor2 = new CoreRebecaActorState(ACTOR_2_ID);
    	actor2.addVariableToScope("self", actor2);
		coreRebecaSystemState.setActorState(ACTOR_2_ID, actor2);
    }

    @Test
    public void GIVEN_TwoActorsHaveMesssages_WHEN_StartExecution_THEN_TwoTargetStatesHaveToBeGenerated() throws RuleIsDisabledException {
    	
    	CoreRebecaMessageState message1 = new CoreRebecaMessageState("m1", new HashMap<String, Object>());
    	CoreRebecaActorState actor1 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	message1.setReceiverId(actor1.getId());
    	actor1.receiveMessage(message1);

		PushARInstructionBean puib = new PushARInstructionBean();
    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1",
    			CoreRebecaTypeSystem.INT_TYPE);
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
    	PopARInstructionBean poib = new PopARInstructionBean();
    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));
    	
    	CoreRebecaMessageState message2 = new CoreRebecaMessageState("m2", new HashMap<String, Object>());
    	CoreRebecaActorState actor2 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_2_ID);
    	message2.setReceiverId(actor2.getId());
		actor2.receiveMessage(message2);
    	aib = new AssignmentInstructionBean(v, 5, null, null);
    	rilModel.addMethod("m2", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));

    	actor1.setRILModel(rilModel);
    	actor2.setRILModel(rilModel);
    	
    	Transition<AbstractSystemState> transition = 
    			takeMessageRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
    	transition = takeMessageRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
    	transition = executeStatementRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(2, transition.size());
    }
   
    @Test
    public void GIVEN_TwoActors_WHEN_SendMessageStatementIsExecuted_THEN_SentMessageHasToBedeliveredToTheTarget() throws RuleIsDisabledException {
    	CoreRebecaActorState actor1 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	CoreRebecaActorState actor2 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_2_ID);
    	
    	actor1.newCallPushToScope(null);

    	actor1.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("m1", 0));
    	actor1.addVariableToScope("actor2", actor2);
		PushARInstructionBean puib = new PushARInstructionBean();
		Variable reciever = new Variable("actor2");
    	MsgsrvCallInstructionBean mcib = new MsgsrvCallInstructionBean(reciever, "m2");
    	PopARInstructionBean poib = new PopARInstructionBean();
    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, mcib, poib, emib)));
		actor1.setRILModel(rilModel);

		reciever = new Variable("self");
    	mcib = new MsgsrvCallInstructionBean(reciever, "m2");
		rilModel = new RILModel();
		rilModel.addMethod("m2", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, mcib, poib, emib)));
		actor2.setRILModel(rilModel);
    	Transition<AbstractSystemState> transition = 
    			executeStatementRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
    	transition = networkDeliveryRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(1, transition.size());

		transition = takeMessageRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(1, transition.size());

		transition = executeStatementRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(2, transition.size());

    }

//    @Configuration
//    @ComponentScan(basePackages = { 
//    		"org.rebecalang.transparentactormodelchecker.abstractrebeca", 
//    		"org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule", 
//    		"org.rebecalang.transparentactormodelchecker.timedrebeca.sos.statementlevelrule"
//    		})
//    public static class Config {
//    	@Autowired
//    	ApplicationContext appContext;
//
//    	@Bean
//    	@Qualifier("CORE_REBECA")
//    	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    	public CompositionLevelExecuteStatementRule getCoreRebecaCompositionExecuteStatementSOSRule() {
//    		ExecuteStatementRule executeStatement = appContext.getBean(ExecuteStatementRule.class);
//    		executeStatement.setSendMessageRule(appContext.getBean(SendMessageRule.class));
//    		CompositionLevelExecuteStatementRule compositionExecuteStatement = 
//    				new CompositionLevelExecuteStatementRule();
//    		compositionExecuteStatement.setExecuteStatementSOSRule(executeStatement);
//    		return compositionExecuteStatement;
//    	}
//
//    	@Bean
//    	@Qualifier("TIMED_REBECA")
//    	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    	public CompositionLevelExecuteStatementRule getTimedRebecaCompositionExecuteStatementSOSRule() {
//    		ExecuteStatementRule executeStatement = appContext.getBean(ExecuteStatementRule.class);
//    		executeStatement.setSendMessageRule(appContext.getBean(TimedRebecaSendMessageSOSRule.class));
//    		CompositionLevelExecuteStatementRule compositionExecuteStatement = 
//    				new CompositionLevelExecuteStatementRule();
//    		compositionExecuteStatement.setExecuteStatementSOSRule(executeStatement);
//    		return compositionExecuteStatement;
//    	}
//    }
}