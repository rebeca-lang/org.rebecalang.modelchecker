package org.rebecalang.modelchecker.corerebeca;

import java.util.ArrayList;
import java.util.List;

public class MessageSpecification {
    String messageName;
    List<Object> parameters;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((messageName == null) ? 0 : messageName.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((senderActorState == null) ? 0 : senderActorState.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageSpecification other = (MessageSpecification) obj;
        if (messageName == null) {
            if (other.messageName != null)
                return false;
        } else if (!messageName.equals(other.messageName))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (senderActorState == null) {
			return other.senderActorState == null;
        } else return senderActorState.equals(other.senderActorState);
	}

    ActorState senderActorState;

    public MessageSpecification(String messageName, ArrayList<Object> parameters, ActorState actorState) {
        super();
        this.messageName = messageName;
        this.parameters = parameters;
        this.senderActorState = actorState;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public ActorState getSenderActorState() {
        return senderActorState;
    }

    public void setSenderActorState(ActorState senderActorState) {
        this.senderActorState = senderActorState;
    }

}
