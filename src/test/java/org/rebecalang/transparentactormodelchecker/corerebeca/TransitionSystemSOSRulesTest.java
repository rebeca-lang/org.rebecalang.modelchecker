package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
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
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.Environment;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.CoreRebecaSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class TransitionSystemSOSRulesTest {
	
	
	@Autowired
	CoreRebecaSOSRule sosRule;
	
    CoreRebecaSystemState coreRebecaSystemState;
    
    public final static int ACTOR_1_ID = 0;
    public final static int ACTOR_2_ID = 1;
    
    @BeforeEach
    public void setup() {
    	coreRebecaSystemState = new CoreRebecaSystemState();
    	coreRebecaSystemState.setEnvironment(new Environment());
    	coreRebecaSystemState.setNetworkState(new CoreRebecaNetworkState());
    	coreRebecaSystemState.setActorState(ACTOR_1_ID, new CoreRebecaActorState(ACTOR_1_ID));
    	coreRebecaSystemState.setActorState(ACTOR_2_ID, new CoreRebecaActorState(ACTOR_2_ID));
    }

    @Test
    public void GIVEN_TwoActorsHaveMesssages_WHEN_StartExecution_THEN_TwoTargetStatesHaveToBeGenerated() throws RuleIsDisabledException {
    	
    	CoreRebecaMessageState message1 = new CoreRebecaMessageState("m1", new HashMap<String, Object>());
    	CoreRebecaActorState actor1 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	message1.setReceiver(actor1);
    	actor1.receiveMessage(message1);

		PushARInstructionBean puib = new PushARInstructionBean();
    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
    	PopARInstructionBean poib = new PopARInstructionBean();
    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));
    	
    	CoreRebecaMessageState message2 = new CoreRebecaMessageState("m2", new HashMap<String, Object>());
    	CoreRebecaActorState actor2 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_2_ID);
    	message2.setReceiver(actor2);
		actor2.receiveMessage(message2);
    	aib = new AssignmentInstructionBean(v, 5, null, null);
    	rilModel.addMethod("m2", 
				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));

    	actor1.setRILModel(rilModel);
    	actor2.setRILModel(rilModel);
    	
    	NondeterministicTransition<CoreRebecaSystemState> transition = 
    			(NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
    	transition = (NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(2, transition.getDestinations().size());
    }
   
    @Test
    public void GIVEN_TwoActors_WHEN_SendMessageStatementIsExecuted_THEN_SentMessageHasToBedeliveredToTheTarget() throws RuleIsDisabledException {
    	CoreRebecaActorState actor1 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	CoreRebecaActorState actor2 = (CoreRebecaActorState) coreRebecaSystemState.getActorState(ACTOR_2_ID);
    	
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
    	actor2.setRILModel(rilModel);
    	NondeterministicTransition<CoreRebecaSystemState> transition = 
    			(NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
    	transition = (NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(2, transition.getDestinations().size());

		transition = (NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(1, transition.getDestinations().size());

		transition = (NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(coreRebecaSystemState, coreRebecaSystemState);
		Assertions.assertEquals(1, transition.getDestinations().size());

//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(coreRebecaSystemState));
    }

}