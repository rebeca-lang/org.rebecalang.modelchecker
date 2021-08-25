package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

import java.util.ArrayList;

public class TimedMessageSpecification extends MessageSpecification {
    int minStartTime;
    int maxStartTime;

    public TimedMessageSpecification(
            String messageName,
            ArrayList<Object> parameters,
            ActorState actorState,
            int minStartTime,
            int maxStartTime) {
        super(messageName, parameters, actorState);
        this.maxStartTime = maxStartTime;
        this.minStartTime = minStartTime;
    }
}
