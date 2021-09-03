package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.timedrebeca.TimedRebecaTypeSystem;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.RebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
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

    private TimedState executeNewState(
            TimedState currentState,
            TimedActorState actorState,
            RILModel transformedRILModel,
            int stateCounter,
            boolean resume,
            TimedMessageSpecification msg) {

        TimedState newState = (TimedState) cloneState(currentState);
        TimedActorState newActorState = (TimedActorState) newState.getActorState(actorState.getName());
        if (resume)
            newActorState.resumeExecution(newState, transformedRILModel, modelCheckingPolicy);
        else
            newActorState.execute(newState, transformedRILModel, modelCheckingPolicy, msg);
        String transitionLabel = calculateTransitionLabel(actorState, newActorState, msg);
        Long stateKey = (long) newState.hashCode();
        if (!statespace.hasStateWithKey(stateKey)) {
            newState.setId(stateCounter++);
            statespace.addState(stateKey, newState);
            newState.clearLinks();
            currentState.addChildState(transitionLabel, newState);
            newState.addParentState(transitionLabel, currentState);
        } else {
            State repeatedState = statespace.getState(stateKey);
            currentState.addChildState(transitionLabel, repeatedState);
            repeatedState.addParentState(transitionLabel, currentState);
        }
        return newState;
    }


    @Override
    protected void doFineGrainedModelChecking(RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;
            PriorityQueue<TimePriorityQueueItem<TimedState>> nextStatesQueue = new PriorityQueue<>();

            TimedState initialState = (TimedState) statespace.getInitialState();
            nextStatesQueue.add(new TimePriorityQueueItem(initialState.getEnablingTime(), initialState));

            while (!nextStatesQueue.isEmpty()) {
                TimePriorityQueueItem timePriorityQueueItem = nextStatesQueue.poll();
                TimedState currentState = (TimedState) timePriorityQueueItem.getItem();
                int enablingTime = currentState.getEnablingTime();
                currentState.checkForTimeStep(enablingTime);
                List<TimedActorState> enabledActors = currentState.getEnabledActors(enablingTime);

                for (TimedActorState currentActorState : enabledActors) {
                    do {
                        if (currentActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                            TimedState newState = executeNewState(currentState, currentActorState, transformedRILModel,
                                    stateCounter, true, null);
                            nextStatesQueue.add(new TimePriorityQueueItem(newState.getEnablingTime(), newState));
                        } else {
                            for (TimedMessageSpecification msg : currentActorState.getEnabledMsgs(enablingTime)) {
                                TimedState newState = executeNewState(currentState, currentActorState, transformedRILModel,
                                        stateCounter, false, msg);
                                nextStatesQueue.add(new TimePriorityQueueItem(newState.getEnablingTime(), newState));
                            }
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
}
