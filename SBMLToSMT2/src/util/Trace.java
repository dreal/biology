package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trace {
	
	private String[] variables;
	
	private double[][] data;

	private double[] timePoints;
	
	public Trace(List<String> variables, List<Double> timePoints, List<List<Double>> data) {
		this.variables = variables.toArray(new String[0]);
		this.timePoints = new double[timePoints.size()];
		for (int i = 0; i < timePoints.size(); i++) {
			this.timePoints[i] = timePoints.get(i);
		}
		this.data = new double[data.size()][data.get(0).size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < data.get(0).size(); j++) {
				this.data[i][j] = data.get(i).get(j);
			}
		}
	}
	
	public Trace(String[] variables, double[] timePoints, double[][] data) {
		this.variables = variables;
		this.timePoints = timePoints;
		this.data = data;
	}
	
	public Trace(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		List<String> variables = new ArrayList<String>();
		List<Double> timePoints = new ArrayList<Double>();
		List<List<Double>> data = new ArrayList<List<Double>>();
		if (scanner.hasNextLine()) {
			String[] vars = scanner.nextLine().split("\t");
			for (int i = 1; i < vars.length; i++) {
				variables.add(vars[i]);
			}
		}
		for (int i = 0; i < variables.size(); i++) {
			data.add(new ArrayList<Double>());
		}
		while (scanner.hasNextLine()) {
			String[] dataLine = scanner.nextLine().split("\t");
			timePoints.add(Double.parseDouble(dataLine[0]));
			for (int i = 1; i < dataLine.length; i++) {
				data.get(i - 1).add(Double.parseDouble(dataLine[i]));
			}
		}
		scanner.close();
		this.variables = variables.toArray(new String[0]);
		this.timePoints = new double[timePoints.size()];
		for (int i = 0; i < timePoints.size(); i++) {
			this.timePoints[i] = timePoints.get(i);
		}
		this.data = new double[data.size()][data.get(0).size()];
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < data.get(0).size(); j++) {
				this.data[i][j] = data.get(i).get(j);
			}
		}
	}
	
	public String[] getVariables() {
		return variables;
	}
	
	public double[] getTimePoints() {
		return timePoints;
	}
	
	public double getValue(String variable, double timePoint) {
		for (int i = 0; i < timePoints.length; i ++) {
			if (timePoints[i] == timePoint) {
				for (int j = 0; j < variables.length; j ++) {
					if (variables[j].equals(variable)) {
						return data[j][i];
					}
				}
			}
		}
		return Double.NaN;
	}

	public double[] getValues(String variable) {
		double[] values = new double[data.length];
		int var = -1;
		for (int i = 0; i < variables.length; i ++) {
			if (variables[i].equals(variable)) {
				var = i;
				break;
			}
		}
		for (int i = 0; i < data.length; i ++) {
			values[i] = data[i][var];
		}
		return values;
	}
	
	public String toString() {
		String trace = "((\"time\"";
		for (String variable : variables) {
			trace += ", \"" + variable + "\"";
		}
		trace += ")";
		for (int i = 0; i < timePoints.length; i++) {
			trace += ",(" + timePoints[i];
			for (double value : data[i]) {
				trace += ", " + value;
			}
			trace += ")";
		}
		trace += ")";
		return trace;
	}
}
