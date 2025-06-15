package com.kirilla.xmslides.controllers.main_modules;

import com.kirilla.xmslides.controllers.MainController;
import com.kirilla.xmslides.util.SyntaxHighlighter;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeEditor {
    @FXML
    private CodeArea codeEditor;

    private SlideWorkspace workspace;
    private File slideFile;

    public MainController main;

    public void setXmlFile(File xmlFile) {
        this.slideFile = xmlFile;
        loadXmlFromFile();
    }

    public void setWorkspace(SlideWorkspace workspace) {
        this.workspace = workspace;
    }

    public void setMain(MainController main){
        this.main = main;
    }

    public CodeArea getCodeArea() {
        return codeEditor;
    }

    @FXML
    public void initialize() {
        codeEditor.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.isControlDown()) {
                switch (event.getCode()){
                    case KeyCode.S:
                        saveXmlToFile();
                        main.Update();
                        break;
                    case KeyCode.D:
                        duplicateLine();
                        break;
                    case KeyCode.TAB:
                        tabulateLine();
                        break;
                }
            }
        });
        codeEditor.replaceText(0,0,"");
        codeEditor.setParagraphGraphicFactory(LineNumberFactory.get(codeEditor));
        codeEditor.textProperty().addListener((obs, oldText, newText) -> {
            codeEditor.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(newText));
        });
    }

    private void duplicateLine(){
        codeEditor.getUndoManager().mark();
        codeEditor.insertText(codeEditor.getCurrentParagraph(),codeEditor.getCurrentLineEndInParargraph(),"\n"+codeEditor.getParagraph(codeEditor.getCurrentParagraph()).getText());
    }

    private void tabulateLine(){
        codeEditor.getUndoManager().mark();
        int pos = codeEditor.getCaretPosition()+4;
        codeEditor.insertText(codeEditor.getCurrentParagraph(),codeEditor.getCurrentLineStartInParargraph(),"    ");
        codeEditor.displaceCaret(pos);
    }

    private void loadXmlFromFile() {
        try {
            String xmlContent = new String(Files.readAllBytes(Paths.get(slideFile.toURI())));
            codeEditor.replaceText(0,codeEditor.getLength(),xmlContent);
            codeEditor.getUndoManager().forgetHistory();
            workspace.loadSlide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void saveXmlToFile() {
        try (FileWriter writer = new FileWriter(slideFile)) {
            writer.write(codeEditor.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
