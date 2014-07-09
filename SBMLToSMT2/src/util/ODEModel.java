package util;

import java.util.HashMap;
import java.util.Map;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ExplicitRule;
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

	Map<String, ASTNode> odes; // A mapping of variables to differential equations

	Map<String, Double> initialValues; // Initial values for each variable

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
	public ODEModel(SBMLDocument document) {
		odes = new HashMap<String, ASTNode>();
		initialValues = new HashMap<String, Double>();
		for (Reaction reaction : document.getModel().getListOfReactions()) {
			ListOf<SpeciesReference> products = reaction.getListOfProducts();
			ListOf<SpeciesReference> reactants = reaction.getListOfReactants();
			for (SpeciesReference product : products) {
				ASTNode newNode = new ASTNode(reaction.getKineticLaw().getMath());
				if (product.isSetStoichiometry() && product.getStoichiometry() != 1) {
					ASTNode multiplyStoich = new ASTNode('*');
					multiplyStoich.addChild(new ASTNode(product.getStoichiometry()));
					multiplyStoich.addChild(newNode);
					newNode = multiplyStoich;
				}
				if (!odes.containsKey(product.getSpecies())) {
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						if (parameter.isSetValue()) {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
						else {
							odes.put(parameter.getId(), new ASTNode(0.0));
						}
					}
					odes.put(product.getSpecies(), newNode);
				}
				else {
					ASTNode math = new ASTNode('+');
					math.addChild(odes.get(product.getSpecies()));
					for (LocalParameter parameter : reaction.getKineticLaw()
							.getListOfLocalParameters()) {
						if (parameter.isSetValue()) {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
						else {
							odes.put(parameter.getId(), new ASTNode(0.0));
						}
					}
					math.addChild(newNode);
					odes.put(product.getSpecies(), math);
				}
			}
			for (SpeciesReference reactant : reactants) {
				ASTNode newNode = new ASTNode(reaction.getKineticLaw().getMath());
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
						if (parameter.isSetValue()) {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
						else {
							odes.put(parameter.getId(), new ASTNode(0.0));
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
						if (parameter.isSetValue()) {
							replace(parameter.getId(), parameter.getValue(), newNode);
						}
						else {
							odes.put(parameter.getId(), new ASTNode(0.0));
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
				if (species.isSetValue()) {
					replaceWithValue(species.getId(), species.getValue());
				}
				else {
					odes.put(species.getId(), new ASTNode(0.0));
					initialValues.put(species.getId(), 0.0);
				}
			}
			else {
				initialValues.put(species.getId(), species.getValue());
			}
		}
		for (Compartment compartment : document.getModel().getListOfCompartments()) {
			if (!odes.containsKey(compartment.getId())) {
				if (compartment.isSetValue()) {
					replaceWithValue(compartment.getId(), compartment.getValue());
				}
				else {
					odes.put(compartment.getId(), new ASTNode(0.0));
					initialValues.put(compartment.getId(), 0.0);
				}
			}
			else {
				initialValues.put(compartment.getId(), compartment.getValue());
			}
		}
		for (Parameter parameter : document.getModel().getListOfParameters()) {
			if (!odes.containsKey(parameter.getId())) {
				if (parameter.isSetValue()) {
					replaceWithValue(parameter.getId(), parameter.getValue());
				}
				else {
					odes.put(parameter.getId(), new ASTNode(0.0));
					initialValues.put(parameter.getId(), 0.0);
				}
			}
			else {
				initialValues.put(parameter.getId(), parameter.getValue());
			}
		}
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
	private void replaceWithValue(String variable, double value) {
		for (ASTNode equation : odes.values()) {
			replace(variable, value, equation);
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

	/**
	 * 
	 * Returns a String representation of the given ASTNode equation in prefix notation form.
	 * 
	 * @param math
	 *            - the equation to convert to prefix notation
	 * @return The prefix notation String representation of the provided equation
	 */
	public static String prefixASTNodeToString(ASTNode math) {
		if (math.getType() == ASTNode.Type.CONSTANT_E) {
			return "exponentiale";
		}
		else if (math.getType() == ASTNode.Type.CONSTANT_FALSE) {
			return "false";
		}
		else if (math.getType() == ASTNode.Type.CONSTANT_PI) {
			return "pi";
		}
		else if (math.getType() == ASTNode.Type.CONSTANT_TRUE) {
			return "true";
		}
		else if (math.getType() == ASTNode.Type.DIVIDE) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(/ " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION) {
			String result = "( " + math.getName();
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ABS) {
			return "(abs " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCOS) {
			return "(acos " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCOSH) {
			return "(acosh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCOT) {
			return "(acot " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCOTH) {
			return "(acoth " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCSC) {
			return "(acsc " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCCSCH) {
			return "(acsch " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCSEC) {
			return "(asec " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCSECH) {
			return "(asech " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCSIN) {
			return "(asin " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCSINH) {
			return "(asinh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCTAN) {
			return "(atan " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ARCTANH) {
			return "(atanh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_CEILING) {
			return "(ceil " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_COS) {
			return "(cos " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_COSH) {
			return "(cosh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_COT) {
			return "(cot " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_COTH) {
			return "(coth " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_CSC) {
			return "(csc " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_CSCH) {
			return "(csch " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_DELAY) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(delay " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_EXP) {
			return "(exp " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_FACTORIAL) {
			return "(factorial " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_FLOOR) {
			return "(floor " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_LN) {
			return "(ln " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_LOG) {
			String result = "(log ";
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_PIECEWISE) {
			String result = "(piecewise ";
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_POWER) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(^ " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_ROOT) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(root " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_SEC) {
			return "(sec " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_SECH) {
			return "(sech " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_SIN) {
			return "(sin " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_SINH) {
			return "(sinh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_TAN) {
			return "(tan " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.FUNCTION_TANH) {
			return "(tanh " + prefixASTNodeToString(math.getChild(0)) + ")";
		}
		else if (math.getType() == ASTNode.Type.INTEGER) {
			return "" + math.getInteger();
		}
		else if (math.getType() == ASTNode.Type.LOGICAL_AND) {
			if (math.getChildCount() == 0)
				return "";
			String result = "(and ";
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.LOGICAL_NOT) {
			if (math.getChildCount() == 0)
				return "";
			String result = "(not ";
			String child = prefixASTNodeToString(math.getChild(0));
			result += child;
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.LOGICAL_OR) {
			if (math.getChildCount() == 0)
				return "";
			String result = "(or ";
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.LOGICAL_XOR) {
			if (math.getChildCount() == 0)
				return "";
			String result = "(xor ";
			for (int i = 0; i < math.getChildCount(); i++) {
				String child = prefixASTNodeToString(math.getChild(i));
				result += child;
				if (i + 1 < math.getChildCount()) {
					result += " ";
				}
			}
			result += ")";
			return result;
		}
		else if (math.getType() == ASTNode.Type.MINUS) {
			if (math.getChildCount() == 1) {
				return "(- 0.0 " + prefixASTNodeToString(math.getChild(0)) + ")";
			}
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(- " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.NAME) {
			return math.getName();
		}
		else if (math.getType() == ASTNode.Type.NAME_AVOGADRO) {
			return "avogadro";
		}
		else if (math.getType() == ASTNode.Type.NAME_TIME) {
			return "t";
		}
		else if (math.getType() == ASTNode.Type.PLUS) {
			String returnVal = "(+ ";
			boolean first = true;
			for (int i = 0; i < math.getChildCount(); i++) {
				if (first) {
					first = false;
				}
				else {
					returnVal += " ";
				}
				returnVal += prefixASTNodeToString(math.getChild(i));
			}
			returnVal += ")";
			return returnVal;
		}
		else if (math.getType() == ASTNode.Type.POWER) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(^ " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RATIONAL) {
			return "(/ " + math.getNumerator() + " " + math.getDenominator() + ")";
		}
		else if (math.getType() == ASTNode.Type.REAL) {
			return "" + math.getReal();
		}
		else if (math.getType() == ASTNode.Type.REAL_E) {
			return math.getMantissa() + "e" + math.getExponent();
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_EQ) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(= " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_GEQ) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(>= " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_GT) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(> " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_LEQ) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(<= " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_LT) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(< " + leftStr + " " + rightStr + ")";
		}
		else if (math.getType() == ASTNode.Type.RELATIONAL_NEQ) {
			String leftStr = prefixASTNodeToString(math.getLeftChild());
			String rightStr = prefixASTNodeToString(math.getRightChild());
			return "(not (= " + leftStr + " " + rightStr + "))";
		}
		else if (math.getType() == ASTNode.Type.TIMES) {
			String returnVal = "(* ";
			boolean first = true;
			for (int i = 0; i < math.getChildCount(); i++) {
				if (first) {
					first = false;
				}
				else {
					returnVal += " ";
				}
				returnVal += prefixASTNodeToString(math.getChild(i));
			}
			returnVal += ")";
			return returnVal;
		}
		else {
			if (math.isOperator()) {
				System.err.println("Operator " + math.getName() + " is not currently supported.");
			}
			else {
				System.err.println(math.getName() + " is not currently supported.");
			}
		}
		return "";
	}
}
