package com.kirilla.xmslides.elements.slide;

import javafx.scene.shape.Rectangle;

import javax.xml.stream.XMLStreamReader;
import java.util.Map;

public class RectangleElement extends ShapeElement{

    public static Rectangle createRectangleNode(XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);
        Rectangle rectangle = new Rectangle();
        setRotation(rectangle,attributes.getOrDefault("rotation",""));
        setStroke(rectangle,attributes.getOrDefault("stroke",""),attributes.getOrDefault("stroke-size","1"),attributes.getOrDefault("stroke-pos","centred"));
        setFill(rectangle,attributes.getOrDefault("fill",""));
        setOpacity(rectangle,attributes.getOrDefault("opacity",""));
        setPosition(rectangle,attributes.getOrDefault("position",""));
        setSize(rectangle,attributes.getOrDefault("size",""));
        return rectangle;

    }

    private static void setPosition(Rectangle rectangle, String position){
        if (position.isEmpty()) return;
        String[] coordinates = position.split(",");
        if (coordinates.length != 2) return;
        rectangle.setX(Double.parseDouble(coordinates[0]));
        rectangle.setY(Double.parseDouble(coordinates[1]));
    }

    private static void setSize(Rectangle rectangle, String size){
        if (size.isEmpty()) return;
        String[] coordinates = size.split(",");
        if (coordinates.length != 2) return;
        rectangle.setWidth(Double.parseDouble(coordinates[0]));
        rectangle.setHeight(Double.parseDouble(coordinates[1]));
    }

}
