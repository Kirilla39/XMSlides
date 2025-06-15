package com.kirilla.xmslides.elements;

import com.kirilla.xmslides.elements.slide.VisualElement;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.xml.stream.XMLStreamReader;
import java.util.Map;

public class SlideElement extends VisualElement {
    public static void configureSlide(Pane slidePane, XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);

        setRatio(slidePane,attributes.getOrDefault("ratio","16:9"));
        setColor(slidePane,attributes.getOrDefault("color","#FFF"));
    }

    private static void setRatio(Pane pane, String ratio){
        if (ratio.isEmpty()) return;
        String[] ratioSides = ratio.split(":");
        if (ratioSides.length < 2) return;
        pane.setPrefSize( Integer.parseInt(ratioSides[0])*120,Integer.parseInt(ratioSides[1])*120);
        pane.setClip(new Rectangle(Integer.parseInt(ratioSides[0])*120,Integer.parseInt(ratioSides[1])*120));
    }

    private static void setColor(Pane pane, String color){
        if (color.isEmpty()) return;
        pane.setBackground(new Background(new BackgroundFill(Color.web(color),null,null)));
    }
}
