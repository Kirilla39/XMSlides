package com.kirilla.xmslides.elements.slide;

import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BaseElement {
    protected static Map<String,String> getAttributes(XMLStreamReader reader){
        Map<String, String> attributes = new HashMap<>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            attributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
        return attributes;
    }
}
