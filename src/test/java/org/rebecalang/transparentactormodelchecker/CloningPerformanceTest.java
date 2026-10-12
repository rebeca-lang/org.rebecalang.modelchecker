package org.rebecalang.transparentactormodelchecker;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.FileUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.corerebeca.TransparentActorCoreRebecaCoarseGrainedDFSModelChecker;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.CoreRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class CloningPerformanceTest {

    public final static int ACTOR_0_ID = 0;
    public final static int ACTOR_1_ID = 1;
    
    private final static int EXECUTION_NUMBER = 1000000;

	private final static String DINING_PHILOSOPHERS = """
			reactiveclass Philosopher(3) {
				knownrebecs {
					Chopstick chpL, chpR;
				}
				statevars {
					boolean eating;
				}
				Philosopher() { 
					eating = false;
					self.arrive();
				}
				msgsrv arrive() {
					chpL.request();
				}
				msgsrv permit() {
					if (sender == chpL) {
						chpR.request();
					} else {
						self.eat();
					}
				}
				msgsrv eat() {
					eating = true;
					self.leave();
				}
				msgsrv leave() {
					eating = false;
					chpL.release();
					chpR.release();
					self.arrive();
				}
			}
			
			reactiveclass Chopstick(3) {
				knownrebecs {
					Philosopher philL, philR;
				}
				statevars {
					boolean lAssign, rAssign, leftReq, rightReq;
				}
				Chopstick() {
					lAssign = false;
					rAssign = false;
					leftReq = false;
					rightReq = false;
				}
			
				msgsrv request() {
					if (sender == philL) {
						leftReq = true;
						if (!rAssign) {
							lAssign = true;
							philL.permit();
						}
					} else {
						rightReq = true;
						if (!lAssign) {
							rAssign = true;
							philR.permit();
						}
					}
				}
				msgsrv release() {
					if (sender == philL){
						leftReq = false;
						lAssign = false;
						if (rightReq) {
							rAssign=true;
							philR.permit();
						}
					}
					if (sender == philR){
						rAssign = false;
						rightReq = false;
						if (leftReq) {
							lAssign=true;
							philL.permit();
						}
					}
				}
			}
			main {
				Philosopher phil0(chp0, chp1):();
				Philosopher phil1(chp0, chp1):();
			
				Chopstick chp0(phil0, phil1):();
				Chopstick chp1(phil1, phil0):();
			}
			"""; 
    
    CoreRebecaSystemState systemState;
    
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    protected TransparentActorCoreRebecaCoarseGrainedDFSModelChecker coarseGrainedDFSModelChecker;
    
    public static CoreRebecaSystemState createEmptySystemState() {
    	ActivationRecord environment = new ActivationRecord();
		CoreRebecaSystemState systemState = new CoreRebecaSystemState();
    	systemState.setEnvironment(environment);
    	systemState.setNetworkState(new CoreRebecaNetworkState());
    	return systemState;
    }
   
    @BeforeEach
    public void setup() throws IOException {
    	
		File rebecaFile = FileUtils.createTempFile(DINING_PHILOSOPHERS);
		
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
		RebecaModel rebecaModel = compiledRebecaFile.getFirst();
        RILModel rilLModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
        
        ReactiveClassDeclaration rcd1 = rebecaModel.getRebecaCode().getReactiveClassDeclaration().get(0);
        ReactiveClassDeclaration rcd2 = rebecaModel.getRebecaCode().getReactiveClassDeclaration().get(1);

        systemState = createEmptySystemState();
    	CoreRebecaActorState actor0State = null;
    	
    	for(int cnt = 0; cnt < 4; cnt++) {
    		actor0State = new CoreRebecaActorState(cnt);
//        	actor0State.pushToScope();
        	actor0State.addFieldsVariablesToScope(rcd1.getStatevars());
//        	actor0State.pushToScope();
        	actor0State.addFieldsVariablesToScope(rcd1.getKnownRebecs());
    		systemState.setActorState(cnt, actor0State);    		
    	}
		
    	for(int cnt = 4; cnt < 8; cnt++) {
    		actor0State = new CoreRebecaActorState(cnt);
//        	actor0State.pushToScope();
        	actor0State.addFieldsVariablesToScope(rcd2.getStatevars());
//        	actor0State.pushToScope();
        	actor0State.addFieldsVariablesToScope(rcd2.getKnownRebecs());
    		systemState.setActorState(cnt, actor0State);    		
    	}
    	
//    	CoreRebecaActorState actor1State = new CoreRebecaActorState(ACTOR_1_ID);
//		systemState.setActorState(ACTOR_1_ID, actor1State);
//    	
//		actor0State.addVariableToScope("ac1", actor1State);
//		actor1State.addVariableToScope("ac0", actor0State);    	
    }
    
    @Test
    public void GIVEN_ASystemState_WHEN_cloning_THEN_LessThanOneMinute() {

		long start = System.currentTimeMillis();
		for(int cnt = 0; cnt < EXECUTION_NUMBER; cnt++) {
			CoreRebecaSystemState cloned = systemState.clone();
			cloned.getNetworkState();
		}
		
		System.out.println("Cloning : " + (System.currentTimeMillis() - start));
    }
    
    @Test
    public void GIVEN_ASystemState_WHEN_hashcode_THEN_LessThanOneMinute() {
		long start = System.currentTimeMillis();
		for(int cnt = 0; cnt < EXECUTION_NUMBER; cnt++) {
			systemState.hashCode();
		}
		
		System.out.println("Hashcode : " + (System.currentTimeMillis() - start));
    }

    @Test
    public void GIVEN_ASystemState_WHEN_equals_THEN_LessThanOneMinute() {
		long start = System.currentTimeMillis();
		for(int cnt = 0; cnt < EXECUTION_NUMBER; cnt++) {
			systemState.equals(systemState);
		}
		
		System.out.println("Equals : " + (System.currentTimeMillis() - start));
    }

}
