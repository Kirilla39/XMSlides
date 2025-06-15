package com.kirilla.xmslides.controllers.show_mode;

import com.kirilla.xmslides.controllers.main_modules.SlideListView;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PresentationView {
    private final Stage presentationStage = new Stage();
    private final StackPane rootPane;
    private final SlideListView slideList;

    public PresentationView(SlideListView slideList) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/kirilla/xmslides/views/show_mode/presentation-view.fxml"));
            rootPane = loader.load();
            this.slideList = slideList;
            presentationStage.initStyle(StageStyle.UNDECORATED);
            presentationStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()){
                    case KeyCode.ESCAPE: presentationStage.close(); break;
                    case KeyCode.LEFT: switchSlide(-1); break;
                    case KeyCode.RIGHT: switchSlide(1); break;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load presentation view", e);
        }
    }
    //TODO Fix that code structure
    private void switchSlide(int step) {
        System.out.println("test");
        slideList.selectSlide(slideList.getCurrentSlide() + step);
        rootPane.getChildren().clear();
        rootPane.getChildren().add(slideList.slidesList.get(slideList.getCurrentSlide()).updateSlide());
    }

    public void showPresentation(Node slideContent, Screen screen) {
        rootPane.getChildren().add(slideContent);
        Rectangle2D bounds = screen.getVisualBounds();
        presentationStage.setX(bounds.getMinX());
        presentationStage.setY(bounds.getMinY());
        presentationStage.setWidth(bounds.getWidth());
        presentationStage.setHeight(bounds.getHeight());

        if (presentationStage.getScene() == null) {presentationStage.setScene(new Scene(rootPane));}
        presentationStage.setFullScreen(true);
        presentationStage.show();
        presentationStage.toFront();
    }
}
