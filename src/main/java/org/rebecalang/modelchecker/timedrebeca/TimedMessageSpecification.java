package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

import java.util.ArrayList;

public class TimedMessageSpecification extends MessageSpecification {
    int minStartTime;
    int maxStartTime;

    public TimedMessageSpecification(
            String messageName,
            ArrayList<Object> parameters,
            BaseActorState baseActorState,
            int minStartTime,
            int maxStartTime) {
        super(messageName, parameters, baseActorState);
        this.maxStartTime = maxStartTime;
        this.minStartTime = minStartTime;
    }
}
