package com.kirilla.xmslides.elements.slide;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;

public class VisualElement extends BaseElement {
    static void setOpacity(Node node, String opacity){
        if (!opacity.isEmpty()) node.setOpacity(Double.parseDouble(opacity));
    }
    static void setRotation(Node node, String rotation){if (!rotation.isEmpty()) node.setRotate(Double.parseDouble(rotation));}

    protected static void setPadding(Node node, String padding) {
        if (padding == null || padding.isEmpty()) return;

        padding = padding.replaceAll("\\s+", "");
        String[] parts = padding.split(",");
        Insets insets;

        try {
            switch (parts.length) {
                case 1:
                    double uniform = Double.parseDouble(parts[0]);
                    insets = new Insets(uniform);
                    break;
                case 2:
                    double vertical = Double.parseDouble(parts[0]);
                    double horizontal = Double.parseDouble(parts[1]);
                    insets = new Insets(vertical, horizontal, vertical, horizontal);
                    break;
                case 4:
                    insets = new Insets(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
                    break;
                default:
                    return;
            }

            node.getProperties().put("layout.padding", insets);
        } catch (NumberFormatException e) {
            System.err.println("Invalid padding format: " + padding);
        }
    }

    protected static void setAlignment(Node node, String hAlign, String vAlign) {
        if ((hAlign == null || hAlign.isEmpty()) &&
                (vAlign == null || vAlign.isEmpty())) return;

        node.getProperties().put("layout.hAlign", hAlign != null ? hAlign.toLowerCase() : "left");
        node.getProperties().put("layout.vAlign", vAlign != null ? vAlign.toLowerCase() : "top");
    }

    public static void applyLayout(Node node, Bounds containerBounds) {
        Insets padding = (Insets) node.getProperties().get("layout.padding");
        String hAlign = (String) node.getProperties().get("layout.hAlign");
        String vAlign = (String) node.getProperties().get("layout.vAlign");

        if (padding == null && hAlign == null && vAlign == null) return;

        Bounds nodeBounds = node.getBoundsInParent();
        double availableWidth = containerBounds.getWidth() -
                (padding != null ? padding.getLeft() + padding.getRight() : 0);
        double availableHeight = containerBounds.getHeight() -
                (padding != null ? padding.getTop() + padding.getBottom() : 0);

        double x = padding != null ? padding.getLeft() : 0;
        double y = padding != null ? padding.getTop() : 0;

        if (hAlign != null) {
            switch (hAlign) {
                case "center":
                    x += (availableWidth - nodeBounds.getWidth()) / 2;
                    break;
                case "right":
                    x += availableWidth - nodeBounds.getWidth();
                    break;
            }
        }

        if (vAlign != null) {
            switch (vAlign) {
                case "center":
                    y += (availableHeight - nodeBounds.getHeight()) / 2;
                    break;
                case "bottom":
                    y += availableHeight - nodeBounds.getHeight();
                    break;
            }
        }

        node.setLayoutX(x);
        node.setLayoutY(y);
    }

}

