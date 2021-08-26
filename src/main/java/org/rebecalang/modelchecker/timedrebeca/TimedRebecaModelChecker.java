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
    protected void addRequiredScopeToScopeStack(BaseActorState baseActorState, ArrayList<ReactiveClassDeclaration> actorSeries) {
        addTimedScopeToScopeStack(baseActorState);
        for (ReactiveClassDeclaration actor : actorSeries) {
            baseActorState.pushInActorScope(actor.getName());
            addStateVarsToRelatedScope(baseActorState, actor);
        }
    }

    private void addTimedScopeToScopeStack(BaseActorState baseActorState) {
        baseActorState.pushInActorScope("TimedRebec");
        baseActorState.addVariableToRecentScope(CURRENT_TIME, 0);
        baseActorState.addVariableToRecentScope(RESUMING_TIME, 0);
        baseActorState.addVariableToRecentScope("self", baseActorState);
    }

    @Override
    protected void doFineGrainedModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
        TimedState initialState = (TimedState) statespace.getInitialState();
        PriorityQueue<TimePriorityQueueItem> nextStatesQueue = new PriorityQueue<>();
        int enablingTime = initialState.getEnablingTime();
        if (enablingTime == Integer.MAX_VALUE)
            throw new ModelCheckingException("Deadlock");
        nextStatesQueue.add(new TimePriorityQueueItem(enablingTime, initialState));
        while (!nextStatesQueue.isEmpty()) {
            TimePriorityQueueItem timePriorityQueueItem = nextStatesQueue.poll();
            TimedState currentState = (TimedState) timePriorityQueueItem.getItem();
//Until here
            List<BaseActorState> enabledActors = currentState.getEnabledActors();
            if (enabledActors.isEmpty())
                throw new ModelCheckingException("Deadlock");
            for (BaseActorState baseActorState : enabledActors) {
                do {
                    TimedState newState = (TimedState) cloneState(currentState);

                    BaseActorState newBaseActorState = newState.getActorState(baseActorState.getName());
                    newBaseActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
                    String transitionLabel = calculateTransitionLabel(baseActorState, newBaseActorState);
                    Long stateKey = (long) newState.hashCode();

                    if (!statespace.hasStateWithKey(stateKey)) {
                        newState.setId(stateCounter++);
                        nextStatesQueue.add(new TimePriorityQueueItem(newState.getEnablingTime(), newState));
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

    protected String calculateTransitionLabel(BaseActorState baseActorState, BaseActorState newBaseActorState) {
        return null;
    }

    public void configPolicy(String policyName) throws ModelCheckingException {

    }
}
