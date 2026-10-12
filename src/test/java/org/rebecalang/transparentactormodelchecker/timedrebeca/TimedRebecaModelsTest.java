package org.rebecalang.transparentactormodelchecker.timedrebeca;

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
public class TimedRebecaModelsTest {

	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/transparentactormodelchecker/"; 

	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    TransparentActorTimedRebecaFTTSModelChecker fttsModelChecker;

	@Test
    public void sendingAMessageWithActorParameter() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass A(3) {
						knownrebecs {}
						statevars{}
						A() {
							self.m1();
						}
						msgsrv m1() {
							self.m2(self);
						}
						msgsrv m2(A a) {
							a.m2((A)sender) after(1);
						}
					}
					main {
						A a():();
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			Assertions.assertTrue(false);
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(2, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getTransitionSystem().getCollisions() + ")");
	}
	@Test
    public void twoSentMessagesInConstruction() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass A(3) {
						knownrebecs {}
						statevars{}
						A() {
							self.m1();
							self.m2();
						}
						msgsrv m1() {
						}
						msgsrv m2() {
							self.m3() after(1);
						}
						msgsrv m3() {
							self.m3() after(1);
						}
					}
					main {
						A a():();
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			Assertions.assertTrue(false);
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(3, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getTransitionSystem().getCollisions() + ")");
	}
	@Test
    public void messagesSentFromConstrutorsHavePriorityOverOthers() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass A(3) {
						knownrebecs {}
						statevars{}
						A() {
							self.m1();
						}
						msgsrv m1() {
						}
					}
					reactiveclass B(3) {
						knownrebecs {A a;}
						statevars{}
						B() {
							self.m2();
						}
						msgsrv m2() {
							a.m1();
							self.m3() after(1);
						}
						msgsrv m3() {
							self.m3() after(1);
						}
					}
					main {
						A a():();
						B b(a):();
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			Assertions.assertTrue(false);
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(6, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getTransitionSystem().getCollisions() + ")");
	}
    
	@Test
    public void racingBetweenTwoSentMessagesAtTheSameTime() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass A(3) {
						knownrebecs {}
						statevars{}
						A() {
							self.m1();
						}
						msgsrv m1() {
							self.m1() after(1);
						}
					}
					reactiveclass B(3) {
						knownrebecs {A a;}
						statevars{}
						B() {
							self.m2();
						}
						msgsrv m2() {
							a.m1() after(1);;
						}
					}
					main {
						A a():();
						B b(a):();
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			Assertions.assertTrue(false);
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
        

		printRILModel(transformedRILModel);
		
		TransparentActorModelCheckingResult coarseDfsResult = fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
		Assertions.assertEquals(7, coarseDfsResult.getTransitionSystem().size());
		System.out.println(coarseDfsResult.getTime());
		System.out.print(coarseDfsResult.getTransitionSystem().size());
		System.out.println("(" + coarseDfsResult.getTransitionSystem().getCollisions() + ")");
	}
	@ParameterizedTest
	@MethodSource("models")
    public void ticketService(String model, int statespaceSize) throws ModelCheckingException, IOException {
		File rebecaFile = FileUtils.createTempFile(model);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
		printRILModel(transformedRILModel);

        HashSet<Feature> features = new HashSet<Feature>();
        features.add(Feature.CompleteTransitionSystem);
		TransparentActorModelCheckingResult result = 
        		fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, features);
		Assertions.assertEquals(statespaceSize, result.getTransitionSystem().size());
		System.out.println("time: " + result.getTime());
		System.out.print("size: " + result.getTransitionSystem().size());
		System.out.println("(" + result.getTransitionSystem().getCollisions() + ")");
		System.out.println(".....................");
        
		printRILModel(transformedRILModel);
	}
	
	protected static Stream<Arguments> models() {
	    return Stream.of(
	    		Arguments.arguments(TicketServiceSourceCodes.ONE_CUSTOMER, 5)
	    		, Arguments.arguments(TicketServiceSourceCodes.TWO_CUSTOMERS, 346)
	    		, Arguments.arguments(TicketServiceSourceCodes.THREE_CUSTOMERS, 13868)
	    		, Arguments.arguments(WSANSourceCodes.ONE_SENSOR, 13)
	    		, Arguments.arguments(WSANSourceCodes.TWO_SENSOR, 69)
	    );
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
}
