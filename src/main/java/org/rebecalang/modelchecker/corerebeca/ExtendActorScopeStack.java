package org.rebecalang.modelchecker.corerebeca;

public class ExtendActorScopeStack extends ActorScopeStack {
    private String typeName;

    public ExtendActorScopeStack(String typeName) {
        this.typeName = typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
