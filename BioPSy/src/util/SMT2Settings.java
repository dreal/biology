package util;

import java.util.*;

import org.sbml.jsbml.ASTNode;

import util.Utility.Tuple;

public class SMT2Settings {
	
	private Map<String, Tuple<Double, Double>> variables; // Initial values for each variable
	
	private String time; // The time variable
	
	private Map<String, ASTNode> odes; // A mapping of variables to differential equations
	
	private Map<String, ASTNode> assignments; // A mapping of variables to assignment rules
	
	private Trace trace; // The time series data

    private Map<String, Double> noise; // Parameters

    private Map<String,Double> epsilon;
	
	public SMT2Settings() {
		variables = new HashMap<String, Tuple<Double, Double>>();
		time = "";
		odes = new HashMap<String, ASTNode>();
		trace = null;
        //noise = new ArrayList<Double>();
        //for(int i = 0; i < variables.size(); i++) {
        //    noise.add(0.1);
        //}
	}
	
	public SMT2Settings(Map<String, Tuple<Double, Double>> variables, String time, Map<String, ASTNode> odes, Map<String, ASTNode> assignments, Trace trace, Map<String,Double> noise, Map<String,Double> epsilon) {
		this.variables = variables;
		this.time = time;
		this.odes = odes;
		this.assignments = assignments;
		this.trace = trace;
		this.noise = noise;
        this.epsilon = epsilon;
	}
		
	public Set<String> getAllVariables() {
		return variables.keySet();
	}
	
	public Set<String> getODEVariables() {
		return odes.keySet();
	}
	
	public Set<String> getAssignedVariables() {
		return assignments.keySet();
	}
	
	public Tuple<Double, Double> getValues(String variable) {
		return variables.get(variable);
	}
		
	public ASTNode getODE(String variable) {
			return odes.get(variable);
	}
	
	public ASTNode getAssignment(String variable) {
		return assignments.get(variable);
}
	
	public void addValues(String variable, Tuple<Double, Double> values) {
		variables.put(variable, values);
	}
	
	public void addODE(String variable, ASTNode ode) {
		odes.put(variable, ode);
	}
	
	public void addAssignment(String variable, ASTNode assignment) {
		assignments.put(variable, assignment);
	}
	
	public void removeVariable(String variable) {
		variables.remove(variable);
		odes.remove(variable);
		assignments.remove(variable);
	}
	
	public String getTimeVariable() {
		return time;
	}
	
	public void setTimeVariable(String time) {
		this.time = time;
	}

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}

    public Map<String, Double> getNoise() {
        return noise;
    }

    public void setNoise(Map<String, Double> noise) {
        this.noise = noise;
    }

    public Map<String, Double> getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(Map<String, Double> epsilon) {
        this.epsilon = epsilon;
    }

}
