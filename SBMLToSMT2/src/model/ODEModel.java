package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ExplicitRule;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;

/**
 * 
 * This class represents an ODE model which is a collection of differential equations stored as
 * ASTNode trees. Additionally, the initial value for each variable being differentiated is stored
 * as a real (double).
 * 
 * @author Curtis Madsen
 * 
 */
public class ODEModel {

	private Map<String, ASTNode> odes; // A mapping of variables to differential equations

	private Map<String, Double> initialValues; // Initial values for each variable

	private Map<String, Double> parameters; // Parameters in the differential equations

	/**
	 * 
	 * Constructs a new ODE model given an SBML document. Each ODE is constructed by iterating over
	 * all of the reactions and rate rules in the SBML model and extracting the equations multiplied
	 * by any stoichiometries into the appropriate ODE. Initial values are also extracted from the
	 * SBML model and stored in a separate data structure.
	 * 
	 * @param document
	 *            - the SBML document to convert into an ODE model
	 */
	public ODEModel(SBMLDocument document, List<String> interestingParameters) {
		odes = new HashMap<String, ASTNode>();
		initialValues = new HashMap<String, Double>();
		parameters = new HashMap<String, Double>();
		for (Reaction reaction : document.getModel().getListOfReactions()) {
			ListOf<SpeciesReference> products = reaction.getListOfProducts();
			ListOf<SpeciesReference> reactants = reaction.getListOfReactants();
			ASTNode newNode = new ASTNode(reaction.getKineticLaw().getMath());
			List<ASTNode> reacts = new ArrayList<ASTNode>();
			for (SpeciesReference reactant : reactants) {
				if (reactant.isSetStoichiometry()) {
					ASTNode r = new ASTNode(ASTNode.Type.FUNCTION_POWER);
					r.addChild(new ASTNode(reactant.getSpecies()));
					r.addChild(new ASTNode(reactant.getStoichiometry()));
					reacts.add(r);
				}
			}
			for (ASTNode n : reacts) {
				ASTNode oldNode = newNode;
				newNode = new ASTNode('*');
				newNode.addChild(n);
				newNode.addChild(oldNode);
			}
			for (SpeciesReference product : products) {
				if (product.isSetStoichiometry() && product.getStoichiometry() != 1) {
					ASTNode multiplyStoich = new ASTNode('*');
					multiplyStoich.addChild(new ASTNode(product.getStoichiometry()));
					multiplyStoich.addChild(newNode);
					newNode = multiplyStoich;
				}
				if (!odes.containsKey(product.getSpecies())) {
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						String newName = reaction.getId() + "_" + parameter.getId();
						if (interestingParameters.contains(newName)) {
							replace(parameter.getId(), newName, newNode);
							if (parameter.isSetValue()) {
								parameters.put(newName, parameter.getValue());
							}
							else {
								parameters.put(newName, 0.0);
							}
						}
						else {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
					}
					odes.put(product.getSpecies(), newNode);
				}
				else {
					ASTNode math = new ASTNode('+');
					math.addChild(odes.get(product.getSpecies()));
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						String newName = reaction.getId() + "_" + parameter.getId();
						if (interestingParameters.contains(newName)) {
							replace(parameter.getId(), newName, newNode);
							if (parameter.isSetValue()) {
								parameters.put(newName, parameter.getValue());
							}
							else {
								parameters.put(newName, 0.0);
							}
						}
						else {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
					}
					math.addChild(newNode);
					odes.put(product.getSpecies(), math);
				}
			}
			for (SpeciesReference reactant : reactants) {
				if (reactant.isSetStoichiometry() && reactant.getStoichiometry() != 1) {
					ASTNode multiplyStoich = new ASTNode('*');
					multiplyStoich.addChild(new ASTNode(reactant.getStoichiometry()));
					multiplyStoich.addChild(newNode);
					newNode = multiplyStoich;
				}
				if (!odes.containsKey(reactant.getSpecies())) {
					ASTNode math = new ASTNode('-');
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						String newName = reaction.getId() + "_" + parameter.getId();
						if (interestingParameters.contains(newName)) {
							replace(parameter.getId(), newName, newNode);
							if (parameter.isSetValue()) {
								parameters.put(newName, parameter.getValue());
							}
							else {
								parameters.put(newName, 0.0);
							}
						}
						else {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
					}
					math.addChild(newNode);
					odes.put(reactant.getSpecies(), math);
				}
				else {
					ASTNode math = new ASTNode('-');
					math.addChild(odes.get(reactant.getSpecies()));
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						String newName = reaction.getId() + "_" + parameter.getId();
						if (interestingParameters.contains(newName)) {
							replace(parameter.getId(), newName, newNode);
							if (parameter.isSetValue()) {
								parameters.put(newName, parameter.getValue());
							}
							else {
								parameters.put(newName, 0.0);
							}
						}
						else {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
					}
					math.addChild(newNode);
					odes.put(reactant.getSpecies(), math);
				}
			}
		}
		for (Rule rule : document.getModel().getListOfRules()) {
			if (rule.isRate()) {
				ExplicitRule rateRule = ((ExplicitRule) rule);
				if (!odes.containsKey(rateRule.getVariable())) {
					odes.put(rateRule.getVariable(), new ASTNode(rateRule.getMath()));
				}
				else {
					ASTNode math = new ASTNode('+');
					math.addChild(odes.get(rateRule.getVariable()));
					math.addChild(new ASTNode(rateRule.getMath()));
					odes.put(rateRule.getVariable(), math);
				}
			}
		}
		for (Species species : document.getModel().getListOfSpecies()) {
			if (!odes.containsKey(species.getId())) {
				odes.put(species.getId(), new ASTNode(0.0));
			}
			if (species.isSetValue()) {
				initialValues.put(species.getId(), species.getValue());
			}
			else {
				initialValues.put(species.getId(), 0.0);
			}
		}
		for (Compartment compartment : document.getModel().getListOfCompartments()) {
			if (!odes.containsKey(compartment.getId())) {
				if (compartment.isSetValue()) {
					replaceAllWithValue(compartment.getId(), compartment.getValue());
				}
				else {
					replaceAllWithValue(compartment.getId(), 0.0);
				}
			}
			else {
				if (compartment.isSetValue()) {
					initialValues.put(compartment.getId(), compartment.getValue());
				}
				else {
					initialValues.put(compartment.getId(), 0.0);
				}
			}
		}
		for (Parameter parameter : document.getModel().getListOfParameters()) {
			if (!odes.containsKey(parameter.getId())) {
				if (interestingParameters.contains(parameter.getId())) {
					if (parameter.isSetValue()) {
						parameters.put(parameter.getId(), parameter.getValue());
					}
					else {
						parameters.put(parameter.getId(), 0.0);
					}
				}
				else {
					if (parameter.isSetValue()) {
						replaceAllWithValue(parameter.getId(), parameter.getValue());
					}
					else {
						replaceAllWithValue(parameter.getId(), 0.0);
					}
				}
			}
			else {
				if (parameter.isSetValue()) {
					initialValues.put(parameter.getId(), parameter.getValue());
				}
				else {
					initialValues.put(parameter.getId(), 0.0);
				}
			}
		}
		replaceAllFunctionDefinitions(document.getModel().getListOfFunctionDefinitions());
	}

