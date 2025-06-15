package com.kirilla.xmslides.dialogs;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class ScreenSelector {
    public interface ScreenSelectionCallback {
        void onScreenSelected(Screen screen);
    }

    public static void show(Window owner, ScreenSelectionCallback callback) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Select Presentation Screen");

        ComboBox<Screen> screenCombo = new ComboBox<>(
                FXCollections.observableArrayList(Screen.getScreens())
        );
        screenCombo.setCellFactory(lv -> new ScreenListCell());
        screenCombo.setButtonCell(new ScreenListCell());

        Button showButton = new Button("Show");
        showButton.setDefaultButton(true);
        showButton.setOnAction(e -> {
            Screen selected = screenCombo.getSelectionModel().getSelectedItem();
            if (selected != null) {
                callback.onScreenSelected(selected);
                dialog.close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, showButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, new Label("Select screen for presentation:"), screenCombo, buttons);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: -fx-background;");

        dialog.setScene(new Scene(layout));
        dialog.showAndWait();
    }

    private static class ScreenListCell extends ListCell<Screen> {
        @Override
        protected void updateItem(Screen screen, boolean empty) {
            super.updateItem(screen, empty);
            if (empty || screen == null) { setText(null); }
            else {setText(String.format("Screen %d (%dx%d)", Screen.getScreens().indexOf(screen) + 1, (int)screen.getBounds().getWidth(), (int)screen.getBounds().getHeight()));}
        }
    }
}