package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.sbml.jsbml.ASTNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.SMT2Settings;
import util.Trace;
import util.Utility;
import util.Utility.Tuple;

public class SMT2SettingsParser {

	public static SMT2Settings parseSettingsFile(String filename) throws DOMException, Exception {
		Map<String, Tuple<Double, Double>> variables = new HashMap<String, Tuple<Double, Double>>();
		String time = "";
		Map<String, ASTNode> odes = new HashMap<String, ASTNode>();
		Trace trace = null;
		double epsilon = 0.000001;
		double delta = 0.000001;
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
		return new SMT2Settings(variables, time, odes, trace, epsilon, delta, noise);
	}

	public static void writeSettingsToFile(String filename, SMT2Settings settings)
			throws ParserConfigurationException, TransformerFactoryConfigurationError,
			FileNotFoundException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element topLevelElement = doc.createElement("data");
		Element declaration = doc.createElement("declare");
		for (String variable : settings.getAllVariables()) {
			Element var = doc.createElement("var");
			if (settings.getODEVariables().contains(variable)) {
				var.setAttribute("type", "var");
			}
			else {
				var.setAttribute("type", "param");
			}
			Element name = doc.createElement("name");
			name.setTextContent(variable);
			var.appendChild(name);
			Element domain = doc.createElement("domain");
			Element left = doc.createElement("left");
			left.setTextContent(("" + settings.getValues(variable).x).replace("E-", "e-").replace(
					"E", "e+"));
			Element right = doc.createElement("right");
			right.setTextContent(("" + settings.getValues(variable).y).replace("E-", "e-").replace(
					"E", "e+"));
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
		Element odes = doc.createElement("odes");
		for (String variable : settings.getAllVariables()) {
			for (String odeVariable : settings.getODEVariables()) {
				if (variable.equals(odeVariable)) {
					Element ode = doc.createElement("ode");
					ode.setTextContent("(= d/dt[" + odeVariable + "] "
							+ Utility.prefixASTNodeToString(settings.getODE(odeVariable)) + ")");
					odes.appendChild(ode);
					break;
				}
			}
		}
		topLevelElement.appendChild(odes);
		Element series = doc.createElement("series");
		for (double timePoint : settings.getTrace().getTimePoints()) {
			Element point = doc.createElement("point");
			point.setAttribute("time", "" + timePoint);
			for (String variable : settings.getAllVariables()) {
				for (String traceVariable : settings.getTrace().getVariables()) {
					if (variable.equals(traceVariable)) {
						if (settings.getODEVariables().contains(traceVariable)) {
							Element interval = doc.createElement("interval");
							interval.setAttribute("var", traceVariable);
							Element left = doc.createElement("left");
							left.setTextContent(("" + (settings.getTrace().getValue(traceVariable,
									timePoint) - settings.getNoise())).replace("E-", "e-").replace(
									"E", "e+"));
							Element right = doc.createElement("right");
							right.setTextContent(("" + (settings.getTrace().getValue(traceVariable,
									timePoint) + settings.getNoise())).replace("E-", "e-").replace(
									"E", "e+"));
							interval.appendChild(left);
							interval.appendChild(right);
							point.appendChild(interval);
						}
						break;
					}
				}
			}
			series.appendChild(point);
		}
		topLevelElement.appendChild(series);
		Element delta = doc.createElement("delta");
		delta.setTextContent(("" + settings.getDelta()).replace("E-", "e-").replace("E", "e+"));
		topLevelElement.appendChild(delta);
		Element epsilon = doc.createElement("epsilon");
		epsilon.setTextContent(("" + settings.getEpsilon()).replace("E-", "e-").replace("E", "e+"));
		topLevelElement.appendChild(epsilon);
		Element noise = doc.createElement("noise");
		noise.setTextContent(("" + settings.getNoise()).replace("E-", "e-").replace("E", "e+"));
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