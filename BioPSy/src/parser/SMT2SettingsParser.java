package parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.SMT2Settings;
import util.Utility;

public class SMT2SettingsParser {

    /*
	public static SMT2Settings parseSettingsFile(String filename) throws DOMException, Exception {
		Map<String, Tuple<Double, Double>> variables = new HashMap<String, Tuple<Double, Double>>();
		String time = "";
		Map<String, ASTNode> odes = new HashMap<String, ASTNode>();
		Trace trace = null;
		double epsilon = 0.00005;
		double delta = 0.001;
		double noise = 0.01;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(filename));
		Element settings = (Element) doc.getElementsByTagName("data").item(0);
		Element declaration = (Element) settings.getElementsByTagName("declare").item(0);
		NodeList vars = declaration.getElementsByTagName("var");
		for (int i = 0; i < vars.getLength(); i++) {
			Element var = (Element) vars.item(i);
			Element name = (Element) var.getElementsByTagName("name").item(0);
			if (var.getAttribute("type").equals("time")) {
				time = name.getTextContent();
			}
			else {
				double left = Double.parseDouble(((Element) ((Element) var.getElementsByTagName(
						"domain").item(0)).getElementsByTagName("left").item(0)).getTextContent());
				double right = Double.parseDouble(((Element) ((Element) var.getElementsByTagName(
						"domain").item(0)).getElementsByTagName("right").item(0)).getTextContent());
				variables.put(name.getTextContent(), new Tuple<Double, Double>(left, right));
			}
		}
		Element equations = (Element) settings.getElementsByTagName("odes").item(0);
		NodeList odeList = equations.getElementsByTagName("ode");
		List<String> variableNames = new ArrayList<String>();
		for (int i = 0; i < odeList.getLength(); i++) {
			Element ode = (Element) odeList.item(i);
			String variable = ode.getTextContent().substring(ode.getTextContent().indexOf('[') + 1,
					ode.getTextContent().indexOf(']'));
			variableNames.add(variable);
			String formula = ode.getTextContent().substring(ode.getTextContent().indexOf("] ") + 2,
					ode.getTextContent().length() - 1);
			odes.put(variable, Utility.prefixStringToASTNode(formula));
		}
		Element series = (Element) settings.getElementsByTagName("series").item(0);
		NodeList points = series.getElementsByTagName("point");
		List<Double> timePoints = new ArrayList<Double>();
		List<List<Double>> data = new ArrayList<List<Double>>();
		for (int i = 0; i < variableNames.size(); i++) {
			data.add(new ArrayList<Double>());
		}
		for (int i = 0; i < points.getLength(); i++) {
			Element point = (Element) points.item(i);
			timePoints.add(Double.parseDouble(point.getAttribute("time")));
			NodeList intervals = point.getElementsByTagName("interval");
			List<Tuple<String, Double>> dataPairs = new ArrayList<Tuple<String, Double>>();
			for (int j = 0; j < intervals.getLength(); j++) {
				Element interval = (Element) intervals.item(j);
				String var = interval.getAttribute("var");
				if (var.equals("")) {
					var = variableNames.get(j);
				}
				double left = Double.parseDouble(((Element) interval.getElementsByTagName("left")
						.item(0)).getTextContent());
				double right = Double.parseDouble(((Element) interval.getElementsByTagName("right")
						.item(0)).getTextContent());
				dataPairs.add(new Tuple<String, Double>(var, (left + right) / 2));
			}
			for (int j = 0; j < variableNames.size(); j++) {
				for (Tuple<String, Double> element : dataPairs) {
					if (variableNames.get(j).equals(element.x)) {
						data.get(j).add(element.y);
						break;
					}
				}
			}
		}
		trace = new Trace(variableNames, timePoints, data);
		try {
			delta = Double.parseDouble(((Element) settings.getElementsByTagName("delta").item(0))
					.getTextContent());
		}
		catch (Exception e) {
		}
		try {
			epsilon = Double.parseDouble(((Element) settings.getElementsByTagName("epsilon")
					.item(0)).getTextContent());
		}
		catch (Exception e) {
		}
		try {
			noise = Double.parseDouble(((Element) settings.getElementsByTagName("noise").item(0))
					.getTextContent());
		}
		catch (Exception e) {
		}
		return new SMT2Settings(variables, time, odes, trace, noise);
	}
	*/

