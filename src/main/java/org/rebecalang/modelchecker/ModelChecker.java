package org.rebecalang.modelchecker;

import java.io.File;
import java.util.*;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.*;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.BuiltInMethodExecutor;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.PolicyFactory;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.AssignmentInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.MsgsrvCallInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.DeclarationInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.EndMethodInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.EndMsgSrvInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ExternalMethodCallInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.JumpIfNotInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.MethodCallInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.PopARInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.PushARInstructionInterpreter;
import org.rebecalang.modelchecker.setting.ModelCheckerSetting;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component
//@Log4j2
public abstract class ModelChecker {
    protected AbstractTypeSystem typeSystem;

    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected ExceptionContainer exceptionContainer;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;

    @Autowired
    protected StatementInterpreterContainer statementInterpreterContainer;

    @Autowired
    protected ExternalMethodRepository externalMethodRepository;

    @Autowired
    protected ConfigurableApplicationContext appContext;

    protected StateSpace<State<? extends BaseActorState>> stateSpace;

    protected AbstractPolicy modelCheckingPolicy;

    protected ModelCheckerSetting modelCheckerSetting;

    @Autowired
    protected abstract void setTypeSystem(AbstractTypeSystem typeSystem);

    protected abstract BaseActorState createInitialActorState();

    protected abstract State<? extends BaseActorState> createFreshState();

    protected abstract void doModelChecking(RILModel transformedRILModel) throws ModelCheckingException;

    protected abstract State<? extends BaseActorState> createInitialStates(RebecaModel rebecaModel);

    protected abstract BaseActorState createAnActorInitialState(MainRebecDefinition mainDefinition);

    protected void initialStateSpace() {
        this.stateSpace = new StateSpace<>();
    }

    public StateSpace<State<? extends BaseActorState>> getStateSpace() {
        return stateSpace;
    }

    protected void setStateSpace(StateSpace<State<? extends BaseActorState>> stateSpace) throws ModelCheckingException {
        this.stateSpace = stateSpace;
    }

    protected void setModelCheckerSetting(ModelCheckerSetting modelCheckerSetting) throws ModelCheckingException {
        this.modelCheckerSetting = modelCheckerSetting;

        setModelCheckingPolicy();
    }

    protected void setModelCheckingPolicy() throws ModelCheckingException {
        modelCheckingPolicy = PolicyFactory.getPolicy(modelCheckerSetting.getPolicy());
    }

    protected Pair<RebecaModel, SymbolTable> compileModel(File model) {
        return rebecaModelCompiler.compileRebecaFile(model, modelCheckerSetting.getCompilerExtension(), modelCheckerSetting.getCoreVersion());
    }

    public void modelCheck(File model, ModelCheckerSetting modelCheckerSetting) throws ModelCheckingException {
        setModelCheckerSetting(modelCheckerSetting);
        modelCheck(compileModel(model));
    }

    public void modelCheck(Pair<RebecaModel, SymbolTable> model) throws ModelCheckingException {
        initialStateSpace();

        if(!exceptionContainer.exceptionsIsEmpty())
            return;

        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(model, modelCheckerSetting.getCompilerExtension(), modelCheckerSetting.getCoreVersion());

        initializeStatementInterpreterContainer();

        generateFirstState(transformedRILModel, model);

        doModelChecking(transformedRILModel);
    }

