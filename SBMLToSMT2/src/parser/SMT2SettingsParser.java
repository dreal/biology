package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.SMT2Settings;
import util.Trace;
import util.Utility;
import util.Utility.Tuple;

public class SMT2SettingsParser {

	public static SMT2Settings parseSettingsFile(String filename) throws ParserConfigurationException,
			SAXException, IOException {
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
				double left = Double.parseDouble(((Element) ((Element) var.getElementsByTagName("domain").item(0)).getElementsByTagName("left").item(0)).getTextContent());
				double right = Double.parseDouble(((Element) ((Element) var.getElementsByTagName("domain").item(0)).getElementsByTagName("right").item(0)).getTextContent());
				variables.put(name.getTextContent(), new Utility().new Tuple<Double, Double>(left, right));
			}
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
		Element odes = doc.createElement("odes");
		for (String variable : settings.getODEVariables()) {
			Element ode = doc.createElement("ode");
			ode.setTextContent("(= d/dt[" + variable + "] " + Utility.prefixASTNodeToString(settings.getODE(variable)) + ")");
			odes.appendChild(ode);
		}
		topLevelElement.appendChild(odes);
		Element series = doc.createElement("series");
		for(double timePoint : settings.getTrace().getTimePoints()) {
			Element point = doc.createElement("point");
			point.setAttribute("time", "" + timePoint);
			for (String variable : settings.getTrace().getVariables()) {
				if (settings.getODEVariables().contains(variable)) {
					Element interval = doc.createElement("interval");
					interval.setAttribute("var", variable);
					Element left = doc.createElement("left");
					left.setTextContent("" + (settings.getTrace().getValue(variable, timePoint) - settings.getNoise()));
					Element right = doc.createElement("right");
					right.setTextContent("" + (settings.getTrace().getValue(variable, timePoint) + settings.getNoise()));
					interval.appendChild(left);
					interval.appendChild(right);
					point.appendChild(interval);
				}
			}
			series.appendChild(point);
		}
		topLevelElement.appendChild(series);
		Element delta = doc.createElement("delta");
		delta.setTextContent("" + settings.getDelta());
		topLevelElement.appendChild(delta);
		Element epsilon = doc.createElement("epsilon");
		epsilon.setTextContent("" + settings.getEpsilon());
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
