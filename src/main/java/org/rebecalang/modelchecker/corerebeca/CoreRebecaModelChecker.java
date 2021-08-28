package org.rebecalang.modelchecker.corerebeca;

import com.rits.cloning.Cloner;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.ScopeException;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.*;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.IndependentMethodExecutor;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.CoarseGrainedPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.FineGrainedPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.*;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class CoreRebecaModelChecker {

    protected RebecaModelCompiler rebecaModelCompiler;
    protected ExceptionContainer exceptionContainer;
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    protected CoreRebecaTypeSystem coreRebecaTypeSystem;
    protected StateSpace statespace;
    protected AbstractPolicy modelCheckingPolicy;

    public final static String FINE_GRAINED_POLICY = "fine";
    public final static String COARSE_GRAINED_POLICY = "coarse";

    private Cloner cloner;

    public CoreRebecaModelChecker(
            CoreRebecaTypeSystem coreRebecaTypeSystem,
            RebecaModelCompiler rebecaModelCompiler,
            ExceptionContainer exceptionContainer,
            Rebeca2RILModelTransformer rebeca2RILModelTransformer) {
        this.cloner = new Cloner();
        this.coreRebecaTypeSystem = coreRebecaTypeSystem;
        this.rebecaModelCompiler = rebecaModelCompiler;
        this.exceptionContainer = exceptionContainer;
        this.rebeca2RILModelTransformer = rebeca2RILModelTransformer;
    }

    public StateSpace getStateSpace() {
        return statespace;
    }

    protected Pair<RebecaModel, SymbolTable> compileModel(
            File model,
            Set<CompilerExtension> extension,
            CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    public void modelCheck(
            File model,
            Set<CompilerExtension> extension,
            CoreVersion coreVersion) throws ModelCheckingException {
        modelCheck(compileModel(model, extension, coreVersion), extension, coreVersion);
    }

    public void modelCheck(
            Pair<RebecaModel, SymbolTable> model,
            Set<CompilerExtension> extension,
            CoreVersion coreVersion) throws ModelCheckingException {
        this.statespace = new StateSpace();

        // To ignore redeclaration of variable
        if (!exceptionContainer.exceptionsIsEmpty()) {
            for (Set<Exception> exceptionContainer : exceptionContainer.getExceptions().values()) {
                for (Exception exception : exceptionContainer)
                    if (!(exception instanceof ScopeException)) return;
            }
        }

        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(model, extension, coreVersion);
        initializeStatementInterpreterContainer();
        generateFirstState(transformedRILModel, model.getFirst());
        doFineGrainedModelChecking(transformedRILModel);
    }

    protected void generateFirstState(RILModel transformedRILModel, RebecaModel model) {
        State initialState = createFreshState();
        List<MainRebecDefinition> mainRebecDefinitions = model.getRebecaCode().getMainDeclaration()
                .getMainRebecDefinition();
        generateInitialActorStates(initialState, mainRebecDefinitions);

        setInitialKnownRebecsOfActors(initialState, mainRebecDefinitions);

        callConstructorsOfActors(transformedRILModel, initialState, mainRebecDefinitions);

        statespace.addInitialState(initialState);
    }

    protected State createFreshState() {
        return new State();
    }

    private void callConstructorsOfActors(
            RILModel transformedRILModel,
            State initialState,
            List<MainRebecDefinition> mainRebecDefinitions) {
        for (MainRebecDefinition definition : mainRebecDefinitions) {
            ReactiveClassDeclaration metaData;
            try {
                metaData = (ReactiveClassDeclaration) coreRebecaTypeSystem.getMetaData(definition.getType());
                ConstructorDeclaration constructorDeclaration = metaData.getConstructors().get(0);
                String computedConstructorName = RILUtilities.computeMethodName(metaData, constructorDeclaration);
                BaseActorState baseActorState = initialState.getActorState(definition.getName());
                baseActorState.pushInActorScope(baseActorState.getTypeName());
                baseActorState.initializePC(computedConstructorName, 0);
                while (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                    ProgramCounter pc = baseActorState.getPC();
                    InstructionBean ib = transformedRILModel.getInstructionList(pc.getMethodName()).get(pc.getLineNumber());
                    StatementInterpreterContainer.getInstance().retrieveInterpreter(ib).interpret(ib, baseActorState,
                            initialState);
                }
            } catch (CodeCompilationException e) {
                e.printStackTrace();
            }
        }
    }

    private void setInitialKnownRebecsOfActors(State initialState, List<MainRebecDefinition> mainRebecDefinitions) {
        for (MainRebecDefinition definition : mainRebecDefinitions) {
            ReactiveClassDeclaration metaData;
            try {
                metaData = (ReactiveClassDeclaration) coreRebecaTypeSystem.getMetaData(definition.getType());
            } catch (CodeCompilationException e) {
                System.err.println("This exception should not happen!");
                e.printStackTrace();
                return;
            }
            addKnownRebecsToRelatedScope(metaData, definition, initialState);
        }
    }

    private void addKnownRebecsToRelatedScope(ReactiveClassDeclaration actorMetaData,
                                              MainRebecDefinition mainRebecDefinition, State initialState) {
        BaseActorState baseActorState = initialState.getActorState(mainRebecDefinition.getName());
        ArrayList<ReactiveClassDeclaration> actorSeries = getActorSeries(actorMetaData);
        int startIndex = 0;
        for (int j = 0; j < actorSeries.size(); j++) {
            ReactiveClassDeclaration curActor = actorSeries.get(j);
            for (int i = 0; i < curActor.getKnownRebecs().size(); i++) {
                Expression relatedBinding = mainRebecDefinition.getBindings().get(startIndex);
                startIndex += (i + 1);
                if (!(relatedBinding instanceof TermPrimary))
                    throw new RebecaRuntimeInterpreterException("not handled yet!");
                String instanceName = ((TermPrimary) relatedBinding).getName();
                String knownRebecName = getKnownRebecName(curActor.getKnownRebecs(), i);
                BaseActorState knownActorState = initialState.getActorState(instanceName);
                baseActorState.addVariableToExactScope(knownRebecName, knownActorState, j+1);
            }
        }
    }

    private void generateInitialActorStates(State initialState, List<MainRebecDefinition> mainRebecDefinitions) {
        for (MainRebecDefinition definition : mainRebecDefinitions) {
            ReactiveClassDeclaration metaData;
            try {
                metaData = (ReactiveClassDeclaration) coreRebecaTypeSystem.getMetaData(definition.getType());
            } catch (CodeCompilationException e) {
                System.err.println("This exception should not happen!");
                e.printStackTrace();
                return;
            }
            ArrayList<ReactiveClassDeclaration> actorSeries = getActorSeries(metaData);
            BaseActorState baseActorState = createFreshActorState();
            baseActorState.setTypeName(definition.getType().getTypeName());
//            baseActorState.setQueue(new LinkedList<>());
            baseActorState.setName(definition.getName());
            baseActorState.initializeScopeStack();
            addRequiredScopeToScopeStack(baseActorState, actorSeries);
            initialState.putActorState(definition.getName(), baseActorState);
        }
    }

    protected void addStateVarsToRelatedScope(BaseActorState baseActorState, ReactiveClassDeclaration actorMetaData) {
        for (FieldDeclaration fieldDeclaration : actorMetaData.getStatevars()) {
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                baseActorState.addVariableToRecentScope(variableDeclarator.getVariableName(), 0);
            }
        }
    }

    protected void addRequiredScopeToScopeStack(BaseActorState baseActorState, ArrayList<ReactiveClassDeclaration> actorSeries) {
        for (ReactiveClassDeclaration actor : actorSeries) {
            baseActorState.pushInActorScope(actor.getName());
            addStateVarsToRelatedScope(baseActorState, actor);
        }
        baseActorState.addVariableToExactScope("self", baseActorState, 0);
    }

    private ArrayList<ReactiveClassDeclaration> getActorSeries(ReactiveClassDeclaration lastActor) {
        ReactiveClassDeclaration curActor = lastActor;
        ArrayList<ReactiveClassDeclaration> actorSeries = new ArrayList<>();
        while (curActor.getExtends() != null) {
            actorSeries.add(curActor);
            try {
                curActor = (ReactiveClassDeclaration) curActor.getExtends().getTypeSystem().getMetaData(curActor.getExtends());
            } catch (CodeCompilationException e) {
                e.printStackTrace();
            }
        }
        actorSeries.add(curActor);
        Collections.reverse(actorSeries);
        return actorSeries;
    }

    protected BaseActorState createFreshActorState() {
        return new ActorSate();
    }

    protected void initializeStatementInterpreterContainer() {
        StatementInterpreterContainer.getInstance().registerInterpreter(AssignmentInstructionBean.class,
                new AssignmentInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(CallMsgSrvInstructionBean.class,
                new CallMsgSrvInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(MethodCallInstructionBean.class,
                new MethodCallInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(DeclarationInstructionBean.class,
                new DeclarationInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(EndMethodInstructionBean.class,
                new EndMethodInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(EndMsgSrvInstructionBean.class,
                new EndMsgSrvInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(JumpIfNotInstructionBean.class,
                new JumpIfNotInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(PopARInstructionBean.class,
                new PopARInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(PushARInstructionBean.class,
                new PushARInstructionInterpreter());
        StatementInterpreterContainer.getInstance().registerInterpreter(ExternalMethodCallInstructionBean.class,
                new ExternalMethodCallInterpreter());

        ExternalMethodRepository.getInstance().registerExecuter(IndependentMethodExecutor.KEY,
                new IndependentMethodExecutor());
    }

    private String getKnownRebecName(List<FieldDeclaration> knownRebecs, int i) {
        int cnt = 0;
        for (FieldDeclaration fd : knownRebecs) {
            for (VariableDeclarator vd : fd.getVariableDeclarators()) {
                if (cnt == i)
                    return vd.getVariableName();
                cnt++;
            }
        }
        throw new RebecaRuntimeInterpreterException("this case should not happen!!");
    }

    protected void doFineGrainedModelChecking(
            RILModel transformedRILModel) throws ModelCheckingException {
        int stateCounter = 1;

        State initialState = statespace.getInitialState();
        LinkedList<State> nextStatesQueue = new LinkedList<>();
        nextStatesQueue.add(initialState);
        while (!nextStatesQueue.isEmpty()) {
            State currentState = nextStatesQueue.pollFirst();
            List<BaseActorState> enabledActors = currentState.getEnabledActors();
            if (enabledActors.isEmpty())
                throw new ModelCheckingException("Deadlock");
            for (BaseActorState baseActorState : enabledActors) {
                do {
                    StatementInterpreterContainer.getInstance().clearNondeterminism();
                    State newState = cloneState(currentState);

                    ActorSate newActorState = (ActorSate)newState.getActorState(baseActorState.getName());
                    newActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
                    String transitionLabel = calculateTransitionLabel(baseActorState, newActorState);
                    Long stateKey = (long) newState.hashCode();

                    if (!statespace.hasStateWithKey(stateKey)) {
                        newState.setId(stateCounter++);
                        nextStatesQueue.add(newState);
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
    }

    protected String calculateTransitionLabel(BaseActorState baseActorState, BaseActorState newBaseActorState) {

        String executingMessageName;

        if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
            ProgramCounter pc = baseActorState.getPC();
            executingMessageName = pc.getMethodName();
            executingMessageName += " [" + pc.getLineNumber() + ",";
        } else {
            executingMessageName = baseActorState.getMessage().messageName;
            executingMessageName += " [START,";

        }

        if (newBaseActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
            ProgramCounter pc = newBaseActorState.getPC();
            executingMessageName += pc.getLineNumber() + "]";
        } else {
            executingMessageName += "END]";

        }
        return baseActorState.getName() + "." + executingMessageName;
    }

    protected State cloneState(State currentState) {
        List<Pair<String, State>> childStates = currentState.getChildStates();
        List<Pair<String, State>> parentStates = currentState.getParentStates();
        currentState.clearLinks();
        State newState = cloner.deepClone(currentState);
        currentState.setParentStates(parentStates);
        currentState.setChildStates(childStates);
        return newState;
    }

    public void configPolicy(String policyName) throws ModelCheckingException {
        if (policyName.equals(COARSE_GRAINED_POLICY))
            modelCheckingPolicy = new CoarseGrainedPolicy();
        else if (policyName.equals(FINE_GRAINED_POLICY))
            modelCheckingPolicy = new FineGrainedPolicy();
        else
            throw new ModelCheckingException("Unknown policy " + policyName);
    }
}