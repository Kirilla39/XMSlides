package com.kirilla.xmslides.util;

import com.kirilla.xmslides.model.SlideModel;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.xslf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PPTXExporter {

    public static void exportToPPTX(List<SlideModel> slides, String outputPath) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        if (!slides.isEmpty()) {
            Pane firstSlide = SlideStAXParser.parseSlide(slides.getFirst().getPath());
            if (firstSlide.getWidth() <= 0 || firstSlide.getHeight() <= 0) {
                firstSlide.autosize();
            }

            ppt.setPageSize(new Dimension(
                    (int)Math.max(firstSlide.getPrefWidth(), firstSlide.getBoundsInLocal().getWidth()),
                    (int)Math.max(firstSlide.getPrefHeight(), firstSlide.getBoundsInLocal().getHeight())
            ));
        }

        for (SlideModel slideModel : slides) {
            Pane slidePane = SlideStAXParser.parseSlide(slideModel.getPath());
            if (slidePane.getWidth() <= 0 || slidePane.getHeight() <= 0) {
                slidePane.autosize();
            }

            XSLFSlide pptSlide = ppt.createSlide();
            setSlideBackground(pptSlide, slidePane);
            processNodes(slidePane, pptSlide, slidePane);
        }

        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            ppt.write(out);
        }
    }

    private static void setSlideBackground(XSLFSlide slide, Pane slidePane) {
        Color bgColor = (Color) slidePane.getBackground().getFills().getFirst().getFill();
        slide.getXmlObject().getCSld().addNewBg();
        slide.getBackground().setFillColor(new java.awt.Color(
                (float)bgColor.getRed(),
                (float)bgColor.getGreen(),
                (float)bgColor.getBlue(),
                (float)bgColor.getOpacity()
        ));
    }

    private static void processNodes(Pane parent, XSLFSlide pptSlide, Pane rootPane) {
        for (Node node : parent.getChildren()) {
            if (node instanceof Pane) {
                processNodes((Pane)node, pptSlide, rootPane);
            }
            else if (node instanceof Group) {
                processGroup((Group)node, pptSlide, rootPane);
            }
            else if (node instanceof Text) {
                addTextToSlide((Text)node, pptSlide, rootPane);
            }
            else if (node instanceof ImageView) {
                addImageToSlide((ImageView)node, pptSlide, rootPane);
            }
            else if (node instanceof Shape) {
                addShapeToSlide((Shape)node, pptSlide, rootPane);
            }
        }
    }

    private static void processGroup(Group group, XSLFSlide pptSlide, Pane rootPane) {
        for (Node node : group.getChildren()) {
            if (node instanceof Text) {
                addTextToSlide((Text)node, pptSlide, rootPane);
            }
            else if (node instanceof ImageView) {
                addImageToSlide((ImageView)node, pptSlide, rootPane);
            }
            else if (node instanceof Shape) {
                addShapeToSlide((Shape)node, pptSlide, rootPane);
            }
            else if (node instanceof Group) {
                processGroup((Group)node, pptSlide, rootPane);
            }
        }
    }

    private static void addTextToSlide(Text textNode, XSLFSlide pptSlide, Pane rootPane) {
        XSLFTextBox shape = pptSlide.createTextBox();
        shape.setText(textNode.getText() + " ");
        shape.setAnchor(calculatePptBounds(textNode, rootPane, pptSlide));

        XSLFTextRun run = shape.getTextParagraphs().getFirst().getTextRuns().getFirst();
        run.setFontColor(toAwtColor((Color)textNode.getFill(), textNode.getOpacity()));
        run.setFontFamily(textNode.getFont().getFamily());
        run.setFontSize(textNode.getFont().getSize());

        if (textNode.getRotate() != 0) {
            shape.setRotation(textNode.getRotate());
        }
    }

    private static void addImageToSlide(ImageView imageView, XSLFSlide pptSlide, Pane rootPane) {
        try {
            Image image = imageView.getImage();
            String url = image.getUrl();

            if (url != null && url.startsWith("file:")) {
                File imageFile = new File(url.replace("file:", ""));
                if (imageFile.exists()) {
                    BufferedImage processed = processImage(imageFile, imageView);
                    File temp = saveTempImage(processed);

                    XSLFPictureData pd = pptSlide.getSlideShow().addPicture(temp, PictureData.PictureType.PNG);
                    XSLFPictureShape picture = pptSlide.createPicture(pd);
                    setImageProperties(picture, imageView, rootPane, pptSlide);

                    temp.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Image export error: " + e.getMessage());
        }
    }

    private static BufferedImage processImage(File source, ImageView imageView) throws IOException {
        BufferedImage original = ImageIO.read(source);
        BufferedImage scaled = scaleImage(original, imageView);
        return cropImage(scaled, imageView);
    }

    private static BufferedImage scaleImage(BufferedImage original, ImageView imageView) {
        if (imageView.getFitWidth() <= 0 || imageView.getFitHeight() <= 0) {
            return original;
        }

        BufferedImage scaled = new BufferedImage(
                (int)imageView.getFitWidth(),
                (int)imageView.getFitHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = scaled.createGraphics();
        g.drawImage(original, 0, 0, scaled.getWidth(), scaled.getHeight(), null);
        g.dispose();
        return scaled;
    }

    private static BufferedImage cropImage(BufferedImage image, ImageView imageView) {
        if (!(imageView.getClip() instanceof Rectangle clip)) {
            return image;
        }

        return image.getSubimage(
                (int)clip.getX(),
                (int)clip.getY(),
                (int)clip.getWidth(),
                (int)clip.getHeight()
        );
    }

    private static File saveTempImage(BufferedImage image) throws IOException {
        File temp = File.createTempFile("pptimg", ".png");
        ImageIO.write(image, "png", temp);
        return temp;
    }

    private static void setImageProperties(XSLFPictureShape picture, ImageView imageView,
                                           Pane rootPane, XSLFSlide slide) {
        Rectangle2D bounds = calculatePptBounds(imageView, rootPane, slide);
        double width = getImageWidth(imageView);
        double height = getImageHeight(imageView);

        picture.setAnchor(new Rectangle2D.Double(
                bounds.getX(), bounds.getY(), width, height
        ));

        if (imageView.getRotate() != 0) {
            picture.setRotation(imageView.getRotate());
        }
    }

    private static double getImageWidth(ImageView imageView) {
        if (imageView.getClip() instanceof Rectangle) {
            return ((Rectangle)imageView.getClip()).getWidth();
        }
        return imageView.getFitWidth() > 0 ? imageView.getFitWidth() : imageView.getImage().getWidth();
    }

    private static double getImageHeight(ImageView imageView) {
        if (imageView.getClip() instanceof Rectangle) {
            return ((Rectangle)imageView.getClip()).getHeight();
        }
        return imageView.getFitHeight() > 0 ? imageView.getFitHeight() : imageView.getImage().getHeight();
    }

    private static void addShapeToSlide(Shape shapeNode, XSLFSlide pptSlide, Pane rootPane) {
        XSLFAutoShape shape = pptSlide.createAutoShape();
        shape.setShapeType(getShapeType(shapeNode));
        shape.setAnchor(calculatePptBounds(shapeNode, rootPane, pptSlide));

        if (shapeNode.getFill() instanceof Color) {
            shape.setFillColor(toAwtColor((Color)shapeNode.getFill(), shapeNode.getOpacity()));
        }

        if (shapeNode.getStroke() instanceof Color stroke) {
            shape.setLineColor(toAwtColor(stroke, shapeNode.getOpacity()));
            shape.setLineWidth(shapeNode.getStrokeWidth());
        }

        if (shapeNode.getRotate() != 0) {
            shape.setRotation(shapeNode.getRotate());
        }
    }

    private static ShapeType getShapeType(Shape shape) {
        return shape instanceof Ellipse ? ShapeType.ELLIPSE : ShapeType.RECT;
    }

    private static java.awt.Color toAwtColor(Color color, double opacity) {
        return new java.awt.Color(
                (float)color.getRed(),
                (float)color.getGreen(),
                (float)color.getBlue(),
                (float)opacity
        );
    }

    private static Rectangle2D calculatePptBounds(Node node, Pane rootPane, XSLFSlide slide) {
        Bounds bounds = getAbsoluteBounds(node, rootPane);
        Dimension pptSize = slide.getSlideShow().getPageSize();

        double scaleX = pptSize.getWidth() / rootPane.getWidth();
        double scaleY = pptSize.getHeight() / rootPane.getHeight();

        return new Rectangle2D.Double(
                bounds.getMinX() * scaleX,
                bounds.getMinY() * scaleY,
                bounds.getWidth() * scaleX,
                bounds.getHeight() * scaleY
        );
    }

    private static Bounds getAbsoluteBounds(Node node, Pane rootPane) {
        Bounds bounds = node.localToParent(node.getBoundsInLocal());
        Node parent = node.getParent();

        while (parent != null && parent != rootPane) {
            bounds = parent.localToParent(bounds);
            parent = parent.getParent();
        }
        return bounds;
    }
}