package org.rebecalang.transparentactormodelchecker.timedrebeca;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PushARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.ModelCheckingException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.TransparentActorAbstractModelChecker;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelNetworkDeliveryRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.compositionlevel.TimedRebecaCompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.TimedRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("TIMED_REBECA")
public class TransparentActorTimedRebecaFTTSModelChecker extends TransparentActorAbstractModelChecker<TimedRebecaSystemState> {

	@Autowired
	protected TimedRebecaCompositionLevelTakeMessageRule takeMessageSOSRule;

	@Autowired
	@Qualifier("TIMED_REBECA")
	CompositionLevelExecuteStatementRule executeStatementRule;
	
	@Autowired
	@Qualifier("TIMED_REBECA")
	CompositionLevelNetworkDeliveryRule networkDeliveryRule;
	
	private boolean completeTransitionSystem;

	@Override
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel, SymbolTable> compiledRebecaFile,
			RILModel rilModel, Set<Feature> features) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new TimedRebecaTransitionSystem();
		if(features.contains(Feature.CompleteTransitionSystem))
			this.completeTransitionSystem = true;
		
		setInitialState();
		
		TransparentActorTransitionSystemState<TimedRebecaSystemState> initialState = 
				transitionSystem.getInitialState();
		try {
			dfs(initialState);
		} catch (Exception e) {
			e.printStackTrace();;
			TransparentActorModelCheckingResult result = 
					new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.INTERNAL_ERROR);
			result.setTransitionSystem(transitionSystem);
			return result;
		}

		TransparentActorModelCheckingResult result = 
				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
		result.setTransitionSystem(transitionSystem);
		return result;
	}

	private void dfs(TransparentActorTransitionSystemState<TimedRebecaSystemState> state) throws ModelCheckingException {
		TimedRebecaSystemState systemState = state.getState().clone();
		Transition<AbstractSystemState> transitions = null;

		try {
			transitions = takeMessageSOSRule.applyRule(state.getState(), systemState);
			for(int cnt = 0; cnt < transitions.size(); cnt++) {
				systemState = (TimedRebecaSystemState) transitions.getDestinationsStates().get(cnt);
				Action action = transitions.getDestinationsActions().get(cnt);
				List<AbstractSystemState> destinations = new ArrayList<AbstractSystemState>();
				destinations.addAll(courseGraindExecuteMessageServer(systemState));
				System.out.println(action.getActionLabel());
				deliverAllMessagesAndExpand(state, destinations);
			}
		} catch (RuleIsDisabledException e) {
			if(completeTransitionSystem)
				return;
			else
				throw new ModelCheckingException(state);
		}
		
	}

	private void deliverAllMessagesAndExpand(TransparentActorTransitionSystemState<TimedRebecaSystemState> state,
			List<AbstractSystemState> destinations) throws ModelCheckingException, RuleIsDisabledException {
		TimedRebecaSystemState systemState;
		for(int stateCounter = 0; stateCounter < destinations.size(); stateCounter++) {
			systemState = (TimedRebecaSystemState) destinations.get(stateCounter);
			try {
				while(true) {
					networkDeliveryRule.applyRule(systemState, systemState);
				}
			} catch (RuleIsDisabledException exception) {}
			int enablingTime = systemState.getEnablingTime();
			for(int actorId : systemState.getActorsIds()) {
				AbstractActorState actorState = systemState.getActorState(actorId);
				int now = (int) actorState.getVariableValue(TimedActorScope.TIME_VARIABLE);
				if(now < enablingTime)
					actorState.setVariableValue(TimedActorScope.TIME_VARIABLE, enablingTime);
			}

			Pair<Boolean, TransparentActorTransitionSystemState<TimedRebecaSystemState>> result = 
					transitionSystem.addIfNotExists(state, systemState);
			if(result.getSecond().getId() == 18)
				result.getSecond().getId();
			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + "\"]\n");//action.getActionLabel() +"\"]\n");
			if(result.getFirst())
				dfs(result.getSecond());
		}
	}

	protected List<AbstractSystemState> courseGraindExecuteMessageServer(TimedRebecaSystemState systemState) {
		List<AbstractSystemState> destinations = new ArrayList<AbstractSystemState>();
		destinations.add(systemState);
		try {
			List<AbstractSystemState> nextRoundNewDestinations = new ArrayList<AbstractSystemState>();
			while(true) {
				nextRoundNewDestinations.clear();
				for(int stateCounter = 0; stateCounter < destinations.size(); stateCounter++) {
					systemState = (TimedRebecaSystemState) destinations.get(stateCounter);
					List<AbstractSystemState> newDestinations = 
							executeStatementRule.applyRule(systemState, systemState).getDestinationsStates();
					for(int cnt2 = 1; cnt2 < newDestinations.size(); cnt2++)
						nextRoundNewDestinations.add(newDestinations.get(cnt2));						
				}
				destinations.addAll(nextRoundNewDestinations);
			}
		} catch (RuleIsDisabledException exception) {}
		return destinations;
	}
	
	@Override
	protected TimedRebecaSystemState createSystemState() {
		return new TimedRebecaSystemState();
	}

	@Override
	protected CompositionLevelExecuteStatementRule getCompositionLevelExecuteStatementRule() {
		return executeStatementRule;
	}

	@Override
	protected CompositionLevelNetworkDeliveryRule getCompositionLevelNetworkDeliveryRule() {
		return networkDeliveryRule;
	}

	@Override
	protected AbstractActorState createAbstractActorState() {
		return new TimedRebecaActorState(AbstractActorState.NO_ACTOR_ID);
	}

	@Override
	protected void initializeMethodBindingTable() {
		super.initializeMethodBindingTable();
//		RebecaModel rebecaModel = compiledRebecaFile.getFirst();
		
//		List<ReactiveClassDeclaration> rcds = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		List<Type> delayMethodInputType = new ArrayList<Type>();
		delayMethodInputType.add(CoreRebecaTypeSystem.INT_TYPE);
		ArrayList<InstructionBean> delayMehodBody = new ArrayList<InstructionBean>();
		delayMehodBody.add(new PushARInstructionBean());
		delayMehodBody.add(new AssignmentInstructionBean(new Variable("now"), new Variable("now"), new Variable("arg0"), "+"));
		delayMehodBody.add(new PopARInstructionBean());
		delayMehodBody.add(new EndMethodInstructionBean());
		String methodName = RILUtilities.computeMethodName(
				null, "delay", delayMethodInputType);
		methodLookup.addMethod(methodName, methodName);
		this.rilModel.addMethod(methodName, delayMehodBody);
	}

