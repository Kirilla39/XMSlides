package com.kirilla.xmslides.controllers;

import com.kirilla.xmslides.XMSlidesApplication;
import com.kirilla.xmslides.util.ProjectManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ListView<Button> projectsListView;
    @FXML
    private Button openButton;
    @FXML
    private Button newButton;

    private final ProjectManager projectManager = ProjectManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshProjectsList();

        openButton.setOnAction(e -> handleOpen());
        newButton.setOnAction(e -> handleNew());
    }

    private void refreshProjectsList() {
        projectsListView.getItems().clear();
        ProjectManager.getLastProjects().forEach(path -> {
            Button btn = new Button(path);
            btn.setOnAction(e -> openProject(path));
            btn.setMaxWidth(Double.MAX_VALUE);
            projectsListView.getItems().add(btn);
        });
    }

    private void openProject(String path) {
        if (projectManager.isValidProject(path)) {
            ProjectManager.setCurrentProject(path);
            try {
                XMSlidesApplication.showMainScene();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOpen() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Project Folder");
        File selectedDirectory = directoryChooser.showDialog(projectsListView.getScene().getWindow());

        if (selectedDirectory != null) {
            openProject(selectedDirectory.getAbsolutePath());
        }
    }

    private void handleNew() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Create New Project");
        dialog.setContentText("Enter project name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(projectName -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Location for New Project");
            File selectedDirectory = directoryChooser.showDialog(projectsListView.getScene().getWindow());

            if (selectedDirectory != null) {
                String projectPath = selectedDirectory.getAbsolutePath() + File.separator + projectName;
                File projectDir = new File(projectPath);
                if (!projectDir.exists() && projectDir.mkdir()) {
                    openProject(projectPath);
                }
            }
        });
    }
}