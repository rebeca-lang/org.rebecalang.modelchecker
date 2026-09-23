package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ReturnInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.MethodLookup;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MethodCallAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.AssignmentRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ConditionalRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.EndMSGSrvRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.EndMethodCallRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.MethodCallRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.PopRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.PushRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.RebecInstantiationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ReturnRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.SendMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.VariableDeclarationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorStateRepresentor;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorsContainer;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class StatementsSOSRulesTest {
	
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
	protected GenericApplicationContext appContext;
	
    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    protected AssignmentRule assignmentSOSRule;

    @Autowired
    protected ReturnRule returnSOSRule;

    @Autowired
    protected RebecInstantiationRule rebecInstantiationSOSRule;
    
    @Autowired
    protected VariableDeclarationRule variableDeclarationSOSRule;
    
    @Autowired
    protected ConditionalRule conditionalSOSRule;

    @Autowired
    protected PushRule pushSOSRule;

    @Autowired
    protected PopRule popSOSRule;

    @Autowired
    protected MethodCallRule methodCallSOSRule;

    @Autowired
    protected EndMethodCallRule endMethodCallSOSRule;

    @Autowired
    protected EndMSGSrvRule endMsgSrvSOSRule;

    // SendMessageRule is the only statement-level rule without @Component, so it is
    // not a Spring bean and has to be constructed directly.
    protected SendMessageRule sendMessageSOSRule = new SendMessageRule();

    protected CoreRebecaTypeSystem typeSystem;
    
    CoreRebecaActorState coreRebecaActorState;
    
    @BeforeEach
    public void setup() throws CodeCompilationException {
    	coreRebecaActorState = new CoreRebecaActorState(0);
    	ActivationRecord environment = new ActivationRecord();
    	ActorsContainer actorsContainer = new ActorsContainer();
    	coreRebecaActorState.setEnvironment(environment);
    	actorsContainer.setActor(0, coreRebecaActorState);
    	environment.setVariableValue(ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME,
    			actorsContainer);

    	typeSystem = new CoreRebecaTypeSystem();
    	typeSystem.clear();
    	ReactiveClassDeclaration rcd = new ReactiveClassDeclaration();
    	rcd.setName("A");
    	typeSystem.addReactiveClassType(rcd);
    	CloningRepository.resetRepository();
    }

    @Test
    public void GIVEN_ActorStateIsEmpty_WHEN_DeclarationInstructionIsExecuted_THEN_ANewVariableIsAddedToTheState() {
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1",
    			CoreRebecaTypeSystem.INT_TYPE);
		variableDeclarationSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, dib);
    	
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(10, coreRebecaActorState.getVariableValue(v));
    }
    
    @Test
    public void GIVEN_ActorStateHasThreeVariables_WHEN_AssignmentInstructionIsExecuted_THEN_CalculatedValueHasToSetInTheState() {

    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope("var2", 2);
    	coreRebecaActorState.addVariableToScope("var3", 3);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

		Variable v1 = new Variable("var1");
		Variable v2 = new Variable("var2");
		Variable v3 = new Variable("var3");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, v2, v3, "-");
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), -1);
    }
    
    @Test
    public void GIVEN_ActorStateHasOneVariable_WHEN_ReturnInstructionIsExecuted_THEN_TheCorrectValueHasToBeStoredInTargetVariable() {

    	coreRebecaActorState.addVariableToScope("var1", 1);
		Variable v1 = new Variable("var1");
    	coreRebecaActorState.newCallPushToScope(v1);
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("var2", 12);
		Variable v2 = new Variable("var2");
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	ReturnInstructionBean rib = new ReturnInstructionBean(v2);
    	
		returnSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, rib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), 12);
    }
    
    @Test
    public void rebecInstantiationTest() throws CodeCompilationException {
    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
		Variable v1 = new Variable("var1");
		RebecInstantiationInstructionBean riib = new RebecInstantiationInstructionBean();
		riib.setType(typeSystem.getType("A"));
		riib.setResultTarget(v1);
		Transition<AbstractActorState> result = 
				rebecInstantiationSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, riib);
		Assertions.assertEquals(result.getDestinationsActions().get(0).getClass(), NewInstanceAction.class);

    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_AssignmentInstructionIsExecutedAndAccessedBySelfKeyword_THEN_ValueHasToBeUpdated() {    	
    	
    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope("self", coreRebecaActorState);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
//    	coreRebecaActorState.setEnvironment(environment);
    	
    	Variable base = new Variable("self");
    	Variable v1 = new Variable(base, "var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, 10, null, null);
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), 10);
    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_ConditionalInstructionIsExecutedAndConditionIsTrue_THEN_NoJumpIsNeeded() {

    	coreRebecaActorState.addVariableToScope("var1", false);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Variable v1 = new Variable("var1");
    	JumpIfNotInstructionBean jinib = new JumpIfNotInstructionBean(v1, "m1", 1);
		conditionalSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, jinib);
		
		Pair<String, Integer> pc = (Pair<String, Integer>) coreRebecaActorState.getPC();
		Assertions.assertEquals(pc.getFirst(), "m1");
		Assertions.assertEquals(pc.getSecond(), 1);
    }
    
    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_NondetInstructionIsExecuted_THEN_ThreeResultStates() {
    	coreRebecaActorState.addVariableToScope("var1", 5);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	NonDetValue ndv = new NonDetValue();
    	ndv.addNonDetValue(3);
    	ndv.addNonDetValue(4);
    	Variable var1 = new Variable("var1");
		ndv.addNonDetValue(var1);
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(var1, ndv, null, null);
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, aib);
    	Transition<AbstractActorState> result = assignmentSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState, aib);
    	List<AbstractActorState> destinations = result.getDestinationsStates();
    	Iterator<AbstractActorState> iterator = destinations.iterator();
    	AbstractActorState first = iterator.next();
    	Assertions.assertEquals(3, first.getVariableValue(var1));
    	AbstractActorState second = iterator.next();
    	Assertions.assertEquals(4, second.getVariableValue(var1));
    	AbstractActorState third = iterator.next();
    	Assertions.assertEquals(5, third.getVariableValue(var1));
    	state.getFirst();
    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_PushInstructionIsExecuted_THEN_ANewFrameIsOpened() {
    	coreRebecaActorState.addVariableToScope("outer", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Transition<AbstractActorState> result = pushSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState);

    	coreRebecaActorState.addVariableToScope("inner", 2);
    	Assertions.assertTrue(coreRebecaActorState.hasVariableInScope("inner"));
    	coreRebecaActorState.popFromScope();
    	Assertions.assertFalse(coreRebecaActorState.hasVariableInScope("inner"));
    	Assertions.assertTrue(coreRebecaActorState.hasVariableInScope("outer"));

    	Assertions.assertEquals(1, coreRebecaActorState.getPC().getSecond());
    	Assertions.assertEquals(TauAction.TAU, result.getDestinationsActions().get(0));
    }

    @Test
    public void GIVEN_TwoNestedFrames_WHEN_PopInstructionRequestsTwoPops_THEN_BothFramesAreDropped() {
    	coreRebecaActorState.addVariableToScope("outer", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("first", 2);
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("second", 3);

    	PopARInstructionBean pib = new PopARInstructionBean(2, false);
    	popSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, pib);

    	Assertions.assertFalse(coreRebecaActorState.hasVariableInScope("second"));
    	Assertions.assertFalse(coreRebecaActorState.hasVariableInScope("first"));
    	Assertions.assertTrue(coreRebecaActorState.hasVariableInScope("outer"));
    	Assertions.assertEquals(1, coreRebecaActorState.getPC().getSecond());
    }

    @Test
    public void GIVEN_TwoNestedFrames_WHEN_PopInstructionRequestsOnePop_THEN_OnlyTheInnermostIsDropped() {
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("first", 2);
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("second", 3);

    	popSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState,
    			new PopARInstructionBean(1, false));

    	Assertions.assertFalse(coreRebecaActorState.hasVariableInScope("second"));
    	Assertions.assertTrue(coreRebecaActorState.hasVariableInScope("first"));
    }

    private MethodCallInstructionBean methodCall(String methodName, String resolvedName,
    		Map<String, Object> parameters, Variable result) {
    	MethodLookup methodLookup = new MethodLookup();
    	methodLookup.addMethod(methodName, resolvedName);
    	// the rule keeps a plain field for the lookup table, it is not injected
    	methodCallSOSRule.setMethodLookup(methodLookup);
    	MethodCallInstructionBean mcib =
    			new MethodCallInstructionBean(null, methodName, parameters);
    	mcib.setFunctionCallResult(result);
    	return mcib;
    }

    @Test
    public void GIVEN_AMethodWithALiteralArgument_WHEN_MethodCallIsExecuted_THEN_TheCalleeFrameIsEntered() {
    	coreRebecaActorState.addVariableToScope("res", 0);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("p", 5);

    	Transition<AbstractActorState> result = methodCallSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState,
    			methodCall("m", "A.m", parameters, new Variable("res")));

    	Assertions.assertEquals("A.m", coreRebecaActorState.getPC().getFirst());
    	Assertions.assertEquals(0, coreRebecaActorState.getPC().getSecond());
    	Assertions.assertEquals(5, coreRebecaActorState.getVariableValue(new Variable("p")));
    	Assertions.assertTrue(result.getDestinationsActions().get(0) instanceof MethodCallAction);
    }

    @Test
    public void GIVEN_AMethodWithAVariableArgument_WHEN_MethodCallIsExecuted_THEN_TheArgumentIsEvaluatedInTheCaller() {
    	coreRebecaActorState.addVariableToScope("x", 7);
    	coreRebecaActorState.addVariableToScope("res", 0);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("p", new Variable("x"));

    	methodCallSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState,
    			methodCall("m", "A.m", parameters, new Variable("res")));

    	Assertions.assertEquals(7, coreRebecaActorState.getVariableValue(new Variable("p")));
    }

    @Test
    public void GIVEN_ACalleeFrame_WHEN_EndMethodCallIsExecuted_THEN_TheCallersProgramCounterIsRestored() {
    	coreRebecaActorState.addVariableToScope("res", 0);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	methodCallSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState,
    			methodCall("m", "A.m", new HashMap<String, Object>(), new Variable("res")));
    	Assertions.assertEquals("A.m", coreRebecaActorState.getPC().getFirst());

    	Transition<AbstractActorState> result = endMethodCallSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState);

    	Assertions.assertEquals("-", coreRebecaActorState.getPC().getFirst());
    	Assertions.assertEquals(1, coreRebecaActorState.getPC().getSecond());
    	Assertions.assertEquals(TauAction.TAU, result.getDestinationsActions().get(0));
    }

    @Test
    public void GIVEN_AMessageServerFrame_WHEN_EndMsgSrvIsExecuted_THEN_TheFrameIsDropped() {
    	coreRebecaActorState.addVariableToScope("actorField", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("msgsrvLocal", 2);

    	Transition<AbstractActorState> result = endMsgSrvSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState);

    	Assertions.assertFalse(coreRebecaActorState.hasVariableInScope("msgsrvLocal"));
    	Assertions.assertTrue(coreRebecaActorState.hasVariableInScope("actorField"));
    	Assertions.assertEquals(TauAction.TAU, result.getDestinationsActions().get(0));
    }

    @Test
    public void GIVEN_AKnownPeer_WHEN_SendMessageIsExecuted_THEN_TheMessageIsAddressedToThatPeer() {
    	CoreRebecaActorState peer = new CoreRebecaActorState(1);
    	peer.setEnvironment(coreRebecaActorState.getEnvironment());
    	((ActorsContainer) coreRebecaActorState.getEnvironment().getVariableValue(
    			ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME)).setActor(1, peer);
    	coreRebecaActorState.addVariableToScope("peer", peer);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("v", 3);
    	MsgsrvCallInstructionBean bean =
    			new MsgsrvCallInstructionBean(new Variable("peer"), "ping", parameters);

    	Transition<AbstractActorState> result = sendMessageSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState, bean);

    	MessageAction action = (MessageAction) result.getDestinationsActions().get(0);
    	AbstractMessageState message = action.getMessage();
    	Assertions.assertEquals("ping", message.getName());
    	Assertions.assertEquals(0, message.getSenderId());
    	Assertions.assertEquals(1, message.getReceiverId());
    	Assertions.assertEquals(3, message.getParameters().get("v"));
    	Assertions.assertEquals(1, coreRebecaActorState.getPC().getSecond());
    }

    @Test
    public void GIVEN_NoReceiverIsNamed_WHEN_SendMessageIsExecuted_THEN_TheMessageIsSentToSelf() {
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	MsgsrvCallInstructionBean bean = new MsgsrvCallInstructionBean(
    			null, "tick", new HashMap<String, Object>());

    	Transition<AbstractActorState> result = sendMessageSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState, bean);

    	AbstractMessageState message = ((MessageAction) result.getDestinationsActions().get(0)).getMessage();
    	Assertions.assertEquals(coreRebecaActorState.getId(), message.getReceiverId());
    	Assertions.assertEquals(coreRebecaActorState.getId(), message.getSenderId());
    }

    @Test
    public void GIVEN_AnActorValuedArgument_WHEN_SendMessageIsExecuted_THEN_ItTravelsAsAnActorReference() {
    	CoreRebecaActorState peer = new CoreRebecaActorState(1);
    	peer.setEnvironment(coreRebecaActorState.getEnvironment());
    	((ActorsContainer) coreRebecaActorState.getEnvironment().getVariableValue(
    			ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME)).setActor(1, peer);
    	coreRebecaActorState.addVariableToScope("peer", peer);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("who", new Variable("peer"));
    	MsgsrvCallInstructionBean bean =
    			new MsgsrvCallInstructionBean(null, "notify", parameters);

    	Transition<AbstractActorState> result = sendMessageSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState, bean);

    	AbstractMessageState message = ((MessageAction) result.getDestinationsActions().get(0)).getMessage();
    	Assertions.assertTrue(message.getParameters().get("who") instanceof ActorStateRepresentor);
    	Assertions.assertEquals(1, ((ActorStateRepresentor) message.getParameters().get("who")).getActorID());
    }

//    @Configuration
//    @ComponentScan(basePackages = { 
//    		"org.rebecalang.transparentactormodelchecker.abstractrebeca", 
//    		"org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule", 
//    		"org.rebecalang.transparentactormodelchecker.timedrebeca.sos.statementlevelrule"
//    		})
//    public static class Config {
//    	
//    }
}