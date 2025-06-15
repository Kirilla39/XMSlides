package com.kirilla.xmslides.controllers.main_modules;

import com.kirilla.xmslides.controllers.show_mode.PresentationView;
import com.kirilla.xmslides.dialogs.ScreenSelector;
import com.kirilla.xmslides.model.SlideModel;
import com.kirilla.xmslides.util.ProjectManager;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SlideListView implements Initializable {
    public ListView slideListView;
    public Button addSlideButton;
    private CodeEditor codeEditor;
    private SlideWorkspace workspace;
    private int currentSlide;
    public List<SlideModel> slidesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LoadSlide();
        addSlideButton.setOnMouseClicked(event -> CreateSlide());
        showSlides();
        slideListView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.isControlDown()) {
                switch (event.getCode()){
                    case KeyCode.R: Update(); break;
                    case KeyCode.F5: showSlideAsPresentation(); break;
                }
            }
        });
    }

    public void showSlides(){
        slidesList.forEach(slide -> {
            Pane slideNode = new Pane();
            slideNode.getChildren().add(new ImageView(slide.getPreview()));
            slideNode.addEventHandler(MouseEvent.MOUSE_CLICKED,mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) selectSlide(slidesList.indexOf(slide));
                else if (mouseEvent.getButton() == MouseButton.SECONDARY) DeleteSlide(slidesList.indexOf(slide));
                });
            slideListView.getItems().add(slideNode);
        });
    }

    private void showSlideAsPresentation(){
        ScreenSelector.show(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null), selectedScreen -> {
            new PresentationView(this).showPresentation(slidesList.get(currentSlide).updateSlide(), selectedScreen);
        });
    }

    public void setCodeEditor(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void setWorkspace(SlideWorkspace workspace) {
        this.workspace = workspace;
    }

    public int getCurrentSlide(){
        return currentSlide;
    }

    public void selectSlide(int index){
        currentSlide = index;
        workspace.setSlide(slidesList.get(index));
        codeEditor.setXmlFile(new File(slidesList.get(index).getPath()));
    }

    public void CreateSlide(){
        String basepath = ProjectManager.getCurrentProject();
        File[] slides = new File(basepath).listFiles((dir, name) -> name.toLowerCase().endsWith("-slide.xml"));
        try {
            FileWriter fw = new FileWriter(basepath + (slides.length + 1) + "-slide.xml");
            fw.write("<Slide ratio=\"16:9\">\n</Slide>");
            fw.close();
        }
        catch (Exception e) {}
        LoadSlide();
        Update();
    }

    public void DeleteSlide(int slide){
        new File(slidesList.get(slide).getPath()); slidesList.remove(slide); Update();
    }


    public void LoadSlide(){
        slidesList = ProjectManager.getSlides();
    }

    public void Update(){
        slideListView.getItems().clear(); showSlides();
    }


}
