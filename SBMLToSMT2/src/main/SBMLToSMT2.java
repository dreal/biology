package main;

import parser.SMT2SettingsParser;
import util.SMT2Settings;
import gui.Gui;

/**
 * 
 * This class represents a command line program that will read in an SBML file and a SMT formula
 * property and produce an SMT2 file that can be analyzed using a SMT2 solver such as dReal.
 * 
 * @author Curtis Madsen
 * 
 */
public class SBMLToSMT2 {

	/**
	 * 
	 * The main method that is executed when the program is run from the command line.
	 * 
	 * @param args
	 *            - the program arguments
	 */
	public static void main(String[] args) {
		new Gui();
		SMT2Settings settings;
		try {
			settings = SMT2SettingsParser.parseSettingsFile("D:\\data.xml");
			SMT2SettingsParser.writeSettingsToFile("D:\\test.xml", settings);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (args.length > 2) {
//			try {
//				String prop = args[2];
//				for (int i = 3; i < args.length; i++) {
//					prop += args[i];
//				}
//				IFormulaParser parser = new FormulaParserLL3(new StringReader(""));
//				ASTNode property = ASTNode.parseFormula(prop, parser);
//				System.out.println(Utility.writeSMT2ToString(SBMLReader.read(new File(args[0])),
//						TraceParser.parseCopasiOutput(new File(args[1])), property));
//			}
//			catch (Exception e) {
//				System.out.println("Invalid arguments.");
//				System.out
//						.println("Usage:  java SBMLToSMT2 <sbml_file> <trace_file> <property_to_check>");
//			}
//		}
//		else {
//			if (args.length > 1) {
//				try {
//					System.out.println(Utility.writeSMT2ToString(
//							SBMLReader.read(new File(args[0])),
//							TraceParser.parseCopasiOutput(new File(args[1])), null));
//				}
//				catch (Exception e) {
//					System.out.println("Invalid arguments.");
//					System.out.println("Usage:  java SBMLToSMT2 <sbml_file> <trace_file>");
//				}
//			}
//			else {
//				System.out.println("Invalid arguments.");
//				System.out.println("Usage:  java SBMLToSMT2 <sbml_file> <trace_file>");
//			}
//		}
	}
}
