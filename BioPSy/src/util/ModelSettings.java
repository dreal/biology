package util;

import java.util.List;
import java.util.ArrayList;

public class ModelSettings {
	
	private String sbmlFile, timeSeriesFile;
	
	private List<String> params;
	
	private double noise;

	public ModelSettings() {
		sbmlFile = "";
		timeSeriesFile = "";
		params = new ArrayList<String>();
		noise = 0.00005;
	}
	
	public ModelSettings(String sbmlFile, String timeSeriesFile, List<String> params, double noise) {
		this.sbmlFile = sbmlFile;
		this.timeSeriesFile = timeSeriesFile;
		this.params = params;
		this.noise = noise;
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

	public void addParam(String param) {
		params.add(param);
	}
	
	public void removeParam(String param) {
		params.remove(param);
	}
}
