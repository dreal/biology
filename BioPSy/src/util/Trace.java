package util;

import java.util.Arrays;
import java.util.List;

public class Trace {
	
	private String[] variables;
	
	private double[][] data;

	private double[] timePoints;
	
	public Trace(List<String> variables, List<Double> timePoints, List<List<Double>> data) {
		this.variables = variables.toArray(new String[0]);
        /*
        System.out.println("Variables:");
        for(int i = 0; i < this.variables.length; i++)
        {
            System.out.println(this.variables[i]);
        }
        */
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
	
	public String[] getVariables() {
		return variables;
	}
	
	public double[] getTimePoints() {
		return timePoints;
	}
	
	public double getValue(String variable, double timePoint) {
		for (int i = 0; i < timePoints.length; i++) {
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
		int var = -1;
		for (int i = 0; i < variables.length; i++) {
			if (variables[i].equals(variable)) {
				var = i;
				break;
			}
		}
		double[] values = new double[data[var].length];
		for (int i = 0; i < data[var].length; i++) {
			values[i] = data[var][i];
		}
		return values;
	}

    private int getIndexOf(String var) {
        int index = -1;
        for(int i = 0; i < variables.length; i++) {
            //System.out.println("In Trace: " + variables[i] + " comparing to " + var);
            if(variables[i].equals(var)) {
                //System.out.println("Equal: " + var);
                index = i;
                break;
            }
        }
        return index;
    }

    public Double getMaxForVar(String var) {
        int index = getIndexOf(var);
        if(index == -1) {
            return null;
        }
        double max = data[index][0];
        for(int i = 1; i < data[index].length; i++) {
            if(!Double.isNaN(data[index][i])) {
                if(data[index][i] > max) max = data[index][i];
            }
        }
        return max;
    }

    public Double getMinForVar(String var) {
        int index = getIndexOf(var);
        if(index == -1) {
            return null;
        }
        double min = data[index][0];
        for(int i = 1; i < data[index].length; i++) {
            if(!Double.isNaN(data[index][i])) {
                if(data[index][i] < min) min = data[index][i];
            }
        }
        return min;
    }
	
	public String toString() {
		String trace = "((\"time\"";
		for (int i = 0; i < variables.length; i++) {
			trace += ", \"" + variables[i] + "\"";
		}
		trace += ")";
		for (int i = 0; i < timePoints.length; i++) {
			trace += ",(" + timePoints[i];
			for (double value : data[i]) {
				trace += ", " + value;
			}
			trace += ")";
            //System.out.println(trace);
		}
		trace += ")";
		return trace;
	}

    public double[][] getData() {
        return data;
    }
}
