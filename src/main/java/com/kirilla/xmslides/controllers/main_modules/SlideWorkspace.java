package com.kirilla.xmslides.controllers.main_modules;

import com.kirilla.xmslides.model.SlideModel;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;

public class SlideWorkspace {
    @FXML
    public ScrollPane workspace;
    @FXML
    private StackPane slideContainer;
    @FXML
    private Scale zoomTransform;

    private static final double ZOOM_FACTOR = 1.1;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 2.0;

    private SlideModel slide;

    @FXML
    public void initialize() {
        workspace.setOnScroll(this::handleZoom);
    }

    private void handleZoom(ScrollEvent event) {
        if (event.isControlDown()) {
            double newZoom = zoomTransform.getX() * (event.getDeltaY() > 0 ? ZOOM_FACTOR : 1 / ZOOM_FACTOR);
            if (newZoom >= MIN_ZOOM && newZoom <= MAX_ZOOM) {
                zoomTransform.setX(newZoom); zoomTransform.setY(newZoom);
            }
            event.consume();
        }
    }

    public void showSlide(Pane currentSlide) {
        slideContainer.getChildren().clear();
        slideContainer.getChildren().add(currentSlide);
    }

    public void loadSlide() { showSlide(slide.updateSlide()); }

    public void setSlide(SlideModel slide){
        this.slide = slide;
    }

}