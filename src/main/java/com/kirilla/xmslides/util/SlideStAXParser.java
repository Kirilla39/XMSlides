package com.kirilla.xmslides.util;

import com.kirilla.xmslides.elements.slide.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;


import static com.kirilla.xmslides.elements.SlideElement.configureSlide;
import static com.kirilla.xmslides.elements.slide.GroupElement.updateGroupLayout;
import static com.kirilla.xmslides.elements.slide.VisualElement.applyLayout;


public class SlideStAXParser {
    public static Pane parseSlide(String filePath) {
        Pane slidePane = new Pane();
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(filePath));

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName();
                    Node node = createNode(reader, slidePane);
                    if (node != null) {
                        slidePane.getChildren().add(node);
                    }
                }
            }
            reader.close();

            applyLayoutToPane(slidePane);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slidePane;
    }

    private static Node createNode(XMLStreamReader reader, Pane parent) {
        switch (reader.getLocalName()) {
            case "Text":
                Node text = TextElement.createTextNode(reader);
                applyLayout(text, parent.getBoundsInLocal());
                return text;
            case "Image":
                Node image = ImageElement.createImageNode(reader);
                applyLayout(image, parent.getBoundsInLocal());
                return image;
            case "Slide":
                configureSlide(parent, reader);
                return null;
            case "Rectangle":
                Node rect = RectangleElement.createRectangleNode(reader);
                applyLayout(rect, parent.getBoundsInLocal());
                return rect;
            case "Ellipse":
                Node ellipse = EllipseElement.createEllipseNode(reader);
                applyLayout(ellipse, parent.getBoundsInLocal());
                return ellipse;
            case "Group":
                Group group = GroupElement.createGroupNode(reader);
                applyLayout(group, parent.getBoundsInLocal());
                return group;
            default: return null;
        }
    }

    private static void applyLayoutToPane(Pane pane) {
        Bounds parentBounds = pane.getParent() != null ?
                pane.getParent().getBoundsInLocal() :
                new BoundingBox(0, 0, 1920, 1080);

        for (Node node : pane.getChildren()) {
            if (node instanceof Group) {
                updateGroupLayout((Group) node);
            } else {
                applyLayout(node, parentBounds);
            }
        }
    }
}