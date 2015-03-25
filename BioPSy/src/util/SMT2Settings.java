package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sbml.jsbml.ASTNode;

import util.Utility.Tuple;

public class SMT2Settings {
	
	private Map<String, Tuple<Double, Double>> variables; // Initial values for each variable
	
	private String time; // The time variable
	
	private Map<String, ASTNode> odes; // A mapping of variables to differential equations
	
	private Trace trace; // The time series data
	
	private double noise; // Parameters
	
	public SMT2Settings() {
		variables = new HashMap<String, Tuple<Double, Double>>();
		time = "";
		odes = new HashMap<String, ASTNode>();
		trace = null;
		noise = 0.1;
	}
	
	public SMT2Settings(Map<String, Tuple<Double, Double>> variables, String time, Map<String, ASTNode> odes, Trace trace, double noise) {
		this.variables = variables;
		this.time = time;
		this.odes = odes;
		this.trace = trace;
		this.noise = noise;
	}
		
	public Set<String> getAllVariables() {
		return variables.keySet();
	}
	
	public Set<String> getODEVariables() {
		return odes.keySet();
	}
	
	public Tuple<Double, Double> getValues(String variable) {
		return variables.get(variable);
	}
		
	public ASTNode getODE(String variable) {
			return odes.get(variable);
	}
	
	public void addValues(String variable, Tuple<Double, Double> values) {
		variables.put(variable, values);
	}
	
	public void addODE(String variable, ASTNode ode) {
		odes.put(variable, ode);
	}
	
	public void removeVariable(String variable) {
		variables.remove(variable);
		odes.remove(variable);
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
	
	public double getNoise() {
		return noise;
	}
	
	public void setNoise(double noise) {
		this.noise = noise;
	}
}
