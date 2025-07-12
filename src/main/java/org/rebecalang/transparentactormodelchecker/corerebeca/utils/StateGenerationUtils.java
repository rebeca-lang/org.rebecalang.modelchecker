package org.rebecalang.transparentactormodelchecker.corerebeca.utils;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;

public class StateGenerationUtils {
    public static Environment getEnvironment(RebecaModel rebecaModel) {
    	Environment environment = new Environment();
        for (FieldDeclaration fieldDeclaration : rebecaModel.getRebecaCode().getEnvironmentVariables()) {
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                Literal literal = (Literal) ((OrdinaryVariableInitializer) variableDeclarator.getVariableInitializer()).getValue();
                environment.setVariableValue(variableDeclarator.getVariableName(), literal.getLiteralValue());
            }
        }
        return environment;
    }

}
