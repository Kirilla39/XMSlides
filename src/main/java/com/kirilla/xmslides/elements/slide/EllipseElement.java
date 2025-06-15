package com.kirilla.xmslides.elements.slide;

import javafx.scene.shape.Ellipse;

import javax.xml.stream.XMLStreamReader;
import java.util.Map;

public class EllipseElement extends ShapeElement{

    public static Ellipse createEllipseNode(XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);

        Ellipse ellipse = new Ellipse();
        setRotation(ellipse,attributes.getOrDefault("rotation",""));
        setStroke(ellipse,attributes.getOrDefault("stroke",""),attributes.getOrDefault("stroke-size","1"),attributes.getOrDefault("stroke-pos","centred"));
        setFill(ellipse,attributes.getOrDefault("fill","#FFF"));
        setOpacity(ellipse,attributes.getOrDefault("opacity",""));
        setPosition(ellipse,attributes.getOrDefault("position",""));
        setRadius(ellipse,attributes.getOrDefault("radius",""));
        return ellipse;

    }

    private static void setPosition(Ellipse ellipse, String position){
        if (position.isEmpty()) return;
        String[] coordinates = position.split(",");
        if (coordinates.length != 2) return;
        ellipse.setCenterX(Double.parseDouble(coordinates[0]));
        ellipse.setCenterY(Double.parseDouble(coordinates[1]));
    }

    private static void setRadius(Ellipse ellipse, String size){
        if (size.isEmpty()) return;
        String[] coordinates = size.split(",");
        if (coordinates.length > 2) return;
        ellipse.setRadiusY(Double.parseDouble(coordinates[coordinates.length-1]));
        ellipse.setRadiusX(Double.parseDouble(coordinates[0]));
    }
}
