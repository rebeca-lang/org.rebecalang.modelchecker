package org.rebecalang.transparentactormodelchecker.corerebeca;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class ActorScopeTest {

	private ActorScope actorScope;

	@BeforeEach
	public void setup() {
		actorScope = new ActorScope();
	}
	
	@Test
	public void retrieveVariable() {
		int[][] value = new int[2][3];
		actorScope.addVariableToScope("v", value);

		Variable v = new Variable("v");
		v.addIndex(1);
		v.addIndex(2);
		actorScope.setVariableValue(v, 10);
		
		Object retrievedValue = actorScope.getVariableValue(v);
		Assertions.assertEquals(10, retrievedValue);
		
		v.getIndeces().set(0, 0);
		retrievedValue = actorScope.getVariableValue(v);
		Assertions.assertEquals(0, retrievedValue);

	}
	
	@Test
	public void retrieveVariableFromEnviroment() {
		ActivationRecord environment = new ActivationRecord();
		environment.setVariableValue("v", 5);
		actorScope.setEnvironment(environment);
		
		Variable v = new Variable("v");
		Object retrievedValue = actorScope.getVariableValue(v);
		Assertions.assertEquals(5, retrievedValue);
	}
}
