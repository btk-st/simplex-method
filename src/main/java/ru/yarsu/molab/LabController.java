package ru.yarsu.molab;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class LabController {

    @FXML
    public MenuBar menuBar;
    @FXML
    private Spinner<Integer> varNSpinner;
    @FXML
    private Spinner<Integer> constraintsNSpinner;
    @FXML
    private GridPane table;
    @FXML
    private GridPane objectiveFunction;

    private Fraction[] objF;
    private Fraction[][] constraints;

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
                System.out.println("bad format" + e);
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
            tf = generateCell("f" + i + "(x)", false);
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
    private void handleFileOpen() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        solver.readFromFile(file);
        constraintsNSpinner.getValueFactory().setValue(solver.getConstraintsN());
        varNSpinner.getValueFactory().setValue(solver.getVarN());
    }

    @FXML
    private void handleFileSave() {

    }

    @FXML
    private void handleFileSaveAs() {

    }

    @FXML
    private void handleAuthor() {
    }

    @FXML
    private void handleHelp() {

    }
    @FXML
    private void apply() {
        saveObjFTable(varNSpinner.getValue());
        saveConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());
        System.out.println("saved:");
        solver.print();
    }
    @FXML
    private void initialize() {
        //init tables
        solver = new Solver();
        solver.init();
        solver.setVarN(varNSpinner.getValue());
        solver.setConstraintsN(constraintsNSpinner.getValue());
        objF = solver.getObjF();
        constraints = solver.getConstraints();

        createObjFTable(varNSpinner.getValue());
        createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());

        //resize tables when values change
        varNSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    solver.setVarN(newValue);
                    createObjFTable(varNSpinner.getValue());
                    createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());
                }
        );
        constraintsNSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    solver.setConstraintsN(newValue);
                    createConstraintsTable(constraintsNSpinner.getValue(), varNSpinner.getValue());
                }
        );
    }


}