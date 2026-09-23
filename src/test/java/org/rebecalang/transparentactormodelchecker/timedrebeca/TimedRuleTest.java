package org.rebecalang.transparentactormodelchecker.timedrebeca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule.TimedRebecaTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.compositionlevel.TimedRebecaCompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;
import org.springframework.test.util.ReflectionTestUtils;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.networklevelrule.TimedRebecaFTTSNetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.networklevelrule.TimedRebecaNetworkLevelDeliverMessage;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;

/**
 * The timed variants of the take-message and delivery rules. Their core-Rebeca
 * counterparts have tests; these did not, although they are what every Timed
 * Rebeca model runs on.
 */
public class TimedRuleTest {

	private TimedRebecaTakeMessageRule takeMessageRule;
	private TimedRebecaFTTSNetworkLevelDeliverMessage fttsDeliverRule;
	private TimedRebecaNetworkLevelDeliverMessage deliverRule;
	private TimedRebecaCompositionLevelTakeMessageRule compositionRule;
	private TimedRebecaActorState actor;

	@BeforeEach
	public void setup() {
		takeMessageRule = new TimedRebecaTakeMessageRule();
		fttsDeliverRule = new TimedRebecaFTTSNetworkLevelDeliverMessage();
		deliverRule = new TimedRebecaNetworkLevelDeliverMessage();
		compositionRule = new TimedRebecaCompositionLevelTakeMessageRule();
		// the rule takes its actor-level rule by injection; there is no setter
		ReflectionTestUtils.setField(compositionRule, "takeMessageRule", takeMessageRule);
		actor = new TimedRebecaActorState(0);
		actor.setVariableValue(TimedActorScope.TIME_VARIABLE, 30);
	}

	private TimedRebecaMessageState message(String name, int senderId, int arrival) {
		TimedRebecaMessageState message = new TimedRebecaMessageState();
		message.setName(name);
		message.setSenderId(senderId);
		message.setReceiverId(0);
		message.setParameters(new HashMap<String, Object>());
		message.setArrival(arrival);
		message.setDeadline(arrival + 100);
		return message;
	}

	// ---------------------------------------------------------- take message --

	@Test
	public void GIVEN_AnEmptyBag_WHEN_TheTimedTakeRuleIsApplied_THEN_ItIsDisabled() {
		assertThrows(RuleIsDisabledException.class,
				() -> takeMessageRule.applyRule(actor, actor));
	}

	@Test
	public void GIVEN_AnArrivedMessage_WHEN_TheTimedTakeRuleIsApplied_THEN_ItIsTaken()
			throws RuleIsDisabledException {
		actor.receiveMessage(message("tick", 1, 10));

		Transition<AbstractActorState> transition = takeMessageRule.applyRule(actor, actor);

		assertEquals(1, transition.size());
		TakeMessageAction action = (TakeMessageAction) transition.getDestinationsActions().get(0);
		assertEquals("tick", action.getMessage().getName());
	}

	@Test
	public void GIVEN_AnArrivedMessage_WHEN_ItIsTaken_THEN_TheBagIsEmptyAgain()
			throws RuleIsDisabledException {
		actor.receiveMessage(message("tick", 1, 10));

		takeMessageRule.applyRule(actor, actor);

		assertTrue(actor.bagIsEmpty());
	}

	@Test
	public void GIVEN_AMessageWithAParameter_WHEN_ItIsTaken_THEN_TheParameterIsInScope()
			throws RuleIsDisabledException {
		TimedRebecaMessageState message = message("tick", 1, 10);
		message.getParameters().put("value", 7);
		actor.receiveMessage(message);

		takeMessageRule.applyRule(actor, actor);

		assertEquals(7, actor.getVariableValue(new org.rebecalang.modeltransformer.ril
				.corerebeca.rilinstruction.Variable("value")));
	}

	@Test
	public void GIVEN_AMessageThatHasNotArrivedYet_WHEN_TheTimedTakeRuleIsApplied_THEN_ItIsDisabled() {
		actor.receiveMessage(message("later", 1, 90));

		assertThrows(RuleIsDisabledException.class,
				() -> takeMessageRule.applyRule(actor, actor));
	}

