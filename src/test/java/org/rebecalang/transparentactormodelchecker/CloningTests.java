package org.rebecalang.transparentactormodelchecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class CloningTests {
    public final static int ACTOR_1_ID = 0;

    @Test
    public void cloneSystemState() {
		CoreRebecaSystemState coreRebecaSystemState = new CoreRebecaSystemState();
    	coreRebecaSystemState.setEnvironment(new Environment());
    	coreRebecaSystemState.setNetworkState(new CoreRebecaNetworkState());
    	coreRebecaSystemState.setActorState(ACTOR_1_ID, new CoreRebecaActorState(ACTOR_1_ID));

    	CoreRebecaMessageState message1 = new CoreRebecaMessageState("m1", new HashMap<String, Object>());
    	CoreRebecaActorState actor1 = coreRebecaSystemState.getActorState(ACTOR_1_ID);
    	message1.setReceiver(actor1);
    	message1.setSender(actor1);
    	actor1.receiveMessage(message1);

		PushARInstructionBean puib = new PushARInstructionBean();
		RILModel rilModel = new RILModel();
		rilModel.addMethod("m1", 
				new ArrayList<InstructionBean>(Arrays.asList(puib)));
    	
    	actor1.setRILModel(rilModel);
    	
    	RebecaStateSerializationUtils.clone(coreRebecaSystemState);
    	
	}
//    @Test
//    public void cloneSystemState() {
//		CoreRebecaSystemState coreRebecaSystemState = new CoreRebecaSystemState();
//    	coreRebecaSystemState.setEnvironment(new Environment());
//    	coreRebecaSystemState.setNetworkState(new CoreRebecaNetworkState());
//    	coreRebecaSystemState.setActorState(ACTOR_1_ID, new CoreRebecaActorState(ACTOR_1_ID));
//    	coreRebecaSystemState.setActorState(ACTOR_2_ID, new CoreRebecaActorState(ACTOR_2_ID));
//
//    	CoreRebecaMessageState message1 = new CoreRebecaMessageState("m1", new HashMap<String, Object>());
//    	CoreRebecaActorState actor1 = coreRebecaSystemState.getActorState(ACTOR_1_ID);
//    	message1.setReceiver(actor1);
//    	message1.setSender(actor1);
//    	actor1.receiveMessage(message1);
//
//		PushARInstructionBean puib = new PushARInstructionBean();
//    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
//		Variable v = new Variable("var1");
//    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
//    	PopARInstructionBean poib = new PopARInstructionBean();
//    	EndMsgSrvInstructionBean emib = new EndMsgSrvInstructionBean();
//		RILModel rilModel = new RILModel();
//		rilModel.addMethod("m1", 
//				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));
//    	
//    	CoreRebecaMessageState message2 = new CoreRebecaMessageState("m2", new HashMap<String, Object>());
//    	CoreRebecaActorState actor2 = coreRebecaSystemState.getActorState(ACTOR_2_ID);
//    	message2.setReceiver(actor2);
//		actor2.receiveMessage(message2);
//		message2.setSender(actor2);
//    	aib = new AssignmentInstructionBean(v, 5, null, null);
//    	rilModel.addMethod("m2", 
//				new ArrayList<InstructionBean>(Arrays.asList(puib, dib, aib, poib, emib)));
//
//    	actor1.setRILModel(rilModel);
//    	actor2.setRILModel(rilModel);
//    	
//    	CoreRebecaSystemState clone = 
//    			RebecaStateSerializationUtils.clone(coreRebecaSystemState);
//    	
//	}
}