//	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
//		this.compiledRebecaFile = compiledRebecaFile;
//		this.rilModel = rilModel;
//		transitionSystem = new TransparentActorCoreRebecaTransitionSystem();
//		setInitialState();
//		
//		TransparentActorCoreRebecaTransitionSystemState initialState = 
//				transitionSystem.getInitialState();
//
//		try {
//			dfs(initialState);
//		} catch (RuleIsDisabledException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		TransparentActorModelCheckingResult result = 
//				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
//		result.setTransitionSystem(transitionSystem);
//		return result;
//	}
//
//	private void dfs(TransparentActorCoreRebecaTransitionSystemState state) throws RuleIsDisabledException {
//		NondeterministicTransition<CoreRebecaSystemState> transitions = 
//				(NondeterministicTransition<CoreRebecaSystemState>) compositionLevelTakeMessageSOSRule.applyRule(
//						RebecaStateSerializationUtils.clone(state.getState()));
//		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
//				transitions.getDestinations();
//		for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
//			CoreRebecaSystemState newState = destination.getSecond();
//			try {
//				while (true) {
//					NondeterministicTransition<CoreRebecaSystemState> stmtExecResult = 
//							compositionLevelExecuteStatementSOSRule.applyRule(newState);
//				}
//			} catch(RuleIsDisabledException exception) {}
//			try {
//				while(true) {
//					compositionLevelNetworkDeliverySOSRule.applyRule(newState);
//				}
//			} catch(RuleIsDisabledException exception) {}
//
//			Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState> result = 
//					transitionSystem.addIfNotExists(state, destination.getSecond());
////			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
////			System.out.println(result.getSecond());
////			System.out.println("........................");
//			if(result.getFirst())
//				dfs(result.getSecond());
//		}
//	}
}
