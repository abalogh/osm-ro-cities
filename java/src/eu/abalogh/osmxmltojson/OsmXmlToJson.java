package eu.abalogh.osmxmltojson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * Simple utility file to parse an osm XML file and extract the streetNames as
 * json file
 * 
 * @author abalogh
 *
 */
public class OsmXmlToJson {

	public static final List<String> IGNORED_STREET_TYPES = Arrays
			.asList(new String[] { "footway", "construction", "proposed" });

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				usage();
			}

			String inputFile = args[0];
			String outputFile = null;
			if (args.length > 1) {
				outputFile = args[1];
			} else if (inputFile.lastIndexOf('.') > 0) {
				outputFile = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".json";
			} else {
				outputFile = "output.json";
			}
			String cityName = "";
			if (args.length > 2) {
				cityName = args[2];
			} else if (inputFile.indexOf('_') > 0) {
				if (inputFile.lastIndexOf(File.separatorChar) > 0
						&& inputFile.lastIndexOf(File.separatorChar) < inputFile.indexOf('_')) {

					cityName = inputFile.substring(inputFile.lastIndexOf(File.separatorChar) + 1,
							inputFile.lastIndexOf('_'));
				} else {
					cityName = inputFile.substring(0, inputFile.lastIndexOf('_'));
				}
			} else {
				cityName = "SomeCity";
			}
			String altLocale = null;

			if (args.length > 3) {
				altLocale = args[3];
			} else if (inputFile.indexOf('_') > 0) {
				// assume file name format is cityname_citynamealtlocale.osm, activate altLocale
				altLocale = "Hu";
			}
			parseFile(inputFile, outputFile, cityName, altLocale);
			System.out.println("Success");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseFile(String inputFile, String outputFile, String cityName, String altLocale)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory fact = SAXParserFactory.newInstance();
		SAXParser parser = fact.newSAXParser();

		File f = new File(inputFile);
		InputStream inputStream = new FileInputStream(f);
		Reader reader = new InputStreamReader(inputStream, "UTF-8");

		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");

		final String altLocaleName;
		if (altLocale != null) {
			altLocaleName = "name:" + altLocale.toLowerCase();
		} else {
			altLocaleName = null;
		}

		parser.parse(is, new DefaultHandler() {
			private Map<String, String> streets = new TreeMap<>();
			private boolean inWaySection = false;
			private boolean isStreet = false;
			private String streetName;
			private String streetNameHu;

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				if (qName.equals("way")) {
					inWaySection = true;
				}
				if (qName.equals("tag")) {

					if ("addr:street".equals(attributes.getValue("k")) && !"".equals(attributes.getValue("v"))) {
						streets.put(attributes.getValue("v"), "");
					}

					if (inWaySection) {
						if ("highway".equals(attributes.getValue("k"))
								&& !IGNORED_STREET_TYPES.contains(attributes.getValue("v"))) {
							isStreet = true;
						}

						if ("name".equals(attributes.getValue("k"))) {
							streetName = attributes.getValue("v");
						}
						if (altLocaleName != null && altLocaleName.equals(attributes.getValue("k"))) {
							streetNameHu = attributes.getValue("v");
						}
					}
				}

			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equals("way")) {

					if (isStreet && streetName != null && streetName.length() > 0) {
						streets.put(streetName, streetNameHu);
					}

					inWaySection = false;
					isStreet = false;
					streetName = null;
					streetNameHu = null;

				}
			}

			@Override
			public void endDocument() throws SAXException {

				StringBuilder sb = new StringBuilder();
				sb.append(String.format("{\"cityName\":\"%s\",", cityName));
				sb.append(System.lineSeparator());
				sb.append(" \"streets\":[");
				int i = 0;
				for (String s : streets.keySet()) {
					if (i > 0) {
						sb.append(",");
					}
					i++;
					String streetId = String.format("%d_%d", Math.abs(cityName.hashCode()), Math.abs(s.hashCode()));
					if (altLocale != null) {
						sb.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"name%s\":\"%s\"}", streetId, s,
								altLocale, streets.get(s) != null ? streets.get(s) : ""));
					} else {
						sb.append(String.format("{\"id\":\"%s\",\"name\":\"%s\"}", streetId, s));
					}
					sb.append(System.lineSeparator());
				}
				sb.append("] }");

				try {
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
					out.write(sb.toString());
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	private static void usage() {
		System.err.println("Usage: OsmXmlToJson <file_input.osm> <file_output.json?> <cityname?>");
		System.exit(1);
	}

}