package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaTypeSystem;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.RebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.timedrebeca.rilinterpreter.CallTimedMsgSrvInstructionInterpreter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class TimedRebecaModelChecker extends CoreRebecaModelChecker {

    public final static String CURRENT_TIME = "current_time";
    public final static String RESUMING_TIME = "resuming_time";

    public TimedRebecaModelChecker(
            TimedRebecaTypeSystem timedRebecaTypeSystem,
            RebecaModelCompiler rebecaModelCompiler,
            ExceptionContainer exceptionContainer,
            Rebeca2RILModelTransformer rebeca2RILModelTransformer
    ) {
        super(timedRebecaTypeSystem, rebecaModelCompiler, exceptionContainer, rebeca2RILModelTransformer);
    }

    @Override
    protected void addRequiredScopeToScopeStack(ActorState actorState, ArrayList<ReactiveClassDeclaration> actorSeries) {
        addTimedScopeToScopeStack(actorState);
        for (ReactiveClassDeclaration actor : actorSeries) {
            actorState.pushInActorScope(actor.getName());
            addStateVarsToRelatedScope(actorState, actor);
        }
    }

    private void addTimedScopeToScopeStack(ActorState actorState) {
        actorState.pushInActorScope("TimedRebec");
        actorState.addVariableToRecentScope(CURRENT_TIME, 0);
        actorState.addVariableToRecentScope(RESUMING_TIME, 0);
        actorState.addVariableToRecentScope("self", actorState);
    }

    @Override
    protected void doFineGrainedModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
        TimedState initialState = (TimedState) statespace.getInitialState();
        PriorityQueue<OpenBorderQueueItem> nextStatesQueue = new PriorityQueue<OpenBorderQueueItem>();
        int enablingTime = initialState.getEnablingTime();
        if (enablingTime == Integer.MAX_VALUE)
            throw new ModelCheckingException("Deadlock");
        nextStatesQueue.add(new OpenBorderQueueItem(enablingTime, initialState));
        while (!nextStatesQueue.isEmpty()) {
            OpenBorderQueueItem openBorderQueueItem = nextStatesQueue.poll();
            TimedState currentState = openBorderQueueItem.getTimedState();

            List<ActorState> enabledActors = currentState.getEnabledActors();
            if (enabledActors.isEmpty())
                throw new ModelCheckingException("Deadlock");
            for (ActorState actorState : enabledActors) {
                do {
                    TimedState newState = (TimedState) cloneState(currentState);

                    ActorState newActorState = newState.getActorState(actorState.getName());
                    newActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
                    String transitionLabel = calculateTransitionLabel(actorState, newActorState);
                    Long stateKey = (long) newState.hashCode();

                    if (!statespace.hasStateWithKey(stateKey)) {
                        newState.setId(stateCounter++);
                        nextStatesQueue.add(new OpenBorderQueueItem(newState.getEnablingTime(), newState));
                        statespace.addState(stateKey, newState);
                        newState.clearLinks();
                        currentState.addChildState(transitionLabel, newState);
                        newState.addParentState(transitionLabel, currentState);
                    } else {
                        State repeatedState = statespace.getState(stateKey);
                        currentState.addChildState(transitionLabel, repeatedState);
                        repeatedState.addParentState(transitionLabel, currentState);
                    }
                } while (StatementInterpreterContainer.getInstance().hasNondeterminism());
            }
        }
        RebecaModelChecker.printStateSpace(initialState);
    }

    protected TimedState createFreshState() {
        return new TimedState();
    }

    protected TimedActorState createFreshActorState() {
        return new TimedActorState();
    }

    protected void initializeStatementInterpreterContainer() {
        super.initializeStatementInterpreterContainer();

        StatementInterpreterContainer.getInstance().registerInterpreter(CallTimedMsgSrvInstructionBean.class,
                new CallTimedMsgSrvInstructionInterpreter());
    }

    protected String calculateTransitionLabel(ActorState actorState, ActorState newActorState) {
        return null;
    }

    public void configPolicy(String policyName) throws ModelCheckingException {

    }

    private class OpenBorderQueueItem implements Comparable<OpenBorderQueueItem> {
        private int time;
        private TimedState timedState;

        public OpenBorderQueueItem(int time, TimedState timedState) {
            super();
            this.time = time;
            this.timedState = timedState;
        }

//		public int getTime() {
//			return time;
//		}
//
//		public void setTime(int time) {
//			this.time = time;
//		}

        public TimedState getTimedState() {
            return timedState;
        }

//		public void setTimedState(TimedState timedState) {
//			this.timedState = timedState;
//		}

        public int compareTo(OpenBorderQueueItem openBorderQueueItem) {
            if (this.time > openBorderQueueItem.time)
                return -1;
            if (this.time < openBorderQueueItem.time)
                return 1;
            return 0;
        }
    }
}
