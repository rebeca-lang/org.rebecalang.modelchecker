package org.rebecalang.transparentactormodelchecker.corerebeca;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorsContainer;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
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

	// Name lookup walks down to index 0, which is null until an environment is
	// attached, so tests that look up an absent name must attach one first.
	private ActivationRecord attachEnvironment() {
		ActivationRecord environment = new ActivationRecord();
		environment.setVariableValue(ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME,
				new ActorsContainer());
		actorScope.setEnvironment(environment);
		return environment;
	}

	private CoreRebecaActorState registerActor(int id) {
		ActorsContainer container = (ActorsContainer) actorScope.getEnvironment()
				.getVariableValue(ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME);
		CoreRebecaActorState actor = new CoreRebecaActorState(id);
		container.setActor(id, actor);
		return actor;
	}

	@Test
	public void GIVEN_AVariableIsInScope_WHEN_HasVariableInScopeIsCalled_THEN_ItIsFound() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		Assertions.assertTrue(actorScope.hasVariableInScope("v"));
	}

	@Test
	public void GIVEN_NoSuchVariable_WHEN_HasVariableInScopeIsCalled_THEN_ItIsNotFound() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		Assertions.assertFalse(actorScope.hasVariableInScope("absent"));
	}

	@Test
	public void GIVEN_NoSuchVariable_WHEN_ValueIsRead_THEN_InterpreterExceptionIsThrown() {
		attachEnvironment();

		Assertions.assertThrows(RebecaRuntimeInterpreterException.class,
				() -> actorScope.getVariableValue(new Variable("absent")));
	}

	@Test
	public void GIVEN_AnInnerFrameIsPushed_WHEN_OuterVariableIsRead_THEN_ItIsStillVisible() {
		attachEnvironment();
		actorScope.addVariableToScope("outer", 1);

		actorScope.pushToScope();

		Assertions.assertEquals(1, actorScope.getVariableValue(new Variable("outer")));
	}

	@Test
	public void GIVEN_AVariableDeclaredInAnInnerFrame_WHEN_TheFrameIsPopped_THEN_ItGoesOutOfScope() {
		attachEnvironment();
		actorScope.addVariableToScope("outer", 1);

		actorScope.pushToScope();
		actorScope.addVariableToScope("inner", 2);
		Assertions.assertTrue(actorScope.hasVariableInScope("inner"));

		actorScope.popFromScope();

		Assertions.assertFalse(actorScope.hasVariableInScope("inner"));
		Assertions.assertTrue(actorScope.hasVariableInScope("outer"));
	}

	@Test
	public void GIVEN_AnInnerFrameShadowsAName_WHEN_TheFrameIsPopped_THEN_TheOuterValueReturns() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		actorScope.pushToScope();
		actorScope.addVariableToScope("v", 2);
		Assertions.assertEquals(2, actorScope.getVariableValue(new Variable("v")));

		actorScope.popFromScope();

		Assertions.assertEquals(1, actorScope.getVariableValue(new Variable("v")));
	}

	@Test
	public void GIVEN_AMethodCallFrame_WHEN_ANameIsResolved_THEN_CallerLocalsAreNotVisible() {
		attachEnvironment();
		actorScope.addVariableToScope("actorField", 1);

		actorScope.pushToScope();
		actorScope.addVariableToScope("callerLocal", 2);

		actorScope.newCallPushToScope(new Variable("actorField"));

		Assertions.assertTrue(actorScope.hasVariableInScope("actorField"));
		Assertions.assertFalse(actorScope.hasVariableInScope("callerLocal"));
	}

	@Test
	public void GIVEN_AMethodCallFrame_WHEN_PopToReturnIsCalled_THEN_ValueLandsInTheTargetVariable() {
		attachEnvironment();
		actorScope.addVariableToScope("dest", 0);

		actorScope.newCallPushToScope(new Variable("dest"));
		actorScope.popToReturn(42);

		Assertions.assertEquals(42, actorScope.getVariableValue(new Variable("dest")));
	}

	@Test
	public void GIVEN_NestedFramesInsideACall_WHEN_PopToReturnIsCalled_THEN_AllOfThemAreDiscarded() {
		attachEnvironment();
		actorScope.addVariableToScope("dest", 0);

		actorScope.newCallPushToScope(new Variable("dest"));
		actorScope.pushToScope();
		actorScope.addVariableToScope("calleeLocal", 7);

		actorScope.popToReturn(9);

		Assertions.assertEquals(9, actorScope.getVariableValue(new Variable("dest")));
		Assertions.assertFalse(actorScope.hasVariableInScope("calleeLocal"));
	}

	@Test
	public void GIVEN_AnActorIsStoredInScope_WHEN_ItIsReadBack_THEN_TheSameActorStateIsReturned() {
		attachEnvironment();
		CoreRebecaActorState peer = registerActor(7);

		actorScope.addVariableToScope("peer", peer);

		Assertions.assertSame(peer, actorScope.getVariableValue(new Variable("peer")));
	}

	@Test
	public void GIVEN_AnActorIsStoredInScope_WHEN_ItsOwnFieldIsRead_THEN_TheNestedValueIsReturned() {
		attachEnvironment();
		CoreRebecaActorState peer = registerActor(7);
		peer.addVariableToScope("x", 5);
		actorScope.addVariableToScope("peer", peer);

		Variable peerX = new Variable(new Variable("peer"), "x");

		Assertions.assertEquals(5, actorScope.getVariableValue(peerX));
	}

	@Test
	public void GIVEN_AnActorIsStoredInScope_WHEN_ItsOwnFieldIsAssigned_THEN_TheOtherActorSeesIt() {
		attachEnvironment();
		CoreRebecaActorState peer = registerActor(7);
		peer.addVariableToScope("x", 5);
		actorScope.addVariableToScope("peer", peer);

		actorScope.setVariableValue(new Variable(new Variable("peer"), "x"), 9);

		Assertions.assertEquals(9, peer.getVariableValue(new Variable("x")));
	}

	@Test
	public void GIVEN_APeerHoldsAnArray_WHEN_AnElementIsReadThroughIt_THEN_TheElementValueIsReturned() {
		attachEnvironment();
		CoreRebecaActorState peer = registerActor(7);
		peer.addVariableToScope("arr", new int[] {4, 5, 6});
		actorScope.addVariableToScope("peer", peer);

		Variable peerArrayElement = new Variable(new Variable("peer"), "arr");
		peerArrayElement.addIndex(1);

		Assertions.assertEquals(5, actorScope.getVariableValue(peerArrayElement));
	}

	@Test
	public void GIVEN_APeerHoldsAnArray_WHEN_AnElementIsAssignedThroughIt_THEN_ThePeerSeesTheNewElement() {
		attachEnvironment();
		CoreRebecaActorState peer = registerActor(7);
		peer.addVariableToScope("arr", new int[] {4, 5, 6});
		actorScope.addVariableToScope("peer", peer);

		Variable peerArrayElement = new Variable(new Variable("peer"), "arr");
		peerArrayElement.addIndex(1);
		actorScope.setVariableValue(peerArrayElement, 9);

		Variable ownArrayElement = new Variable("arr");
		ownArrayElement.addIndex(1);
		Assertions.assertEquals(9, peer.getVariableValue(ownArrayElement));

		Variable untouched = new Variable("arr");
		untouched.addIndex(0);
		Assertions.assertEquals(4, peer.getVariableValue(untouched));
	}

	@Test
	public void GIVEN_AClonedScope_WHEN_TheCloneIsModified_THEN_TheOriginalIsUnchanged() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		ActorScope clone = actorScope.clone();
		clone.setVariableValue(new Variable("v"), 2);

		Assertions.assertEquals(2, clone.getVariableValue(new Variable("v")));
		Assertions.assertEquals(1, actorScope.getVariableValue(new Variable("v")));
	}

	@Test
	public void GIVEN_TwoScopesWithEqualLocals_WHEN_TheyAreCompared_THEN_TheEnvironmentIsIgnored() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		ActorScope other = new ActorScope();
		ActivationRecord otherEnvironment = new ActivationRecord();
		otherEnvironment.setVariableValue("unrelated", 99);
		other.setEnvironment(otherEnvironment);
		other.addVariableToScope("v", 1);

		Assertions.assertEquals(actorScope, other);
		Assertions.assertEquals(actorScope.hashCode(), other.hashCode());
	}

	@Test
	public void GIVEN_TwoScopesWithDifferentLocals_WHEN_TheyAreCompared_THEN_TheyAreNotEqual() {
		attachEnvironment();
		actorScope.addVariableToScope("v", 1);

		ActorScope other = new ActorScope();
		other.setEnvironment(actorScope.getEnvironment());
		other.addVariableToScope("v", 2);

		Assertions.assertNotEquals(actorScope, other);
	}
}
