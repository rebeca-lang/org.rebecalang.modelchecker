package org.rebecalang.modelchecker.corerebeca;

import com.rits.cloning.Cloner;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class CoreRebecaModelChecker {

    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected ExceptionContainer exceptionContainer;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;

    @Autowired
    protected CoreRebecaTypeSystem coreRebecaTypeSystem;

    protected StateSpace statespace;

    protected AbstractPolicy modelCheckingPolicy;

    public final static String FINE_GRAINED_POLICY = "fine";
    public final static String COARSE_GRAINED_POLICY = "coarse";

    private Cloner cloner;

    public CoreRebecaModelChecker() {
        this.cloner = new Cloner();
    }

    public StateSpace getStateSpace() {
        return statespace;
    }


    protected Pair<RebecaModel, SymbolTable> compileModel(File model, Set<CompilerExtension> extension, CoreVersion coreVersion) {
        return rebecaModelCompiler.compileRebecaFile(model, extension, coreVersion);
    }

    public void modelCheck(File model,
                           Set<CompilerExtension> extension,
                           CoreVersion coreVersion) throws ModelCheckingException {
        modelCheck(compileModel(model, extension, coreVersion),
                extension, coreVersion);
    }

    public void modelCheck(Pair<RebecaModel, SymbolTable> model, Set<CompilerExtension> extension, CoreVersion coreVersion) throws ModelCheckingException {
        this.statespace = new StateSpace();

        if (!exceptionContainer.exceptionsIsEmpty())
            return;

        RILModel transformedRILModel =
                rebeca2RILModelTransformer.transformModel(model, extension, coreVersion);
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
                ActorState actorState = initialState.getActorState(definition.getName());
                actorState.pushInActorScope();
                actorState.initializePC(computedConstructorName, 0);
                while (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                    ProgramCounter pc = actorState.getPC();
                    InstructionBean ib = transformedRILModel.getInstructionList(pc.getMethodName()).get(pc.getLineNumber());
                    StatementInterpreterContainer.getInstance().retrieveInterpreter(ib).interpret(ib, actorState,
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
            List<FieldDeclaration> knownRebecs = metaData.getKnownRebecs();
            for (int i = 0; i < definition.getBindings().size(); i++) {
                Expression knownRebecDefExp = definition.getBindings().get(i);
                if (!(knownRebecDefExp instanceof TermPrimary))
                    throw new RebecaRuntimeInterpreterException("not handled yet!");
                String name = ((TermPrimary) knownRebecDefExp).getName();
                String knownRebecName = getKnownRebecName(knownRebecs, i);
                ActorState actState = initialState.getActorState(name);
                initialState.getActorState(definition.getName()).addVariableToRecentScope(knownRebecName, actState);
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
            ActorState actorState = createFreshActorState();
            actorState.initializeScopeStack();
            actorState.pushInParentsScopeStack();
            addVariableToParentsScope(actorState, metaData);
            actorState.pushInActorScope();

            for (FieldDeclaration fieldDeclaration : metaData.getStatevars()) {
                for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                    actorState.addVariableToRecentScope(variableDeclarator.getVariableName(), 0);
                }
            }
            actorState.addVariableToRecentScope("self", actorState);

            actorState.setTypeName(definition.getType().getTypeName());
            actorState.setQueue(new LinkedList<>());
            actorState.setName(definition.getName());
            initialState.putActorState(definition.getName(), actorState);

        }
    }

    private void addVariableToParentsScope(ActorState actorState, ReactiveClassDeclaration metaData) {
        try {
            if (metaData.getExtends() != null) {
                ReactiveClassDeclaration parentMetaData = (ReactiveClassDeclaration) metaData.getExtends().getTypeSystem().getMetaData(metaData.getExtends());

                while (true) {
                    for (FieldDeclaration fieldDeclaration : parentMetaData.getStatevars()) {
                        for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                            actorState.addVariableToRecentScope(variableDeclarator.getVariableName(), 0);
                        }
                    }
                    if (parentMetaData.getExtends() != null) {
                        parentMetaData = (ReactiveClassDeclaration) parentMetaData.getExtends().getTypeSystem().getMetaData(parentMetaData.getExtends());
                    } else break;
                }
            }
        } catch (CodeCompilationException e) {
            e.printStackTrace();
        }

    }


    protected ActorState createFreshActorState() {
        return new ActorState();
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
            List<ActorState> enabledActors = currentState.getEnabledActors();
            if (enabledActors.isEmpty())
                throw new ModelCheckingException("Deadlock");
            for (ActorState actorState : enabledActors) {
                do {
                    StatementInterpreterContainer.getInstance().clearNondeterminism();
                    State newState = cloneState(currentState);

                    ActorState newActorState = newState.getActorState(actorState.getName());
                    newActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
                    String transitionLabel = calculateTransitionLabel(actorState, newActorState);
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

    protected String calculateTransitionLabel(ActorState actorState, ActorState newActorState) {

        String executingMessageName;

        if (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
            ProgramCounter pc = actorState.getPC();
            executingMessageName = pc.getMethodName();
            executingMessageName += " [" + pc.getLineNumber() + ",";
        } else {
            executingMessageName = actorState.getQueue().peek().getMessageName();
            executingMessageName += " [START,";

        }

        if (newActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
            ProgramCounter pc = newActorState.getPC();
            executingMessageName += pc.getLineNumber() + "]";
        } else {
            executingMessageName += "END]";

        }
        return actorState.getName() + "." + executingMessageName;
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