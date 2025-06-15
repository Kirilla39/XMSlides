package com.kirilla.xmslides.elements.slide;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.Map;

public class TextElement extends VisualElement {

    public static Text createTextNode(XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);
        Text text = new Text(attributes.getOrDefault("text",getCurrentText(reader)));
        setRotation(text,attributes.getOrDefault("rotation",""));
        setFont(text,attributes.getOrDefault("font-size","12"),attributes.getOrDefault("font",""));
        setColor(text,attributes.getOrDefault("color",""));
        setOpacity(text,attributes.getOrDefault("opacity",""));
        setPadding(text, attributes.getOrDefault("padding", ""));
        setAlignment(text, attributes.getOrDefault("horizontal_align", "left"), attributes.getOrDefault("vertical_align", "top"));
        return text;
    }

    public static String getCurrentText(XMLStreamReader reader){
        try {
            while (reader.hasNext()) {
                int eventType = reader.next();
                if (eventType == XMLStreamConstants.CHARACTERS) return reader.getText().trim();
                else if (eventType == XMLStreamConstants.END_ELEMENT) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void setRotation(Text text, String rotation){
        if (rotation.isEmpty()) return;
        text.setRotate(Double.parseDouble(rotation));
    }

    private static void setFont(Text text, String fontSize, String fontFamily){
        text.setFont(Font.font(fontFamily, Double.parseDouble(fontSize)));
    }

    private static void setColor(Text text, String color){
        if(color.isEmpty()) return;
        text.setFill(Color.web(color));
    }

    private static void setOpacity(Text text, String opacity){
        if (opacity.isEmpty()) return;
        text.setOpacity(Double.parseDouble(opacity));
    }
}
