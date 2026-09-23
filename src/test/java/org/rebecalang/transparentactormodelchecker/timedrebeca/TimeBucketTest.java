package org.rebecalang.transparentactormodelchecker.timedrebeca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.ActorReceivingBucket;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimeBucket;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;

/**
 * TimeBucket and ActorReceivingBucket are only ever reached through
 * TimedRebecaNetworkState, so a fault in them shows up as a wrong state count
 * rather than as a failure of their own. These tests drive them directly.
 */
public class TimeBucketTest {

	private static final int RECEIVER = 3;
	private static final int OTHER_RECEIVER = 4;

	private TimedRebecaMessageState message(String name, int receiverId,
			int arrival, int deadline) {
		TimedRebecaMessageState message = new TimedRebecaMessageState();
		message.setName(name);
		message.setSenderId(0);
		message.setReceiverId(receiverId);
		message.setParameters(new HashMap<String, Object>());
		message.setArrival(arrival);
		message.setDeadline(deadline);
		return message;
	}

	private TimeBucket bucketWith(TimedRebecaMessageState... messages) {
		TimeBucket bucket = new TimeBucket(messages[0].getArrival());
		for (TimedRebecaMessageState message : messages)
			bucket.getReceiverMessages(message.getReceiverId()).add(message);
		return bucket;
	}

	@Test
	public void GIVEN_ABucketHoldingAMessage_WHEN_ItIsCloned_THEN_TheCloneHoldsIt() {
		TimeBucket bucket = bucketWith(message("m", RECEIVER, 10, 20));

		TimeBucket clone = bucket.clone();

		assertEquals(1, clone.getMessages().size());
		assertEquals(1, clone.getReceiverMessages(RECEIVER).getAllSentMessages().size());
	}

	@Test
	public void GIVEN_AClonedBucket_WHEN_TheCloneGetsAMessage_THEN_TheOriginalIsUnchanged() {
		TimeBucket bucket = bucketWith(message("m", RECEIVER, 10, 20));

		TimeBucket clone = bucket.clone();
		clone.getReceiverMessages(RECEIVER).add(message("later", RECEIVER, 10, 20));

		assertEquals(1, bucket.getReceiverMessages(RECEIVER).getAllSentMessages().size());
		assertEquals(2, clone.getReceiverMessages(RECEIVER).getAllSentMessages().size());
	}

	@Test
	public void GIVEN_ABucketHoldingAMessage_WHEN_ItIsCloned_THEN_TheMessagesAreCopies() {
		TimeBucket bucket = bucketWith(message("m", RECEIVER, 10, 20));

		TimeBucket clone = bucket.clone();

		assertNotSame(bucket.getReceiverMessages(RECEIVER),
				clone.getReceiverMessages(RECEIVER));
	}

	@Test
	public void GIVEN_MessagesForTwoReceivers_WHEN_TheyAreAdded_THEN_EachHasItsOwnBucket() {
		TimeBucket bucket = bucketWith(message("m1", RECEIVER, 10, 20),
				message("m2", OTHER_RECEIVER, 10, 20));

		assertEquals(2, bucket.getMessages().size());
		assertEquals(1, bucket.getReceiverMessages(RECEIVER).getAllSentMessages().size());
		assertEquals(1, bucket.getReceiverMessages(OTHER_RECEIVER).getAllSentMessages().size());
	}

	@Test
	public void GIVEN_ABucket_WHEN_TimeIsAsked_THEN_ItIsTheTimeItWasBuiltWith() {
		assertEquals(10, new TimeBucket(10).getTime());
	}

	@Test
	public void GIVEN_TwoBucketsWithTheSameMessage_WHEN_ShiftEqualsIsCalled_THEN_TheyMatch() {
		TimeBucket first = bucketWith(message("m", RECEIVER, 10, 20));
		TimeBucket second = bucketWith(message("m", RECEIVER, 10, 20));

		assertTrue(first.shiftEquals(second).getFirst());
	}

	@Test
	public void GIVEN_BucketsWithDifferentReceiverCounts_WHEN_ShiftEqualsIsCalled_THEN_TheyDoNot() {
		TimeBucket first = bucketWith(message("m1", RECEIVER, 10, 20));
		TimeBucket second = bucketWith(message("m1", RECEIVER, 10, 20),
				message("m2", OTHER_RECEIVER, 10, 20));

		assertFalse(first.shiftEquals(second).getFirst());
	}

	@Test
	public void GIVEN_TwoEmptyReceivingBuckets_WHEN_ShiftEqualsIsCalled_THEN_TheyMatch() {
		assertTrue(new ActorReceivingBucket()
				.shiftEquals(new ActorReceivingBucket()).getFirst());
	}

	@Test
	public void GIVEN_TwoMessagesForOneReceiver_WHEN_OnlyTheSecondDiffers_THEN_TheyDoNotMatch() {
		ActorReceivingBucket first = new ActorReceivingBucket();
		first.add(message("m", RECEIVER, 10, 20));
		first.add(message("a", RECEIVER, 10, 20));

		ActorReceivingBucket second = new ActorReceivingBucket();
		second.add(message("m", RECEIVER, 10, 20));
		second.add(message("b", RECEIVER, 10, 20));

		assertFalse(first.shiftEquals(second).getFirst());
	}

	@Test
	public void GIVEN_MessagesMovedByAConstant_WHEN_ShiftEqualsIsCalled_THEN_TheShiftIsReported() {
		ActorReceivingBucket first = new ActorReceivingBucket();
		first.add(message("m", RECEIVER, 10, 20));

		ActorReceivingBucket second = new ActorReceivingBucket();
		second.add(message("m", RECEIVER, 15, 25));

		Pair<Boolean, Integer> result = first.shiftEquals(second);
		assertTrue(result.getFirst());
		assertEquals(5, result.getSecond());
	}

	@Test
	public void GIVEN_ArrivalAndDeadlineMoveApart_WHEN_ShiftEqualsIsCalled_THEN_TheyDoNotMatch() {
		ActorReceivingBucket first = new ActorReceivingBucket();
		first.add(message("m", RECEIVER, 10, 20));

		ActorReceivingBucket second = new ActorReceivingBucket();
		second.add(message("m", RECEIVER, 15, 21));

		assertFalse(first.shiftEquals(second).getFirst());
	}

	@Test
	public void GIVEN_AReceivingBucket_WHEN_ItIsCloned_THEN_TheCloneIsIndependent() {
		ActorReceivingBucket bucket = new ActorReceivingBucket();
		bucket.add(message("m", RECEIVER, 10, 20));

		ActorReceivingBucket clone = bucket.clone();
		clone.add(message("later", RECEIVER, 10, 20));

		assertEquals(1, bucket.getAllSentMessages().size());
		assertEquals(2, clone.getAllSentMessages().size());
	}

	@Test
	public void GIVEN_TwoMessagesForOneReceiver_WHEN_TheyAreReadBack_THEN_TheOrderIsKept() {
		ActorReceivingBucket bucket = new ActorReceivingBucket();
		bucket.add(message("first", RECEIVER, 10, 20));
		bucket.add(message("second", RECEIVER, 10, 20));

		assertEquals("first", bucket.getAllSentMessages().get(0).getName());
		assertEquals("second", bucket.getAllSentMessages().get(1).getName());
	}
}
