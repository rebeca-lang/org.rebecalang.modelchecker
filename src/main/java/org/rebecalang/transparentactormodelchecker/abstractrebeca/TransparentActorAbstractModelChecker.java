package org.rebecalang.transparentactormodelchecker.abstractrebeca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystem;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelNetworkDeliveryRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.StateGenerationUtils;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.springframework.stereotype.Component;

@Component
public abstract class TransparentActorAbstractModelChecker<T extends AbstractSystemState> {

	protected HashMap<String, String> methodLookupTable;
	protected Pair<RebecaModel,SymbolTable> compiledRebecaFile;
	protected RILModel rilModel;
	
	protected TransparentActorTransitionSystem<T> transitionSystem;

	protected abstract CompositionLevelExecuteStatementRule getCompositionLevelExecuteStatementRule();
	
	protected abstract CompositionLevelNetworkDeliveryRule getCompositionLevelNetworkDeliveryRule();

	//	@Autowired
//	protected CompositionLevelExecuteStatementRule compositionLevelExecuteStatementSOSRule;
//	@Autowired
//	protected CoreRebecaCompositionLevelNetworkDeliverySOSRule compositionLevelNetworkDeliverySOSRule;

	protected abstract T createSystemState();

	protected abstract AbstractActorState createAbstractActorState();
	
	protected void setInitialState() {
		ActivationRecord environment = 
				StateGenerationUtils.getEnvironment(compiledRebecaFile.getFirst());		
		
		T systemState = createSystemState();
		systemState.setEnvironment(environment);
		
		TransparentActorTransitionSystemState<T> initialState = 
				new TransparentActorTransitionSystemState<T>(0);
		initialState.setState(systemState);
		initialState.setNextStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		initialState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemTransition>());
		
		initializeMethodBindingTable();
		
		executeMainBody(initialState, environment);
		transitionSystem.setInitialState(initialState);
	}

	@SuppressWarnings("unchecked")
	private void executeMainBody(TransparentActorTransitionSystemState<T> initialState, ActivationRecord environment) {
		T state = initialState.getState();
		
		AbstractActorState tempState = createAbstractActorState();
		tempState.setRILModel(rilModel);
	
		ArrayList<InstructionBean> instructions = rilModel.getInstructionList("main");
		InstructionBean lastStatement = instructions.remove(instructions.size() - 2);
	
		tempState.addVariableToScope(AbstractActorState.PC, 
				new Pair<String, Integer>("main", 0));
		tempState.setEnvironment(environment);
		state.setActorState(tempState);
		tempState.setPriority(Integer.MAX_VALUE);
		
		
		CompositionLevelExecuteStatementRule compositionLevelExecuteStatementRule = getCompositionLevelExecuteStatementRule();
		CompositionLevelNetworkDeliveryRule compositionLevelNetworkDeliveryRule = getCompositionLevelNetworkDeliveryRule();
		
		try {
			while(true) { 
				T backup = (T) state.clone();
				compositionLevelExecuteStatementRule.applyRule(backup, state);
			}
		} catch(RuleIsDisabledException exception) {}
		try {
			while(true) {
				T backup = (T) state.clone();
				compositionLevelNetworkDeliveryRule.applyRule((AbstractSystemState)backup, (AbstractSystemState)state);
			}
		} catch(RuleIsDisabledException exception) {}
		
		state.destroyActorState(tempState);
		
		instructions.add(instructions.size() - 1, lastStatement);
	}

	private void initializeMethodBindingTable() {
		RebecaModel rebecaModel = compiledRebecaFile.getFirst();
		
		methodLookupTable = new HashMap<String, String>();
		List<ReactiveClassDeclaration> rcds = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for(ReactiveClassDeclaration rcd : rcds) {
			for(MethodDeclaration md : rcd.getSynchMethods()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
			for(MethodDeclaration md : rcd.getMsgsrvs()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
			for(MethodDeclaration md : rcd.getConstructors()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
		}
		CompositionLevelExecuteStatementRule compositionLevelExecuteStatementRule = getCompositionLevelExecuteStatementRule();
		compositionLevelExecuteStatementRule.setMethodLookupTable(methodLookupTable);
	}

	public abstract TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel, Set<Feature> features);

}