	// ------------------------------------------------------ network delivery --

	@Test
	public void GIVEN_AnEmptyNetwork_WHEN_TheFttsDeliverRuleIsApplied_THEN_ItIsDisabled() {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();

		assertThrows(RuleIsDisabledException.class,
				() -> fttsDeliverRule.applyRule(network, network));
	}

	@Test
	public void GIVEN_ANetworkHoldingAMessage_WHEN_TheFttsDeliverRuleIsApplied_THEN_ItIsDelivered()
			throws RuleIsDisabledException {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();
		network.addMessage(message("m", 1, 10));

		Transition<AbstractNetworkState> transition = fttsDeliverRule.applyRule(network, network);

		assertEquals(1, transition.size());
		MessageAction action = (MessageAction) transition.getDestinationsActions().get(0);
		assertEquals("m", action.getMessage().getName());
	}

	@Test
	public void GIVEN_ANetworkHoldingOneMessage_WHEN_ItIsDelivered_THEN_TheNetworkIsEmpty()
			throws RuleIsDisabledException {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();
		network.addMessage(message("m", 1, 10));

		fttsDeliverRule.applyRule(network, network);

		assertTrue(network.getReceivedMessages().isEmpty());
	}

	@Test
	public void GIVEN_MessagesAtTwoInstants_WHEN_TheFttsDeliverRuleIsApplied_THEN_TheEarlierOneGoesFirst()
			throws RuleIsDisabledException {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();
		network.addMessage(message("later", 1, 20));
		network.addMessage(message("earlier", 1, 10));

		Transition<AbstractNetworkState> transition = fttsDeliverRule.applyRule(network, network);

		MessageAction action = (MessageAction) transition.getDestinationsActions().get(0);
		assertEquals("earlier", action.getMessage().getName());
	}

	@Test
	public void GIVEN_MessagesAtTwoInstants_WHEN_TheEarlierIsDelivered_THEN_TheLaterOneRemains()
			throws RuleIsDisabledException {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();
		network.addMessage(message("later", 1, 20));
		network.addMessage(message("earlier", 1, 10));

		fttsDeliverRule.applyRule(network, network);

		assertEquals(1, network.getReceivedMessages().size());
		assertEquals(20, network.getReceivedMessages().get(0).getTime());
	}

	// ------------------------------------------------------- system level --

	private TimedRebecaSystemState systemWith(TimedRebecaActorState... actors) {
		TimedRebecaSystemState system = new TimedRebecaSystemState();
		system.setEnvironment(new ActivationRecord());
		for (TimedRebecaActorState each : actors)
			system.setActorState(each.getId(), each);
		return system;
	}

	@Test
	public void GIVEN_NoActorHasAnArrivedMessage_WHEN_TheCompositionRuleIsApplied_THEN_ItIsDisabled() {
		TimedRebecaSystemState system = systemWith(actor);

		assertThrows(RuleIsDisabledException.class,
				() -> compositionRule.applyRule(system, system));
	}

	@Test
	public void GIVEN_OneActorHasAnArrivedMessage_WHEN_TheCompositionRuleIsApplied_THEN_ThatActorMoves()
			throws RuleIsDisabledException {
		actor.receiveMessage(message("tick", 0, 10));
		TimedRebecaSystemState system = systemWith(actor);

		Transition<AbstractSystemState> transition = compositionRule.applyRule(system, system);

		assertEquals(1, transition.size());
		TakeMessageAction action = (TakeMessageAction) transition.getDestinationsActions().get(0);
		assertEquals("tick", action.getMessage().getName());
	}

	// The non-FTTS delivery rule has its whole body commented out in src/main, so
	// it can never produce a transition. This records that, rather than leaving the
	// class looking as though it works.
	@Test
	public void GIVEN_ANetworkHoldingAMessage_WHEN_ThePlainDeliverRuleIsApplied_THEN_ItIsStillDisabled() {
		TimedRebecaNetworkState network = new TimedRebecaNetworkState();
		network.addMessage(message("m", 1, 10));

		assertThrows(RuleIsDisabledException.class,
				() -> deliverRule.applyRule(network, network, 10));
		assertFalse(network.getReceivedMessages().isEmpty());
	}
}
