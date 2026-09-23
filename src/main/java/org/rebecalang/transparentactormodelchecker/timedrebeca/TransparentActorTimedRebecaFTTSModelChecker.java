package org.rebecalang.transparentactormodelchecker.timedrebeca;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
	private Logger logger;
	
	public TransparentActorTimedRebecaFTTSModelChecker() {
		logger = LogManager.getRootLogger();
	}


	private StringBuilder toStringRILModel(RILModel transformedRILModel) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("RIL-MODEL\n========================================================\n");
		for(String methodName : transformedRILModel.getMethodNames()) {
			stringBuilder.append(methodName + "\n");
			int counter = 0;
			for(InstructionBean instruction : transformedRILModel.getInstructionList(methodName)) {
				stringBuilder.append("" + counter++ +":" + instruction + "\n");
			}
			stringBuilder.append("...............................................\n");
		}
		return stringBuilder;
	}

	@Override
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel, SymbolTable> compiledRebecaFile,
			RILModel rilModel, Set<Feature> features) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new TimedRebecaTransitionSystem();
		if(features.contains(Feature.CompleteTransitionSystem))
			this.completeTransitionSystem = true;
		
		logger.debug("{}", toStringRILModel(rilModel));

		setInitialState();
		
		
		TransparentActorTransitionSystemState<TimedRebecaSystemState> initialState = 
				transitionSystem.getInitialState();
		logger.debug("The initial state is: {}", initialState.getState());
//		long time = System.currentTimeMillis();
//		for(int cnt = 0; cnt < 100000; cnt++)
//			logger.debug("State is {}", initialState.getState());
//			initialState.getState().toString();
//		System.out.println(System.currentTimeMillis() - time);
		
		try {
			dfs(initialState);
		} catch (ModelCheckingException e) {
			// only the search reaching a state with no applicable rule is reported as a
			// result; anything else is a defect in the checker and must not be returned
			// as though it were an answer
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

		if(state.getId() > 99) {
			state.getId();
		}
		try {
			transitions = takeMessageSOSRule.applyRule(state.getState(), systemState);
			for(int cnt = 0; cnt < transitions.size(); cnt++) {
				systemState = (TimedRebecaSystemState) transitions.getDestinationsStates().get(cnt);
				logger.debug("Taking action {} resulted in the state: {}", 
						transitions.getDestinationsActions().get(cnt), systemState);
				Action action = transitions.getDestinationsActions().get(cnt);
				List<AbstractSystemState> destinations = new ArrayList<AbstractSystemState>();
				destinations.addAll(courseGraindExecuteMessageServer(systemState));
				deliverAllMessagesAndExpand(state, destinations, action);
			}
		} catch (RuleIsDisabledException e) {
			if(completeTransitionSystem)
				return;
			else
				throw new ModelCheckingException(state);
		}
		
	}

	private void deliverAllMessagesAndExpand(TransparentActorTransitionSystemState<TimedRebecaSystemState> state,
			List<AbstractSystemState> destinations, Action action) throws ModelCheckingException, RuleIsDisabledException {
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
			logger.info("S{} -> S{} [label=\"{}@{}\"]", state.getId(), 
					result.getSecond().getId(), action.getActionLabel(), 
					state.getState().getEnablingTime());
//			outputStatespace.
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
		
		List<Type> delayMethodInputType = new ArrayList<Type>();
		delayMethodInputType.add(CoreRebecaTypeSystem.INT_TYPE);
		ArrayList<InstructionBean> delayMehodBody = new ArrayList<InstructionBean>();
		delayMehodBody.add(new PushARInstructionBean());
		delayMehodBody.add(new AssignmentInstructionBean(new Variable("now"), new Variable("now"), new Variable("arg0"), "+"));
		delayMehodBody.add(new PopARInstructionBean());
		delayMehodBody.add(new EndMethodInstructionBean());
		// computeMethodName renders a null class name as the literal "null.", while the
		// RIL emits a base-less call as plain "delay$int". Keep only the part after the
		// separator so the registered name is the one the instruction actually asks for.
		String canonicalName = RILUtilities.computeMethodName(
				null, "delay", delayMethodInputType);
		String methodName = canonicalName.substring(canonicalName.indexOf('.') + 1);
		methodLookup.addMethod(methodName, methodName);
		this.rilModel.addMethod(methodName, delayMehodBody);
	}

}
