package com.kirilla.xmslides.controllers;


import com.kirilla.xmslides.controllers.main_modules.CodeEditor;
import com.kirilla.xmslides.controllers.main_modules.ElementInspector;
import com.kirilla.xmslides.controllers.main_modules.SlideListView;
import com.kirilla.xmslides.controllers.main_modules.SlideWorkspace;
import com.kirilla.xmslides.util.PPTXExporter;
import com.kirilla.xmslides.util.ProjectManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public MenuBar titleBar;
    @FXML
    public BorderPane mainWindow;

    static String basePath = "/com/kirilla/xmslides";

    SlideWorkspace slideWorkspace;
    CodeEditor codeEditor;
    SlideListView slideListView;

    //TODO Rework that afwul piece of code
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SplitPane center = (SplitPane) mainWindow.getCenter();
        try {
            FXMLLoader workspaceLoader = new FXMLLoader(getClass().getResource(basePath+"/views/main_modules/slide_workspace.fxml"));
            center.getItems().add(workspaceLoader.load());
            slideWorkspace = workspaceLoader.getController();

            FXMLLoader codeEditorLoader = new FXMLLoader(getClass().getResource(basePath+"/views/main_modules/code_editor.fxml"));
            center.getItems().add(codeEditorLoader.load());
            codeEditor = codeEditorLoader.getController();

            FXMLLoader inspectorLoader = new FXMLLoader(getClass().getResource(basePath+"/views/main_modules/element_inspector.fxml"));
            mainWindow.setTop(inspectorLoader.load());
            ElementInspector inspector = inspectorLoader.getController();

            FXMLLoader slideListLoader = new FXMLLoader(getClass().getResource(basePath+"/views/main_modules/slide_list_view.fxml"));
            mainWindow.setLeft(slideListLoader.load());
            slideListView = slideListLoader.getController();

            if (inspector != null && codeEditor != null) {
                inspector.setCodeEditor(codeEditor);
            } else {
                System.err.println("Failed to initialize ElementInspector or CodeEditor");
            }

            codeEditor.setWorkspace(slideWorkspace);
            codeEditor.setMain(this);
            slideListView.setCodeEditor(codeEditor);
            slideListView.setWorkspace(slideWorkspace);
            mainWindow.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if(event.isControlDown()) {
                    switch (event.getCode()){
                        case KeyCode.E: handleExport(); break;
                    }
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + e.getMessage(), e);
        }
    }

    public void Update(){
        slideWorkspace.loadSlide();
        slideListView.Update();
    }

    @FXML
    private void handleExport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PowerPoint Files", "*.pptx")
            );

            File file = fileChooser.showSaveDialog(mainWindow.getScene().getWindow());
            if (file != null) {
                PPTXExporter.exportToPPTX(ProjectManager.getSlides(), file.getAbsolutePath());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}