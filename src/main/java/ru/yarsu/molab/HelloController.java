package ru.yarsu.molab;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private Spinner<Integer> varNSpinner;
    @FXML
    private Spinner<Integer> constraintsNSpinner;
    @FXML
    private GridPane table;
    @FXML
    private GridPane objectiveFunction;

    private final Fraction[] objF = new Fraction[17];
    private final Fraction[][] constraints = new Fraction[17][17];

    private Solver solver;

    private TextField generateCell(String text, boolean editable) {
        TextField tf = new TextField();
        tf.setPrefHeight(15);
        tf.setPrefWidth(50);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(editable);
        tf.setText(text);
        return tf;
    }

    private void createObjFTable(int varN) {
        objectiveFunction.getChildren().clear();
        TextField tf = generateCell("", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        objectiveFunction.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell("c" + i, false);
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, i);
            objectiveFunction.getChildren().add(tf);
        }
        tf = generateCell("c", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, varN + 1);
        objectiveFunction.getChildren().add(tf);

        tf = generateCell("f(x)", false);
        GridPane.setRowIndex(tf, 1);
        GridPane.setColumnIndex(tf, 0);
        objectiveFunction.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell(objF[i - 1].toString(), true);
            GridPane.setRowIndex(tf, 1);
            GridPane.setColumnIndex(tf, i);
            objectiveFunction.getChildren().add(tf);
        }
        tf = generateCell(objF[16].toString(), true);
        GridPane.setRowIndex(tf, 1);
        GridPane.setColumnIndex(tf, varN + 1);
        objectiveFunction.getChildren().add(tf);
    }

    private void saveObjFTable(int cols) {
        for (int i = cols + 3; i < cols * 2 + 3; i++) {
            String value = ((TextField) (objectiveFunction.getChildren().get(i))).getText();
            try {
                objF[i - cols - 3] = new Fraction(value);

            } catch (Exception e) {
                System.out.println("bad format");
            }
        }
        try {
            objF[objF.length - 1] = new Fraction(((TextField) (objectiveFunction.getChildren().get(cols * 2 + 3))).getText());
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    private void createConstraintsTable(int constraintsN, int varN) {
        table.getChildren().clear();
        TextField tf = generateCell("", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        table.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell("a" + i, false);
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, i);
            table.getChildren().add(tf);
        }
        tf = generateCell("b", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, varN + 1);
        table.getChildren().add(tf);

        for (int i = 1; i < constraintsN + 1; i++) {
            tf = generateCell("f" + Integer.toString(i) + "(x)", false);
            GridPane.setRowIndex(tf, i);
            GridPane.setColumnIndex(tf, 0);
            table.getChildren().add(tf);
            for (int j = 1; j < varN + 1; j++) {
                tf = generateCell(constraints[i - 1][j - 1].toString(), true);
                GridPane.setRowIndex(tf, i);
                GridPane.setColumnIndex(tf, j);
                table.getChildren().add(tf);
            }
            tf = generateCell(constraints[i - 1][16].toString(), true);
            GridPane.setRowIndex(tf, i);
            GridPane.setColumnIndex(tf, varN + 1);
            table.getChildren().add(tf);
        }

    }

    private void saveConstraintsTable(int rows, int cols) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String value = ((TextField) (table.getChildren().get((i + 1) * (cols + 2) + j + 1))).getText();
                try {
                    constraints[i][j] = new Fraction(value);

                } catch (Exception e) {
                    System.out.println("bad format");
                }
            }
            try {
                constraints[i][16] = new Fraction(((TextField) (table.getChildren().get((i + 1) * (cols + 2) + cols + 1))).getText());
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

    }

    @FXML
    private void handleKeyInput() {

    }
    @FXML
    private void handleAboutAction() {

    }

    @FXML
    private void initialize() {
        //init tables
        for (int i = 0; i < 17; i++) {
            objF[i] = new Fraction(0, 1);
            for (int j = 0; j < 17; j++) {
                constraints[i][j] = new Fraction(0, 1);
            }
        }

        createObjFTable(varNSpinner.getValue());
        createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());

        //resize tables when values change
        varNSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    saveObjFTable(oldValue);
                    createObjFTable(varNSpinner.getValue());
                    saveConstraintsTable(constraintsNSpinner.getValue(), oldValue);
                    createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());
                }
        );

        constraintsNSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    saveConstraintsTable(oldValue, varNSpinner.getValue());
                    createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());
                }
        );
    }


}