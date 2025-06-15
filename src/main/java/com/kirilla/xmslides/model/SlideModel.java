package com.kirilla.xmslides.model;

import com.kirilla.xmslides.util.SlideStAXParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class SlideModel {
    private final String path;
    private WritableImage preview;

    public SlideModel(String path){
        this.path = path;
        this.preview = generatePreview(path);
    }

    private static WritableImage generatePreview(Pane slide){
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(slide.getBackground().getFills().getLast().getFill());
        parameters.setTransform(new Scale(0.12,0.12));
        parameters.setViewport(new Rectangle2D(0,0,slide.getPrefWidth()*0.12,slide.getPrefHeight()*0.12));
        return slide.snapshot(parameters, null);
    }

    private static WritableImage generatePreview(String path){
        Pane slide = SlideStAXParser.parseSlide(path);
        return generatePreview(slide);
    }

    private static WritableImage getPreviewCache(String path){
        //TODO
        return null;
    }

    private static void savePreviewToCache(String path){
        //TODO
    }

    public Pane updateSlide(){
        Pane slide = SlideStAXParser.parseSlide(path);
        preview = generatePreview(slide);
        return slide;
    }

    public final WritableImage getPreview(){
        return preview;
    }

    public final String getPath() {
        return path;
    }
}
