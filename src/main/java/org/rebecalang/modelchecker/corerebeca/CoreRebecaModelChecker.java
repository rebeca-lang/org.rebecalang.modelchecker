package org.rebecalang.modelchecker.corerebeca;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.RebecaCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modelchecker.Main;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.ExternalMethodRepository;
import org.rebecalang.modelchecker.corerebeca.builtinmethod.IndependentMethodExecutor;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.policy.FineGrainedPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.AssignmentInstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.CallMsgSrvInstructionInterpreter;
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
import org.rebecalang.modelchecker.corerebeca.policy.CoarseGrainedPolicy;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.Rebeca2RILTransformer;
import org.rebecalang.modeltransformer.ril.StatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;

import com.rits.cloning.Cloner;

public class CoreRebecaModelChecker {

	protected Hashtable<Long, State> statespace;
	protected Hashtable<String, ArrayList<InstructionBean>> transformedRILModel;
	private Set<CompilerFeature> compilerFeatures;
	private File rebecaFile;
	protected AbstractPolicy modelCheckingPolicy;

	public final static String FINE_GRAINED_POLICY = "fine";
	public final static String COARSE_GRAINED_POLICY = "coarse";
	private Cloner cloner;

	public CoreRebecaModelChecker(Set<CompilerFeature> compilerFeatures, File rebecaFile) {
		this.statespace = new Hashtable<Long, State>();
		this.transformedRILModel = new Hashtable<String, ArrayList<InstructionBean>>();
		this.compilerFeatures = compilerFeatures;
		this.rebecaFile = rebecaFile;
		this.cloner = new Cloner();

	}

	public Hashtable<Long, State> getStatespace() {
		return statespace;
	}
	
	public void modelCheck() throws ExceptionContainer, ModelCheckingException {
		RebecaCompiler rebecaCompiler = new RebecaCompiler();
		Pair<RebecaModel, SymbolTable> compilationResult = rebecaCompiler.compileRebecaFile(rebecaFile,
				compilerFeatures);

		ExceptionContainer exceptionContainer = rebecaCompiler.getExceptionContainer();
		if (!exceptionContainer.exceptionsIsEmpty()) {
			throw exceptionContainer;
		}

		StatementTranslatorContainer.getInstance().setSymbolTable(compilationResult.getSecond());
		StatementContainer.getInstance().registerSymbolTable(compilationResult.getSecond());
		Rebeca2RILTransformer rilTransformer = new Rebeca2RILTransformer();
		rilTransformer.transformModel(compilationResult, compilerFeatures);
		transformedRILModel = rilTransformer.getTransformedRILModel();
		initializeStatementInterpreterContainer();
		generateFirstState(compilationResult.getFirst());
		doFineGrainedModelChecking();
	}

	protected void generateFirstState(RebecaModel model) {

		State initialState = createFreshState();
		List<MainRebecDefinition> mainRebecDefinitions = model.getRebecaCode().getMainDeclaration()
				.getMainRebecDefinition();
		generateInitialActorStates(initialState, mainRebecDefinitions);

		setInitialKnownRebecsOfActors(initialState, mainRebecDefinitions);

		callConstructorsOfActors(initialState, mainRebecDefinitions);

		statespace.put(new Long(initialState.hashCode()), initialState);
	}

	protected State createFreshState() {
		return new State();
	}

	private void callConstructorsOfActors(State initialState, List<MainRebecDefinition> mainRebecDefinitions) {
		for (MainRebecDefinition definition : mainRebecDefinitions) {
			ReactiveClassDeclaration metaData;
			try {
				metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(definition.getType());
				ConstructorDeclaration constructorDeclaration = metaData.getConstructors().get(0);
				String computedConstructorName = RILUtilities.computeMethodName(metaData, constructorDeclaration);
				ActorState actorState = initialState.getActorState(definition.getName());
				actorState.pushInActorScope();
				actorState.initializePC(computedConstructorName, 0);
				while (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
					ProgramCounter pc = actorState.getPC();
					InstructionBean ib = transformedRILModel.get(pc.getMethodName()).get(pc.getLineNumber());
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
				metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(definition.getType());
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
			ActorState actorState = createFreshActorState();
			actorState.initializeScopeStack();
			actorState.pushInActorScope();
			ReactiveClassDeclaration metaData;
			try {
				metaData = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(definition.getType());
			} catch (CodeCompilationException e) {
				System.err.println("This exception should not happen!");
				e.printStackTrace();
				return;
			}
			for (FieldDeclaration fieldDeclaration : metaData.getStatevars()) {
				for (VariableDeclarator variableDeclator : fieldDeclaration.getVariableDeclarators()) {
					actorState.addVariableToRecentScope(variableDeclator.getVariableName(), 0);
				}
			}
			actorState.addVariableToRecentScope("self", actorState);

			actorState.setTypeName(TypesUtilities.getTypeName(definition.getType()));
			actorState.setQueue(new LinkedList<MessageSpecification>());
			actorState.setName(definition.getName());
			initialState.putActorState(definition.getName(), actorState);
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

	protected void doFineGrainedModelChecking() throws ModelCheckingException {
		int stateCounter = 1;
		State initialState = statespace.get(statespace.keySet().iterator().next());
		LinkedList<State> nextStatesQueue = new LinkedList<State>();
		nextStatesQueue.add(initialState);
		while (!nextStatesQueue.isEmpty()) {
			State currentState = nextStatesQueue.pollFirst();
			List<ActorState> enabledActors = currentState.getEnabledActors();
			if (enabledActors.isEmpty())
				throw new ModelCheckingException("Deadlock");
			for (ActorState actorState : enabledActors) {

				State newState = cloneState(currentState);

				ActorState newActorState = newState.getActorState(actorState.getName());
				newActorState.execute(newState, transformedRILModel, modelCheckingPolicy);
				String transitionLabel = calculateTransitionLabel(actorState, newActorState);
				Long stateKey = new Long(newState.hashCode());

				if (!statespace.containsKey(stateKey)) {
					newState.setId(stateCounter++);
					nextStatesQueue.add(newState);
					statespace.put(stateKey, newState);
					newState.clearLinks();
					currentState.addChildState(transitionLabel, newState);
					newState.addParentState(transitionLabel, currentState);
				} else {
					State repeatedState = statespace.get(stateKey);
					currentState.addChildState(transitionLabel, repeatedState);
					repeatedState.addParentState(transitionLabel, currentState);
				}
			}
		}
		Main.printStateSpace(initialState);
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