	private void replaceAllFunctionDefinitions(ListOf<FunctionDefinition> functions) {
		for (String key : odes.keySet()) {
			odes.put(key, replaceFunctionDefinition(odes.get(key), functions));
		}
	}

	private ASTNode replaceFunctionDefinition(ASTNode equation, ListOf<FunctionDefinition> functions) {
		if (equation.getType() == ASTNode.Type.FUNCTION) {
			FunctionDefinition fd = functions.get(equation.getName());
			ASTNode newNode = fd.getBody();
			for (int i = 0; i < fd.getArgumentCount(); i++) {
				newNode = replace(fd.getArgument(i).getName(), equation.getChild(i), newNode);
			}
			return newNode;
		}
		for (int i = 0; i < equation.getChildCount(); i++) {
			equation.replaceChild(i, replaceFunctionDefinition(equation.getChild(i), functions));
		}
		return equation;
	}

	private ASTNode replace(String variable, ASTNode replacement, ASTNode equation) {
		if (equation.getType() == ASTNode.Type.NAME && equation.getName().equals(variable)) {
			return new ASTNode(replacement);
		}
		for (int i = 0; i < equation.getChildCount(); i++) {
			equation.replaceChild(i,
					replace(variable, new ASTNode(replacement), equation.getChild(i)));
		}
		return equation;
	}

	/**
	 * 
	 * Replaces a variable with the provided real in all ODE equations.
	 * 
	 * @param variable
	 *            - the variable to be replaced
	 * @param value
	 *            - the real value replacing the variable
	 */
	private void replaceAllWithValue(String variable, double value) {
		for (String key : odes.keySet()) {
			odes.put(key, replace(variable, value, odes.get(key)));
		}
	}

	/**
	 * 
	 * Replaces a variable with the provided real in the given ODE equation.
	 * 
	 * @param variable
	 *            - the variable to be replaced
	 * @param value
	 *            - the real value replacing the variable
	 * @param equation
	 *            - the ODE equation where the variable should be replaced
	 * @return The ODE equation after the replacement has taken place
	 */
	private ASTNode replace(String variable, double value, ASTNode equation) {
		if (equation.getType() == ASTNode.Type.NAME && equation.getName().equals(variable)) {
			return new ASTNode(value);
		}
		for (int i = 0; i < equation.getChildCount(); i++) {
			equation.replaceChild(i, replace(variable, value, equation.getChild(i)));
		}
		return equation;
	}

	/**
	 * 
	 * Replaces a variable with a new name in the given ODE equation.
	 * 
	 * @param variable
	 *            - the variable to be replaced
	 * @param newName
	 *            - the new variable name
	 * @param equation
	 *            - the ODE equation where the variable should be replaced
	 * @return The ODE equation after the replacement has taken place
	 */
	private ASTNode replace(String variable, String newName, ASTNode equation) {
		if (equation.getType() == ASTNode.Type.NAME && equation.getName().equals(variable)) {
			return new ASTNode(newName);
		}
		for (int i = 0; i < equation.getChildCount(); i++) {
			equation.replaceChild(i, replace(variable, newName, equation.getChild(i)));
		}
		return equation;
	}

	/**
	 * 
	 * Returns the ODE associated with the provided variable.
	 * 
	 * @param variable
	 *            - the variable for which the ODE should be returned
	 * @return The ODE associated with the given variable
	 */
	public ASTNode getODE(String variable) {
		return odes.get(variable);
	}

	/**
	 * 
	 * Returns the initial value for the provided variable.
	 * 
	 * @param variable
	 *            - the variable for which the initial value should be returned
	 * @return The initial value for the given variable
	 */
	public double getInitialValue(String variable) {
		return initialValues.get(variable);
	}

	/**
	 * 
	 * Returns an array of variable names.
	 * 
	 * @return The array of variable names
	 */
	public String[] getArrayOfVariables() {
		return odes.keySet().toArray(new String[0]);
	}
}
