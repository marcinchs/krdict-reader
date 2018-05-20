package org.mchs.dict.local;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

class WriteXMLFile {

    static String createIdxEntry(String entryName, Set<String> inflections, int entryId) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("idx:entry");
        doc.appendChild(rootElement);
        rootElement.setAttribute("name", "english");
        rootElement.setAttribute("scriptable", "yes");
        rootElement.setAttribute("spell", "yes");

        // staff elements
        Element idxShort = doc.createElement("idx:short");
        rootElement.appendChild(idxShort);

        Element idxShortAId = doc.createElement("a");
        idxShortAId.setAttribute("id", Integer.toString(entryId));

        idxShort.appendChild(idxShortAId);

        Element idxOrth = doc.createElement("idx:orth");
        idxOrth.setAttribute("value", entryName);

        Element boldEntryTitle = doc.createElement("b");
        boldEntryTitle.setTextContent(entryName);

        idxOrth.appendChild(boldEntryTitle);

        if (inflections != null && inflections.size() > 0) {
            Element idxInfl = doc.createElement("idx:infl");
            for (String infl : inflections) {
                Element idxIform = doc.createElement("idx:iform");
                idxIform.setAttribute("value", infl);
                idxInfl.appendChild(idxIform);
            }
            idxOrth.appendChild(idxInfl);
        }

        idxShort.appendChild(idxOrth);

        Element mainEntryContent = doc.createElement("p");
        mainEntryContent.setTextContent("%s");

        idxShort.appendChild(mainEntryContent);

        // write the content
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
}