    protected void initializeStatementInterpreterContainer() {
        statementInterpreterContainer.clear();
        statementInterpreterContainer.registerInterpreter(AssignmentInstructionBean.class,
                appContext.getBean(AssignmentInstructionInterpreter.class,
                        typeSystem));
        statementInterpreterContainer.registerInterpreter(MsgsrvCallInstructionBean.class,
                appContext.getBean(MsgsrvCallInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(MethodCallInstructionBean.class,
                appContext.getBean(MethodCallInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(DeclarationInstructionBean.class,
                appContext.getBean(DeclarationInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(EndMethodInstructionBean.class,
                appContext.getBean(EndMethodInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(EndMsgSrvInstructionBean.class,
                appContext.getBean(EndMsgSrvInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(JumpIfNotInstructionBean.class,
                appContext.getBean(JumpIfNotInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(PopARInstructionBean.class,
                appContext.getBean(PopARInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(PushARInstructionBean.class,
                appContext.getBean(PushARInstructionInterpreter.class));
        statementInterpreterContainer.registerInterpreter(ExternalMethodCallInstructionBean.class,
                appContext.getBean(ExternalMethodCallInterpreter.class));

        externalMethodRepository.clear();
        externalMethodRepository.registerExecuter(BuiltInMethodExecutor.KEY,
                new BuiltInMethodExecutor());
    }

    protected void generateFirstState(RILModel transformedRILModel, Pair<RebecaModel, SymbolTable> model) {

        RebecaModel rebecaModel = model.getFirst();

        State<? extends BaseActorState> initialState = createInitialStates(rebecaModel);

        List<MainRebecDefinition> mainRebecDefinitions = ObjectModelUtils.getMainRebecDefinition(rebecaModel);

        setInitialKnownRebecsOfActors(initialState, mainRebecDefinitions);

        callConstructorsOfActors(transformedRILModel, initialState, mainRebecDefinitions);

        stateSpace.addInitialState(initialState);
    }

    private void callConstructorsOfActors(
            RILModel transformedRILModel,
            State<? extends BaseActorState> initialState,
            List<MainRebecDefinition> mainRebecDefinitions) {
        ArrayList<InstructionBean> mainInstructions =
                transformedRILModel.getInstructionList("main");
        int cnt = 1;
        for (MainRebecDefinition definition : mainRebecDefinitions) {
            ReactiveClassDeclaration metaData;
            try {
                metaData = (ReactiveClassDeclaration) typeSystem.getMetaData(definition.getType());
                ConstructorDeclaration constructorDeclaration = metaData.getConstructors().get(0);
                String computedConstructorName = RILUtilities.computeMethodName(metaData, constructorDeclaration);
                BaseActorState actorState = initialState.getActorState(definition.getName());
                actorState.pushInScopeStackForMethodCallInitialization(definition.getType().getTypeName());
                RebecInstantiationInstructionBean riib = (RebecInstantiationInstructionBean) mainInstructions.get(cnt++);
                for(Map.Entry<String, Object> constructorParameter : riib.getConstructorParameters().entrySet()) {
                    actorState.addVariableToRecentScope(
                            constructorParameter.getKey(), constructorParameter.getValue());
                }
                actorState.initializePC(computedConstructorName, 0);
                ProgramCounter pc = actorState.getPC();
                ArrayList<InstructionBean> instructionsList =
                        transformedRILModel.getInstructionList(pc.getMethodName());
                while (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
                    InstructionBean ib = instructionsList.get(pc.getLineNumber());
                    statementInterpreterContainer.retrieveInterpreter(ib).interpret(
                            ib, actorState, initialState);
                }
            } catch (CodeCompilationException e) {
                e.printStackTrace();
            }
        }
    }

    private void setInitialKnownRebecsOfActors(State<? extends BaseActorState> initialState,
                                               List<MainRebecDefinition> mainRebecDefinitions) {

        for (MainRebecDefinition definition : mainRebecDefinitions) {
            BaseActorState actorState = initialState.getActorState(definition.getName());
            LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy =
                    extractActorDeclarationHierarchy(definition);
            ActorScopeStack actorScopeStack = actorState.getActorScopeStack();
            List<Expression> bindings = definition.getBindings();
            int cnt = 0;
            int bindingCounter = 0;
            for(ReactiveClassDeclaration rcd : actorDeclarationHierarchy) {
                for (FieldDeclaration fieldDeclaration : rcd.getKnownRebecs()) {
                    for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
                        Expression knownRebecDefExp = bindings.get(bindingCounter++);
                        BaseActorState knownRebecActorState =
                                extractActorStateBasedOnTheRebecName(
                                        initialState, knownRebecDefExp);
                        actorScopeStack.addVariable(variableDeclator.getVariableName(),
                                knownRebecActorState, cnt);
                    }
                }
                cnt++;
            }
        }
    }

    protected BaseActorState extractActorStateBasedOnTheRebecName(
            State<? extends BaseActorState> initialState, Expression knownRebecDefExp) {
        if (!(knownRebecDefExp instanceof TermPrimary))
            throw new RebecaRuntimeInterpreterException("not handled yet!");
        String knownRebecName = ((TermPrimary) knownRebecDefExp).getName();
        return initialState.getActorState(knownRebecName);
    }

    protected BaseActorState createFreshActorState() {
        BaseActorState actorState = createInitialActorState();
        actorState.setTypeSystem(typeSystem);
        return actorState;
    }

    protected LinkedList<ReactiveClassDeclaration> extractActorDeclarationHierarchy(MainRebecDefinition definition) {
        LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy = new LinkedList<ReactiveClassDeclaration>();
        Type type = definition.getType();
        while (type != null) {
            ReactiveClassDeclaration metaData;
            try {
                metaData = (ReactiveClassDeclaration) typeSystem.getMetaData(type);
            } catch (CodeCompilationException e) {
                System.err.println("This exception should not happen!");
                e.printStackTrace();
                break;
            }
            actorDeclarationHierarchy.addFirst(metaData);
            type = metaData.getExtends();
        }
        return actorDeclarationHierarchy;
    }

    protected void addRequiredScopeToScopeStack(BaseActorState baseActorState, LinkedList<ReactiveClassDeclaration> actorDeclarationHierarchy) {
        for (ReactiveClassDeclaration reactiveClassDeclaration : actorDeclarationHierarchy) {
            baseActorState.pushInScopeStackForInheritanceStack(reactiveClassDeclaration.getName());

            // "self" must be added to all activation records of hierarchy
            // to have "self" in the scope of parent method calls
            baseActorState.addVariableToRecentScope("self", baseActorState);
            addStateVarsToRelatedScope(baseActorState, reactiveClassDeclaration);
        }
    }

    protected void addStateVarsToRelatedScope(BaseActorState baseActorState, ReactiveClassDeclaration reactiveClassDeclaration) {
        for (FieldDeclaration fieldDeclaration : reactiveClassDeclaration.getStatevars()) {
            for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
                baseActorState.addVariableToRecentScope(variableDeclator.getVariableName(),
                        retrieveDefaultValue(fieldDeclaration.getType()));
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
            executingMessageName = baseActorState.getMessage().getMessageName();
            executingMessageName += " [START,";

        }

        if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
            ProgramCounter pc = baseActorState.getPC();
            executingMessageName += pc.getLineNumber() + "]";
        } else {
            executingMessageName += "END]";

        }
        return baseActorState.getName() + "." + executingMessageName;
    }

    private Object retrieveDefaultValue(Type type) {
        if(type == CoreRebecaTypeSystem.BYTE_TYPE ||
                type == CoreRebecaTypeSystem.SHORT_TYPE ||
                type == CoreRebecaTypeSystem.INT_TYPE)
            return 0;
        if(type == CoreRebecaTypeSystem.FLOAT_TYPE ||
                type == CoreRebecaTypeSystem.DOUBLE_TYPE)
            return 0.0;
        if(type == CoreRebecaTypeSystem.BOOLEAN_TYPE)
            return false;
        if(type == CoreRebecaTypeSystem.STRING_TYPE)
            return "";

        return null;
    }
}