package org.rebecalang.transparentactormodelchecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.MethodCallActivationRecord;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.CoreRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

/**
 * The container that decides whether a newly reached system state is one the
 * search has seen before. Every state count the tool reports is the number this
 * class arrives at, and it had no test of its own.
 */
public class TransitionSystemStructureTest {

	private CoreRebecaTransitionSystem transitionSystem;
	private TransparentActorTransitionSystemState<CoreRebecaSystemState> initial;

	// the actors are compared through the environment, which setEnvironment puts
	// them into; without it two states differ in nothing and always look identical
	private CoreRebecaSystemState systemState(int counter) {
		CoreRebecaSystemState state = new CoreRebecaSystemState();
		state.setEnvironment(new ActivationRecord());
		CoreRebecaActorState actor = new CoreRebecaActorState(0);
		actor.addVariableToScope("counter", counter);
		state.setActorState(0, actor);
		return state;
	}

	@BeforeEach
	public void setup() {
		transitionSystem = new CoreRebecaTransitionSystem();
		initial = new TransparentActorTransitionSystemState<CoreRebecaSystemState>(0);
		initial.setState(systemState(0));
		transitionSystem.setInitialState(initial);
	}

	// -------------------------------------------------- state space bookkeeping --

	@Test
	public void GIVEN_AFreshTransitionSystem_WHEN_ItsSizeIsAsked_THEN_OnlyTheInitialStateIsIn() {
		assertEquals(1, transitionSystem.size());
		assertSame(initial, transitionSystem.getInitialState());
	}

	@Test
	public void GIVEN_AStateNotSeenBefore_WHEN_ItIsOffered_THEN_ItIsAddedAsNew() {
		Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result =
				transitionSystem.addIfNotExists(initial, systemState(1));

		assertTrue(result.getFirst());
		assertEquals(2, transitionSystem.size());
	}

	@Test
	public void GIVEN_AStateAlreadySeen_WHEN_ItIsOfferedAgain_THEN_TheOldOneComesBack() {
		Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> first =
				transitionSystem.addIfNotExists(initial, systemState(1));

		Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> again =
				transitionSystem.addIfNotExists(initial, systemState(1));

		assertFalse(again.getFirst());
		assertSame(first.getSecond(), again.getSecond());
	}

	@Test
	public void GIVEN_AStateAlreadySeen_WHEN_ItIsOfferedAgain_THEN_TheSizeDoesNotGrow() {
		transitionSystem.addIfNotExists(initial, systemState(1));

		transitionSystem.addIfNotExists(initial, systemState(1));

		assertEquals(2, transitionSystem.size());
	}

	@Test
	public void GIVEN_TwoDifferentStates_WHEN_TheyAreOffered_THEN_BothAreAdded() {
		transitionSystem.addIfNotExists(initial, systemState(1));
		transitionSystem.addIfNotExists(initial, systemState(2));

		assertEquals(3, transitionSystem.size());
	}

	@Test
	public void GIVEN_AStateIsAdded_WHEN_TheGraphIsWalked_THEN_ItIsLinkedBothWays() {
		TransparentActorTransitionSystemState<CoreRebecaSystemState> added =
				transitionSystem.addIfNotExists(initial, systemState(1)).getSecond();

		assertEquals(1, initial.getNextStates().size());
		assertSame(added, initial.getNextStates().get(0).getTarget());
		assertEquals(1, added.getPreviousStates().size());
		assertSame(initial, added.getPreviousStates().get(0).getTarget());
	}

	// ------------------------------------------------------------- transition --

	@Test
	public void GIVEN_ATransition_WHEN_ItIsBuilt_THEN_ItKeepsItsTargetAndAction() {
		TransparentActorTransitionSystemTransition transition =
				new TransparentActorTransitionSystemTransition(initial, TauAction.TAU);

		assertSame(initial, transition.getTarget());
		assertSame(TauAction.TAU, transition.getAction());
	}

	// ------------------------------------------- the method call frame marker --

	@Test
	public void GIVEN_AMethodCallFrame_WHEN_ItIsBuilt_THEN_ItRemembersTheCallersScope() {
		assertEquals(4, new MethodCallActivationRecord(4).getScopeIndex());
	}

	@Test
	public void GIVEN_TwoFramesWithTheSameContent_WHEN_TheScopeIndexDiffers_THEN_TheyAreNotEqual() {
		MethodCallActivationRecord first = new MethodCallActivationRecord(1);
		MethodCallActivationRecord second = new MethodCallActivationRecord(2);
		first.setVariableValue("x", 1);
		second.setVariableValue("x", 1);

		assertNotEquals(first, second);
	}

	@Test
	public void GIVEN_TwoIdenticalFrames_WHEN_TheyAreCompared_THEN_TheyAreEqual() {
		MethodCallActivationRecord first = new MethodCallActivationRecord(1);
		MethodCallActivationRecord second = new MethodCallActivationRecord(1);
		first.setVariableValue("x", 1);
		second.setVariableValue("x", 1);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	public void GIVEN_AMethodCallFrame_WHEN_ItIsCloned_THEN_TheCloneIsIndependent() {
		MethodCallActivationRecord frame = new MethodCallActivationRecord(3);
		frame.setVariableValue("x", 1);

		MethodCallActivationRecord clone = frame.clone();
		clone.setVariableValue("x", 2);

		assertEquals(3, clone.getScopeIndex());
		assertEquals(1, frame.getVariableValue("x"));
		assertEquals(2, clone.getVariableValue("x"));
	}

	// ------------------------------------------------------------- exception --

	@Test
	public void GIVEN_AModelCheckingRuntimeException_WHEN_ItIsThrown_THEN_ItCarriesItsMessage() {
		ModelCheckingRuntimeException thrown = assertThrows(
				ModelCheckingRuntimeException.class,
				() -> {
					throw new ModelCheckingRuntimeException("cloning failed");
				});

		assertEquals("cloning failed", thrown.getMessage());
	}
}
