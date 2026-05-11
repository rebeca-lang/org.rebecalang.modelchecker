package org.rebecalang.transparentactormodelchecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class HashCodeAndEqualsTest {
	
	CoreRebecaActorState coreRebecaActorState1;
	CoreRebecaActorState coreRebecaActorState2;
	
    @BeforeEach
    public void setup() {
    	coreRebecaActorState1 = new CoreRebecaActorState(1);
    	coreRebecaActorState2 = new CoreRebecaActorState(1);
    }

    @Test
    public void GIVEN_TwoActorStates_WHEN_TheirStateVariablesAreNotTheSame_THEN_TheyAreNotEqual() {
		
    	coreRebecaActorState1.addVariableToScope("var1", 1);
    	coreRebecaActorState1.addVariableToScope("var2", coreRebecaActorState2);
    	coreRebecaActorState1.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	coreRebecaActorState2.setId(1);
    	coreRebecaActorState2.addVariableToScope("var1", 1);
    	coreRebecaActorState2.addVariableToScope("var2", coreRebecaActorState1);
    	coreRebecaActorState2.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	
    	assertEquals(coreRebecaActorState1.hashCode(),coreRebecaActorState2.hashCode());
    	assertTrue(coreRebecaActorState1.deepEquals(coreRebecaActorState2));
    	assertTrue(coreRebecaActorState2.deepEquals(coreRebecaActorState1));
	}
}
