package com.kirilla.xmslides.controllers.main_modules;

import com.kirilla.xmslides.controllers.show_mode.PresentationView;
import com.kirilla.xmslides.dialogs.ScreenSelector;
import com.kirilla.xmslides.model.SlideModel;
import com.kirilla.xmslides.util.ProjectManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SlideListView implements Initializable {
    public ListView<Pane> slideListView;
    public Button addSlideButton;
    private CodeEditor codeEditor;
    private SlideWorkspace workspace;
    private int currentSlide;
    public ObservableList<SlideModel> slidesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSlides();
        addSlideButton.setOnMouseClicked(event -> createSlide());
        showSlides();

        slideListView.setCellFactory(lv -> {
            SlideListCell cell = new SlideListCell();

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(cell.getIndex()));
                    db.setContent(content);
                    event.consume();
                }
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    int dropIndex = cell.isEmpty() ? slidesList.size() : cell.getIndex();

                    if (draggedIndex != dropIndex) {
                        SlideModel draggedSlide = slidesList.get(draggedIndex);
                        slidesList.remove(draggedIndex);
                        slidesList.add(dropIndex > draggedIndex ? dropIndex - 1 : dropIndex, draggedSlide);
                        ProjectManager.saveSlideOrder(slidesList);
                        Update();
                    }
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
                event.consume();
            });

            return cell;
        });

        slideListView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case KeyCode.R: Update(); break;
                    case KeyCode.F5: showSlideAsPresentation(); break;
                }
            }
        });
    }

    private static class SlideListCell extends javafx.scene.control.ListCell<Pane> {
        @Override
        protected void updateItem(Pane item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                setGraphic(item);
            }
        }
    }

    public void showSlides() {
        slideListView.getItems().clear();
        slidesList.forEach(slide -> {
            Pane slideNode = new Pane();
            slideNode.getChildren().add(new ImageView(slide.getPreview()));
            slideNode.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) selectSlide(slidesList.indexOf(slide));
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) deleteSlide(slidesList.indexOf(slide));
            });
            slideListView.getItems().add(slideNode);
        });
    }

    private void showSlideAsPresentation() {
        ScreenSelector.show(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null),
                selectedScreen -> {
                    new PresentationView(this).showPresentation(
                            slidesList.get(currentSlide).updateSlide(),
                            selectedScreen
                    );
                });
    }

    public void setCodeEditor(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void setWorkspace(SlideWorkspace workspace) {
        this.workspace = workspace;
    }

    public int getCurrentSlide() {
        return currentSlide;
    }

    public void selectSlide(int index) {
        currentSlide = index;
        workspace.setSlide(slidesList.get(index));
        codeEditor.setXmlFile(new File(slidesList.get(index).getPath()));
    }

    public void createSlide() {
        String basepath = ProjectManager.getCurrentProject();
        File[] slides = new File(basepath).listFiles((dir, name) -> name.toLowerCase().endsWith("-slide.xml"));
        try {
            FileWriter fw = new FileWriter(basepath + (slides.length + 1) + "-slide.xml");
            fw.write("<Slide ratio=\"16:9\">\n</Slide>");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadSlides();
        Update();
    }

    public void deleteSlide(int slide) {
        new File(slidesList.get(slide).getPath()).delete();
        slidesList.remove(slide);
        ProjectManager.saveSlideOrder(slidesList);
        Update();
    }

    public void loadSlides() {
        slidesList = FXCollections.observableArrayList(ProjectManager.getSlides());
    }

    public void Update() {
        showSlides();
    }
}