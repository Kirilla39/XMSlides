package com.kirilla.xmslides.controllers.main_modules;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementInspector {
    private static final String NO_ELEMENT_SELECTED = "No element selected";
    private static final Pattern TAG_PATTERN = Pattern.compile("<([^\\s>/]+)(\\s+[^>]*?)?(/?>)|</([^>]+)>");
    private static final Pattern ATTR_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*\"([^\"]*)\"");
    private static final Pattern CONTENT_PATTERN = Pattern.compile(">(.*?)<");

    @FXML private VBox propertyContainer;
    @FXML private Label elementTypeLabel;

    private CodeEditor codeEditor;
    private String currentElementTag;
    private int elementStartPosition;
    private int elementEndPosition;

    private enum PropertyType {
        TEXT_FIELD,
        COMBO_BOX,
        COLOR_PICKER
    }

    private static class PropertyDefinition {
        String name;
        String attribute;
        PropertyType type;
        String[] options;
        String defaultValue;

        PropertyDefinition(String name, String attribute, PropertyType type,
                           String defaultValue, String... options) {
            this.name = name;
            this.attribute = attribute;
            this.type = type;
            this.options = options;
            this.defaultValue = defaultValue;
        }
    }

    private static final Map<String, List<PropertyDefinition>> ELEMENT_PROPERTIES = Map.of(
            "text", List.of(
                    new PropertyDefinition("Font", "font", PropertyType.COMBO_BOX,
                            "Arial", "Arial", "Calibri", "Cambria", "Courier New", "Georgia"),
                    new PropertyDefinition("Size", "font-size", PropertyType.TEXT_FIELD, "12"),
                    new PropertyDefinition("Color", "color", PropertyType.COLOR_PICKER, "#000000"),
                    new PropertyDefinition("V Align", "vertical_align", PropertyType.COMBO_BOX,
                            "top", "top", "center", "bottom"),
                    new PropertyDefinition("H Align", "horizontal_align", PropertyType.COMBO_BOX,
                            "left", "left", "center", "right")
            ),
            "image", List.of(
                    new PropertyDefinition("Source", "src", PropertyType.TEXT_FIELD, ""),
                    new PropertyDefinition("Size", "size", PropertyType.TEXT_FIELD, ""),
                    new PropertyDefinition("V Align", "vertical_align", PropertyType.COMBO_BOX,
                            "top", "top", "center", "bottom"),
                    new PropertyDefinition("H Align", "horizontal_align", PropertyType.COMBO_BOX,
                            "left", "left", "center", "right")
            )
    );

    public void setCodeEditor(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
        setupCodeEditorListener();
    }

    private void setupCodeEditorListener() {
        codeEditor.getCodeArea().caretPositionProperty()
                .addListener((obs, oldPos, newPos) ->
                        updateInspectorForCurrentPosition(newPos));
    }

    private void updateInspectorForCurrentPosition(int caretPosition) {
        String xmlText = codeEditor.getCodeArea().getText();
        ElementPosition elementPos = findElementAtPosition(xmlText, caretPosition);

        if (elementPos != null) {
            currentElementTag = elementPos.tagName;
            elementStartPosition = elementPos.start;
            elementEndPosition = elementPos.end;
            updateInspectorUI(xmlText);
        } else {
            clearInspector();
        }
    }

    private record ElementPosition(String tagName, int start, int end) { }

    private ElementPosition findElementAtPosition(String xmlText, int position) {
        Deque<ElementPosition> tagStack = new ArrayDeque<>();
        Matcher tagMatcher = TAG_PATTERN.matcher(xmlText);

        while (tagMatcher.find()) {
            if (tagMatcher.group(3) != null && tagMatcher.group(3).equals("/>")) {
                if (position >= tagMatcher.start() && position <= tagMatcher.end()) {
                    return new ElementPosition(tagMatcher.group(1),
                            tagMatcher.start(), tagMatcher.end());
                }
            } else if (tagMatcher.group(0).startsWith("</")) {
                String closingTagName = tagMatcher.group(4);
                if (!tagStack.isEmpty() && tagStack.peek().tagName.equals(closingTagName)) {
                    ElementPosition openingTag = tagStack.pop();
                    if (position >= openingTag.start && position <= tagMatcher.end()) {
                        return new ElementPosition(openingTag.tagName,
                                openingTag.start, tagMatcher.end());
                    }
                }
            } else {
                tagStack.push(new ElementPosition(tagMatcher.group(1),
                        tagMatcher.start(), -1));
            }
        }
        return null;
    }

    private void updateInspectorUI(String xmlText) {
        propertyContainer.getChildren().clear();

        if (currentElementTag == null) {
            elementTypeLabel.setText(NO_ELEMENT_SELECTED);
            return;
        }

        elementTypeLabel.setText(currentElementTag + " Properties");
        String elementXml = xmlText.substring(elementStartPosition, elementEndPosition);
        Map<String, String> attributes = extractAttributes(elementXml);

        addPropertiesBasedOnTag(attributes, xmlText);
    }

    private void addPropertiesBasedOnTag(Map<String, String> attributes, String xmlText) {
        String tagLower = currentElementTag.toLowerCase();
        List<PropertyDefinition> properties = ELEMENT_PROPERTIES.get(tagLower);
        if (properties == null) return;

        for (int i = 0; i < properties.size(); i += 2) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(5, 0, 5, 0));

            int propertiesInRow = Math.min(2, properties.size() - i);
            for (int j = 0; j < propertiesInRow; j++) {
                PropertyDefinition prop = properties.get(i + j);
                String currentValue = attributes.getOrDefault(prop.attribute, prop.defaultValue);
                row.getChildren().add(createPropertyControl(prop, currentValue));
            }

            propertyContainer.getChildren().add(row);
        }

        if (tagLower.equals("text")) {
            String content = getTextContent(xmlText, elementStartPosition, elementEndPosition);
            addContentControl(content);
        }
    }

    private VBox createPropertyControl(PropertyDefinition prop, String currentValue) {
        VBox container = new VBox(2);
        Label label = new Label(prop.name);
        label.setStyle("-fx-font-size: 11;");

        Control control = switch (prop.type) {
            case COMBO_BOX -> createComboBox(prop.attribute, currentValue, prop.options);
            case COLOR_PICKER -> createColorPicker(prop.attribute, currentValue);
            default -> createTextField(prop.attribute, currentValue);
        };

        container.getChildren().addAll(label, control);
        container.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(container, Priority.ALWAYS);
        return container;
    }

    private ComboBox<String> createComboBox(String attribute, String currentValue, String[] options) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(options);
        comboBox.setValue(currentValue);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setStyle("-fx-font-size: 12;");

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (updateAttribute(attribute, newVal)) {
                codeEditor.saveXmlToFile();
            }
        });

        return comboBox;
    }

    private ColorPicker createColorPicker(String attribute, String currentValue) {
        ColorPicker colorPicker = new ColorPicker();
        try {
            colorPicker.setValue(Color.web(currentValue));
        } catch (Exception e) {
            colorPicker.setValue(Color.BLACK);
        }
        colorPicker.setMaxWidth(Double.MAX_VALUE);

        colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            String hexColor = String.format("#%02X%02X%02X",
                    (int)(newVal.getRed() * 255),
                    (int)(newVal.getGreen() * 255),
                    (int)(newVal.getBlue() * 255));

            if (updateAttribute(attribute, hexColor)) {
                codeEditor.saveXmlToFile();
            }
        });

        return colorPicker;
    }

    private TextField createTextField(String attribute, String currentValue) {
        TextField textField = new TextField(currentValue);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setStyle("-fx-font-size: 12;");

        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (updateAttribute(attribute, newVal)) {
                codeEditor.saveXmlToFile();
            }
        });

        return textField;
    }

    private void addContentControl(String content) {
        HBox row = new HBox();
        row.setPadding(new Insets(5, 0, 5, 0));

        VBox container = new VBox(2);
        Label label = new Label("Content");
        label.setStyle("-fx-font-size: 11;");

        TextArea textArea = new TextArea(content);
        textArea.setPrefRowCount(3);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setStyle("-fx-font-size: 12;");

        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (updateTextContent(newVal)) {
                codeEditor.saveXmlToFile();
            }
        });

        container.getChildren().addAll(label, textArea);
        container.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().add(container);
        propertyContainer.getChildren().add(row);
    }

    private String getTextContent(String xmlText, int start, int end) {
        String elementXml = xmlText.substring(start, end);
        if (elementXml.contains("/>")) {
            return extractAttributes(elementXml).getOrDefault("text", "");
        }
        Matcher contentMatcher = CONTENT_PATTERN.matcher(elementXml);
        return contentMatcher.find() ? contentMatcher.group(1) : "";
    }

    private Map<String, String> extractAttributes(String elementXml) {
        Map<String, String> attributes = new HashMap<>();
        Matcher attrMatcher = ATTR_PATTERN.matcher(elementXml);
        while (attrMatcher.find()) {
            attributes.put(attrMatcher.group(1), attrMatcher.group(2));
        }
        return attributes;
    }

    private boolean updateAttribute(String attrName, String newValue) {
        String elementXml = codeEditor.getCodeArea().getText()
                .substring(elementStartPosition, elementEndPosition);

        Matcher attrMatcher = Pattern.compile(
                "(\\s+" + Pattern.quote(attrName) + "=\")([^\"]*)(\")").matcher(elementXml);

        String newElementXml = attrMatcher.find() ?
                attrMatcher.replaceFirst("$1" + newValue + "$3") :
                insertAttribute(elementXml, attrName, newValue);

        if (newElementXml != null) {
            updateCodeArea(newElementXml);
            return true;
        }
        return false;
    }

    private String insertAttribute(String elementXml, String attrName, String newValue) {
        int insertPos = elementXml.indexOf('>');
        if (insertPos == -1) return null;
        return elementXml.substring(0, insertPos) + " " + attrName + "=\"" +
                newValue + "\"" + elementXml.substring(insertPos);
    }

    private boolean updateTextContent(String newContent) {
        String elementXml = codeEditor.getCodeArea().getText()
                .substring(elementStartPosition, elementEndPosition);
        Matcher contentMatcher = CONTENT_PATTERN.matcher(elementXml);

        if (contentMatcher.find()) {
            updateCodeArea(contentMatcher.replaceFirst(">" + newContent + "<"));
            return true;
        }
        return false;
    }

    private void updateCodeArea(String newElementXml) {
        codeEditor.getCodeArea().replaceText(elementStartPosition,
                elementEndPosition, newElementXml);
        elementEndPosition = elementStartPosition + newElementXml.length();
    }

    private void clearInspector() {
        propertyContainer.getChildren().clear();
        currentElementTag = null;
        elementTypeLabel.setText(NO_ELEMENT_SELECTED);
    }
}