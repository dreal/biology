package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import util.Settings;

public class SettingsParser {

	public static Settings parseSettingsFile(String filename) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(filename));
		Element settings = (Element) doc.getElementsByTagName("settings").item(0);
		String sbmlFile = settings.getElementsByTagName("sbml").item(0).getTextContent();
		String timeSeriesFile = settings.getElementsByTagName("series").item(0).getTextContent();
		ArrayList<String> params = new ArrayList<String>();
		NodeList elements = settings.getElementsByTagName("param");
		for (int i = 0; i < elements.getLength(); i++) {
			params.add(elements.item(i).getTextContent());
		}
		double noise = Double.parseDouble(settings.getElementsByTagName("noise").item(0)
				.getTextContent());
		double precision = Double.parseDouble(settings.getElementsByTagName("precision").item(0)
				.getTextContent());
		return new Settings(sbmlFile, timeSeriesFile, params, noise, precision);
	}

	public static void writeSettingsToFile(String filename, Settings settings)
			throws ParserConfigurationException, TransformerFactoryConfigurationError,
			FileNotFoundException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element topLevelElement = doc.createElement("settings");
		Element sbmlFile = doc.createElement("sbml");
		sbmlFile.setTextContent(settings.getSBMLFile());
		topLevelElement.appendChild(sbmlFile);
		Element timeSeriesFile = doc.createElement("series");
		timeSeriesFile.setTextContent(settings.getTimeSeriesFile());
		topLevelElement.appendChild(timeSeriesFile);
		for (String param : settings.getParams()) {
			Element params = doc.createElement("param");
			params.setTextContent(param);
			topLevelElement.appendChild(params);
		}
		Element noise = doc.createElement("noise");
		noise.setTextContent("" + settings.getNoise());
		topLevelElement.appendChild(noise);
		Element precision = doc.createElement("precision");
		precision.setTextContent("" + settings.getPrecision());
		topLevelElement.appendChild(precision);
		doc.appendChild(topLevelElement);
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
		tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(filename)));
	}

}
