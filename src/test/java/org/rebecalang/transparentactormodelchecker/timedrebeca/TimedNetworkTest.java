package org.rebecalang.transparentactormodelchecker.timedrebeca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaNetworkState;

public class TimedNetworkTest {

	CoreRebecaActorState coreRebecaActorState;
	TimedRebecaMessageState message1, message2;

	@BeforeEach
	public void setup() {
		coreRebecaActorState = new CoreRebecaActorState(0);
		message1 = new TimedRebecaMessageState();
		message1.setName("m1");
		message1.setSenderId(coreRebecaActorState.getId());
		message1.setReceiverId(coreRebecaActorState.getId());
		message1.setParameters(new HashMap<String, Object>());
		message1.setArrival(10);
		message1.setDeadline(20);

		message2 = new TimedRebecaMessageState();
		message2.setName("m2");
		message2.setSenderId(coreRebecaActorState.getId());
		message2.setReceiverId(coreRebecaActorState.getId());
		message2.setParameters(new HashMap<String, Object>());
		message2.setArrival(10);
		message2.setDeadline(20);

	}

	@Test
	public void TestNetwork() {
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		tns.addMessage(message1);

		assertEquals(1, tns.getReceivedMessages().size());
	}

	@Test
	public void TestNotEqualDifferentMessages() {
		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(message1);

		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(message2);

		Pair<Boolean, Integer> result = tns1.shiftEquals(tns2);
		assertFalse(result.getFirst());
	}

	@Test
	public void TestEqualIdenticalMessage() {
		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(message1);

		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(message1);

		Pair<Boolean, Integer> result = tns1.shiftEquals(tns2);
		assertTrue(result.getFirst());
	}

	@Test
	public void TestEqualClonedMessage() {
		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(message1);

		message2.setName("m1");
		message2.setArrival(20);
		message2.setDeadline(30);
		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(message2);

		Pair<Boolean, Integer> result = tns1.shiftEquals(tns2);
		assertTrue(result.getFirst());
		assertEquals(10, result.getSecond());
	}

	@Test
	public void TestNotEqualDifferentTimesMessages() {
		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(message1);

		message2.setName("m1");
		message2.setArrival(11);
		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(message2);

		Pair<Boolean, Integer> result = tns1.shiftEquals(tns2);
		assertFalse(result.getFirst());
	}

	@Test
	public void GIVEN_TwoMessagesWithTheSameArrival_WHEN_TheyAreAdded_THEN_TheyShareOneTimeBucket() {
		message2.setName("m2");
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		tns.addMessage(message1);
		tns.addMessage(message2);

		assertEquals(1, tns.getReceivedMessages().size());
		assertEquals(10, tns.getReceivedMessages().get(0).getTime());
		assertEquals(2, tns.getReceivedMessages().get(0)
				.getReceiverMessages(message1.getReceiverId()).getAllSentMessages().size());
	}

	@Test
	public void GIVEN_TwoMessagesWithDifferentArrivals_WHEN_TheyAreAdded_THEN_TwoBucketsExist() {
		message2.setArrival(30);
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		tns.addMessage(message1);
		tns.addMessage(message2);

		assertEquals(2, tns.getReceivedMessages().size());
	}

	@Test
	public void GIVEN_AnEmptyNetwork_WHEN_HasMessageIsCalled_THEN_ItIsFalseUntilAMessageArrives() {
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		assertFalse(tns.hasMessage());

		tns.addMessage(message1);

		assertTrue(tns.hasMessage());
	}

	@Test
	public void GIVEN_TwoEmptyNetworks_WHEN_ShiftEqualsIsCalled_THEN_TheyAreEquivalent() {
		assertTrue(new TimedRebecaNetworkState()
				.shiftEquals(new TimedRebecaNetworkState()).getFirst());
	}

	@Test
	public void GIVEN_NetworksWithDifferentBucketCounts_WHEN_ShiftEqualsIsCalled_THEN_TheyAreNotEquivalent() {
		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(message1);

		message2.setName("m1");
		message2.setArrival(40);
		message2.setDeadline(50);
		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(message1);
		tns2.addMessage(message2);

		assertFalse(tns1.shiftEquals(tns2).getFirst());
	}

	@Test
	public void GIVEN_ANetworkHoldingAMessage_WHEN_ItIsCloned_THEN_TheCloneStillHoldsThatMessage() {
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		tns.addMessage(message1);

		TimedRebecaNetworkState clone = tns.clone();

		assertEquals(1, clone.getReceivedMessages().size());
		assertEquals(1, clone.getReceivedMessages().get(0)
				.getReceiverMessages(message1.getReceiverId()).getAllSentMessages().size());
	}

	@Test
	public void GIVEN_AnActorReceivesTwoMessagesAtOneInstant_WHEN_OnlyTheSecondDiffers_THEN_TheNetworksAreNotEquivalent() {
		TimedRebecaMessageState first = message1.clone();
		TimedRebecaMessageState secondA = message1.clone();
		secondA.setName("a");
		TimedRebecaMessageState secondB = message1.clone();
		secondB.setName("b");

		TimedRebecaNetworkState tns1 = new TimedRebecaNetworkState();
		tns1.addMessage(first);
		tns1.addMessage(secondA);

		TimedRebecaNetworkState tns2 = new TimedRebecaNetworkState();
		tns2.addMessage(first);
		tns2.addMessage(secondB);

		assertFalse(tns1.shiftEquals(tns2).getFirst());
	}

	@Test
	public void GIVEN_AMessageArrivesEarlierThanAnExistingOne_WHEN_ItIsAdded_THEN_BucketsStayOrderedByTime() {
		TimedRebecaNetworkState tns = new TimedRebecaNetworkState();
		message2.setArrival(30);
		tns.addMessage(message2);
		tns.addMessage(message1);

		assertEquals(2, tns.getReceivedMessages().size());
		assertEquals(10, tns.getReceivedMessages().get(0).getTime());
		assertEquals(30, tns.getReceivedMessages().get(1).getTime());
	}
}
