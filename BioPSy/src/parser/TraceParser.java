package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.TimeSeriesModel;
import util.Trace;

import javax.swing.*;

public class TraceParser {
	
	public static Trace parseCopasiOutput(File file) throws IOException {
		Scanner scanner = new Scanner(file);
		List<String> variables = new ArrayList<String>();
		List<Double> timePoints = new ArrayList<Double>();
		List<List<Double>> data = new ArrayList<List<Double>>();
		if (scanner.hasNextLine()) {
			String[] vars = scanner.nextLine().split(",");
            for (int i = 1; i < vars.length; i++) {
                variables.add(vars[i]);
			}
		}
		for (int i = 0; i < variables.size(); i++) {
			data.add(new ArrayList<Double>());
		}
		while (scanner.hasNextLine()) {
			String[] dataLine = scanner.nextLine().split(",");
            if(dataLine.length > variables.size() + 1) {
                System.err.println("The size of data vector is greater than the size of variable vector " + dataLine.length + " " + variables.size());
                return null;
            }
            timePoints.add(Double.parseDouble(dataLine[0]));
            TimeSeriesModel.addTimePoint(Double.parseDouble(dataLine[0]));
			for (int i = 1; i < dataLine.length; i++) {
                if(!dataLine[i].isEmpty()) {
                    data.get(i - 1).add(Double.parseDouble(dataLine[i]));
                } else {
                    data.get(i - 1).add(new Double(Double.NaN));
                }
			}
            if(dataLine.length < variables.size() + 1) {
                for (int i = dataLine.length; i < variables.size() + 1; i++) {
                    data.get(i - 1).add(new Double(Double.NaN));
                }
            }
		}
		scanner.close();
        /*
        System.out.println("Data:");
        for(int i = 0; i < data.size(); i++) {
            for(int j = 0; j < data.get(i).size(); j++) {
                System.out.println(data.get(i).get(j) + ",");
            }
            System.out.println();
        }
        System.out.println("Number of rows: " + data.size());
        System.out.println("Number of columns: " + data.get(0).size());
        */
        Trace resultTrace = new Trace(variables, timePoints, data);
        //System.out.println(resultTrace);
		return resultTrace;
	}

}
