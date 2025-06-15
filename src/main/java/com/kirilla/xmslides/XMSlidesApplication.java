package com.kirilla.xmslides;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class XMSlidesApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showHomeScene();
        stage.setTitle("XMSlides");
        stage.show();
    }

    public static void showHomeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(XMSlidesApplication.class.getResource("views/home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setupScene(scene);
        primaryStage.setScene(scene);
    }

    public static void showMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(XMSlidesApplication.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        setupScene(scene);
        primaryStage.setScene(scene);
    }

    private static void setupScene(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(
                XMSlidesApplication.class.getResource("css/SyntaxStylesheet.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(
                XMSlidesApplication.class.getResource("css/Main.css")).toExternalForm());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(
                XMSlidesApplication.class.getResourceAsStream("images/XMSlides.png"))));
    }

    public static void main(String[] args) {
        launch();
    }
}