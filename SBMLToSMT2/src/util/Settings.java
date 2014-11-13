package util;

import java.util.ArrayList;

public class Settings {
	
	private String sbmlFile, timeSeriesFile;
	
	private ArrayList<String> params;
	
	private double noise, precision, boxSize;
	
	public Settings() {
		sbmlFile = "";
		timeSeriesFile = "";
		params = new ArrayList<String>();
		noise = 1^(-1);
		precision = 1^(-4);
		boxSize = 1^(-9);
	}
	
	public Settings(String sbmlFile, String timeSeriesFile, ArrayList<String> params, double noise, double precision, double boxSize) {
		this.sbmlFile = sbmlFile;
		this.timeSeriesFile = timeSeriesFile;
		this.params = params;
		this.noise = noise;
		this.precision = precision;
		this.boxSize = boxSize;
	}
	
	public String getSBMLFile() {
		return sbmlFile;
	}
	
	public String getTimeSeriesFile() {
		return timeSeriesFile;
	}
	
	public String[] getParams() {
		return params.toArray(new String[0]);
	}
	
	public double getNoise() {
		return noise;
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public double getBoxSize() {
		return boxSize;
	}
	
	public void setSBMLFile(String sbmlFile) {
		this.sbmlFile = sbmlFile;
	}
	
	public void setTimeSeriesFile(String timeSeriesFile) {
		this.timeSeriesFile = timeSeriesFile;
	}
	
	public void setParams(ArrayList<String> params) {
		this.params = params;
	}
	
	public void setNoise(double noise) {
		this.noise = noise;
	}
	
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	
	public void setBoxSize(double boxSize) {
		this.boxSize = boxSize;
	}
	
	public void addParam(String param) {
		params.add(param);
	}
	
	public void removeParam(String param) {
		params.remove(param);
	}
}
