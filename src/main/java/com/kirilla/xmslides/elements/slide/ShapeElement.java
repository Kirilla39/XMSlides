package com.kirilla.xmslides.elements.slide;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

public abstract class ShapeElement extends VisualElement {
    static void setStroke(Shape shape, String stroke, String strokeWidth, String type){
        if (stroke.isEmpty()) return;
        shape.setStroke(Color.web(stroke));
        shape.setStrokeWidth(Double.parseDouble(strokeWidth));
        switch (type){
            case "centered":
                shape.setStrokeType(StrokeType.CENTERED);
                break;
            case "outside":
                shape.setStrokeType(StrokeType.OUTSIDE);
                break;
            case "inside":
                shape.setStrokeType(StrokeType.INSIDE);
                break;
        }
    }
    static void setFill(Shape shape, String fill){if (!fill.isEmpty()) shape.setFill(Color.web(fill));}
}
