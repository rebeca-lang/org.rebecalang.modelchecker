package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.FileUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class CoreRebecaModelsTest {

	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/transparentactormodelchecker/"; 

	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    protected TransparentActorCoreRebecaFineGrainedDFSModelChecker fineGrainedDFSModelChecker;

//    @Autowired
//    protected TransparentActorCoreRebecaFineGrainedBFSModelChecker fineGrainedBFSModelChecker;
//    
    @Autowired
    protected TransparentActorCoreRebecaCoarseGrainedDFSModelChecker coarseGrainedDFSModelChecker;

	@Test
    public void GIVEN_SELF_LOOP_RebecaModel_WHEN_No_Error() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass Test(3) {
						knownrebecs {}
						statevars{int t;}
						Test() {
							t = 4;
							self.m1(self, t);
						}
						msgsrv m1(Test t1, int value) {
							t1.m1(self, value);
						}
					}
					main {
						Test t1():();
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = coarseGrainedDFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(1, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getCollisions() + ")");

	}
	
	@Test
    public void GIVEN_RebecaModel_WHEN_No_Error() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass Ping(3) {
						knownrebecs {
							Pong po;
						}
						statevars{int t;}
						Ping() {
							self.ping();
						}
						Ping(int i) {
							self.ping();
						}
					
						msgsrv ping() {
							po.pong();
						}
					}
					reactiveclass Pong(3) {
						knownrebecs {
							Ping pi;
						}
						Pong(int j) {
							//pong();
						}
						msgsrv pong() {
							pi.ping();
						}
					}
					
					main {
						Ping pi(po):(4 + 2);
						Pong po(pi):(3);
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = fineGrainedDFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(29, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getCollisions() + ")");

	}

	@ParameterizedTest
	@MethodSource("philosophers")
	public void GIVEN_DiningPhilosopherModel_WHEN_No_Error(String model, int statespaceSize) throws ModelCheckingException, IOException {
		File rebecaFile = FileUtils.createTempFile(model);
		
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
        

//		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = coarseGrainedDFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(statespaceSize, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getCollisions() + ")");
	}
	
	private void printRILModel(RILModel transformedRILModel) {
		for(String methodName : transformedRILModel.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : transformedRILModel.getInstructionList(methodName)) {
				System.out.println("" + counter++ +":" + instruction);
			}
			System.out.println("...............................................");
		}
	}
	
	protected static Stream<Arguments> philosophers() {
	    return Stream.of(
	    		Arguments.arguments(DiningPhilosophersSourceCodes.TWO_PHILOSOPHERS, 105)
	    		, Arguments.arguments(DiningPhilosophersSourceCodes.THREE_PHILOSOPHERS, 1471)
	    		, Arguments.arguments(DiningPhilosophersSourceCodes.FOUR_PHILOSOPHERS, 18053)
//	    		, Arguments.arguments(DiningPhilosophersSourceCodes.FIVE_PHILOSOPHERS, 214108)
	    );
	}
}