	public static void writeSettingsToFile(String filename, SMT2Settings settings)
			throws ParserConfigurationException, TransformerFactoryConfigurationError,
			FileNotFoundException, TransformerException {
		List<String> variables = new ArrayList<String>();
        /*
		for (String traceVariable : settings.getTrace().getVariables()) {
			if (settings.getODEVariables().contains(traceVariable)) {
				variables.add(traceVariable);
			}
		}
		*/
		for (String odeVariable : settings.getODEVariables()) {
			if (!variables.contains(odeVariable)) {
				variables.add(odeVariable);
			}
		}
		for (String assignedVariable : settings.getAssignedVariables()) {
			if (!variables.contains(assignedVariable)) {
				variables.add(assignedVariable);
			}
		}
		for (String variable : settings.getAllVariables()) {
			if (!variables.contains(variable)) {
				variables.add(variable);
			}
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element topLevelElement = doc.createElement("data");
		Element declaration = doc.createElement("declare");
		for (String variable : variables) {
			Element var = doc.createElement("var");
			if (settings.getODEVariables().contains(variable) || settings.getAssignedVariables().contains(variable)) {
				var.setAttribute("type", "var");
			}
			else {
				var.setAttribute("type", "param");
                var.setAttribute("epsilon", Double.toString(settings.getEpsilon().get(variable)));
			}
			Element name = doc.createElement("name");
			name.setTextContent(variable);
			var.appendChild(name);
			Element domain = doc.createElement("domain");
			Element left = doc.createElement("left");
			left.setTextContent("" + settings.getValues(variable).x);
			Element right = doc.createElement("right");
			right.setTextContent("" + settings.getValues(variable).y);
			domain.appendChild(left);
			domain.appendChild(right);
			var.appendChild(domain);
			declaration.appendChild(var);
		}
		Element var = doc.createElement("var");
		var.setAttribute("type", "time");
		Element name = doc.createElement("name");
		name.setTextContent("t");
		var.appendChild(name);
		declaration.appendChild(var);
		topLevelElement.appendChild(declaration);
		Element assignments = doc.createElement("assignments");
		for (String variable : settings.getAssignedVariables()) {
			Element assignment = doc.createElement("assignment");
			assignment.setAttribute("var", variable);
    		Element eq2 = doc.createElement("eq1");
			eq2.setTextContent("(= " + variable + "_0_0 " + Utility.prefixASTNodeToStringWithSuffix(settings.getAssignment(variable), "_0_0") + ")");
			Element eq3 = doc.createElement("eq2");
			eq3.setTextContent("(= " + variable + "_0_t " + Utility.prefixASTNodeToStringWithSuffix(settings.getAssignment(variable), "_0_t") + ")");
			assignment.appendChild(eq2);
			assignment.appendChild(eq3);
			assignments.appendChild(assignment);
		}
		topLevelElement.appendChild(assignments);
		Element odes = doc.createElement("odes");
		for (String variable : settings.getODEVariables()) {
			Element ode = doc.createElement("ode");
			ode.setTextContent("(= d/dt[" + variable + "] " + Utility.prefixASTNodeToString(settings.getODE(variable))
					+ ")");
			odes.appendChild(ode);
		}
		topLevelElement.appendChild(odes);
		Element series = doc.createElement("series");
		for (int i = 0; i < settings.getTrace().getTimePoints().length; i++) {
			double timePoint = settings.getTrace().getTimePoints()[i];
			Element point = doc.createElement("point");
			point.setAttribute("time", "" + timePoint);
			for (String variable : variables) {
				for (String traceVariable : settings.getTrace().getVariables()) {
                    if (variable.equals(traceVariable)) {
						if ((settings.getODEVariables().contains(traceVariable)) || (settings.getAssignedVariables().contains(traceVariable))) {
                            if(!Double.isNaN(settings.getTrace().getValue(traceVariable, timePoint))) {
                                double noise = settings.getNoise().get(traceVariable);
                                Element interval = doc.createElement("interval");
                                interval.setAttribute("var", traceVariable);
                                double leftValue = settings.getTrace().getValue(traceVariable,
                                        timePoint);
                                double rightValue = leftValue;
                                /*
                                if (i == 0) {
                                    if (leftValue == 0) {
                                        leftValue -= 0.0001;
                                        rightValue += 0.0001;
                                    } else {
                                        leftValue -= (leftValue * 0.00001);
                                        rightValue += (rightValue * 0.00001);
                                    }
                                } else {
                                    leftValue -= settings.getNoise();
                                    rightValue += settings.getNoise();
                                }
                                */
                                if (i != 0) {
                                    leftValue -= noise;
                                    rightValue += noise;
                                }
                                Element left = doc.createElement("left");
                                left.setTextContent("" + leftValue);
                                Element right = doc.createElement("right");
                                right.setTextContent("" + rightValue);
                                interval.appendChild(left);
                                interval.appendChild(right);
                                point.appendChild(interval);
                            }
						}
						break;
					}
				}
			}
			series.appendChild(point);
		}
		topLevelElement.appendChild(series);
		Element delta = doc.createElement("delta");
		delta.setTextContent("" + 0.001);
		topLevelElement.appendChild(delta);
		Element epsilon = doc.createElement("epsilon");
		epsilon.setTextContent("" + 0.001);
		topLevelElement.appendChild(epsilon);
		Element noise = doc.createElement("noise");
		noise.setTextContent("" + settings.getNoise());
		topLevelElement.appendChild(noise);
		doc.appendChild(topLevelElement);
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
		tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(filename)));
	}

}
