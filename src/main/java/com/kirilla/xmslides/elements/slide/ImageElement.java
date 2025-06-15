package com.kirilla.xmslides.elements.slide;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.util.Map;

public class ImageElement extends VisualElement {
    public static ImageView createImageNode(XMLStreamReader reader) {
        Map<String, String> attributes = getAttributes(reader);

        Image image = new Image(new File(attributes.get("src")).toURI().toString());
        ImageView imageView = new ImageView(image);
        setRotation(imageView, attributes.getOrDefault("rotation",""));
        setOpacity(imageView, attributes.getOrDefault("opacity",""));
        setColorAdjust(imageView, attributes.getOrDefault("contrast",""),attributes.getOrDefault("brightness",""));
        setPadding(imageView, attributes.getOrDefault("padding", ""));
        setAlignment(imageView, attributes.getOrDefault("horizontal_align", "left"), attributes.getOrDefault("vertical_align", "top"));
        setCrop(imageView, attributes.getOrDefault("crop",""));
        setSize(imageView, attributes.getOrDefault("size",""));
        return imageView;
    }

    private static void setRotation(ImageView imageView, String rotation){
        if (rotation.isEmpty()) return;
        imageView.setRotate(Double.parseDouble(rotation));
    }

    private static void setColorAdjust(ImageView imageView, String contrast, String brightness){
        ColorAdjust colorAdjust = new ColorAdjust();
        if (!contrast.isEmpty()) colorAdjust.setContrast(Math.clamp(Double.parseDouble(contrast), -1,1));
        if (!brightness.isEmpty()) colorAdjust.setBrightness(Math.clamp(Double.parseDouble(brightness), -1,1));
        imageView.setEffect(colorAdjust);
    }

    private static void setCrop(ImageView imageView, String crop){
        if(crop.isEmpty()) return;
        String[] sizes = crop.split(",");
        if(sizes.length <= 2) imageView.setClip(new Rectangle(Double.parseDouble(sizes[0]),Double.parseDouble(sizes[sizes.length-1])));
        else if (sizes.length == 4) imageView.setClip(new Rectangle( Double.parseDouble(sizes[2]),Double.parseDouble(sizes[3]), Double.parseDouble(sizes[0]),Double.parseDouble(sizes[1]) ));
    }

    private static void setSize(ImageView imageView, String size){
        if(size.isEmpty()) return;
        String[] sizes = size.split(",");
        if(sizes.length > 2) return;
        imageView.setFitWidth(Double.parseDouble(sizes[0]));
        imageView.setFitHeight(Double.parseDouble(sizes[sizes.length-1]));
    }
}
