package com.kirilla.xmslides.elements.slide;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.Map;

import static com.kirilla.xmslides.elements.slide.BaseElement.getAttributes;
import static com.kirilla.xmslides.elements.slide.VisualElement.*;

public class GroupElement {
    public static Group createGroupNode(XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);
        Group group = new Group();


        setRotation(group, attributes.getOrDefault("rotation", ""));
        setOpacity(group, attributes.getOrDefault("opacity", ""));
        setPadding(group, attributes.getOrDefault("padding", ""));
        setAlignment(group,
                attributes.getOrDefault("horizontal_align", ""),
                attributes.getOrDefault("vertical_align", ""));

        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    Node child = createChildNode(reader);
                    if (child != null) {
                        group.getChildren().add(child);
                    }
                }
                else if (event == XMLStreamConstants.END_ELEMENT && "Group".equals(reader.getLocalName())) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateGroupLayout(group);
        return group;
    }

    private static Node createChildNode(XMLStreamReader reader) {
        String elementName = reader.getLocalName();
        switch (elementName) {
            case "Text": return TextElement.createTextNode(reader);
            case "Image": return ImageElement.createImageNode(reader);
            case "Rectangle": return RectangleElement.createRectangleNode(reader);
            case "Ellipse": return EllipseElement.createEllipseNode(reader);
            case "Group": return createGroupNode(reader);
            default: return null;
        }
    }

    public static void updateGroupLayout(Group group) {
        Bounds contentBounds = calculateContentBounds(group);

        for (Node child : group.getChildren()) {
            applyLayout(child, contentBounds);
        }

        if (group.getProperties().containsKey("layout.padding") ||
                group.getProperties().containsKey("layout.hAlign") ||
                group.getProperties().containsKey("layout.vAlign")) {

            Bounds parentBounds = group.getParent() != null ?
                    group.getParent().getBoundsInLocal() :
                    new BoundingBox(0, 0, 1280, 720);

            applyLayout(group, parentBounds);
        }
    }

    private static Bounds calculateContentBounds(Group group) {
        if (group.getChildren().isEmpty()) {
            return new BoundingBox(0, 0, 0, 0);
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Node child : group.getChildren()) {
            Bounds childBounds = child.getBoundsInParent();
            minX = Math.min(minX, childBounds.getMinX());
            minY = Math.min(minY, childBounds.getMinY());
            maxX = Math.max(maxX, childBounds.getMaxX());
            maxY = Math.max(maxY, childBounds.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }
}