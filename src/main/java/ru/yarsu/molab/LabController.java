package ru.yarsu.molab;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    @FXML
    private Label fileNameLabel;
    @FXML
    private Button solve;
    @FXML
    GridPane diagMatrixPane;

    private Fraction[] objF;
    private Fraction[][] constraints;
    private ArrayList<Integer> basis = new ArrayList<Integer>();
    private File curFile = null;
    private Solver solver;
    private StepMatrix diagMatrix;
    private ArrayList<StepMatrix> steps = new ArrayList<StepMatrix>();

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
        basis.clear();
        TextField tf = generateCell("", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        table.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell("a" + i, false);

            //eventhandler to get chosen basis vars
            int finalI = i;
            tf.setOnMouseClicked(e -> {
                TextField textField = (TextField) e.getSource();
                if (!basis.contains(finalI-1)) {
                    basis.add(finalI-1);
                    textField.setStyle("-fx-border-color:red;");
                }
                else {
                    basis.remove(Integer.valueOf(finalI-1));
                    textField.setStyle("-fx-border-color:lightgrey;");
                }
            });
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

    void createDiagMatrixPane() {
        diagMatrixPane.getChildren().clear();
        TextField tf;

        //header
        for (int i = 0; i < diagMatrix.getCols(); i++) {
            tf = generateCell("x" + (diagMatrix.getoX()[i]+1), false);
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, i);
            diagMatrixPane.getChildren().add(tf);
        }
        tf = generateCell("b", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, diagMatrix.getCols());
        diagMatrixPane.getChildren().add(tf);

        //data
        for (int i = 0; i < diagMatrix.getRows(); i++) {
            for (int j=0; j < diagMatrix.getCols()+1; j++) {
                tf = generateCell(((diagMatrix.getMatrix()).getElement(i,j)).toString(), false);
                GridPane.setRowIndex(tf, i+1);
                GridPane.setColumnIndex(tf, j);
                diagMatrixPane.getChildren().add(tf);
            }
        }
    }
    @FXML
    private void handleNewFile() {
        solver.init();
        solver.setVarN(varNSpinner.getValue());
        solver.setConstraintsN(constraintsNSpinner.getValue());
        createObjFTable(solver.getVarN());
        createConstraintsTable(solver.getConstraintsN(), solver.getVarN());
        curFile = null;
        fileNameLabel.setText("Файл не используется");
    }

    @FXML
    private void handleFileOpen() {
        //todo не обновляются значения
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (file == null) return;
        curFile = file;
        fileNameLabel.setText("Текущий файл: " + file.getAbsolutePath());
        solver.readFromFile(file);
        constraintsNSpinner.getValueFactory().setValue(solver.getConstraintsN());
        varNSpinner.getValueFactory().setValue(solver.getVarN());
    }

    @FXML
    private void handleFileSave() {
        apply();
        if (curFile == null) {
            System.out.println("no active file");
            return;
        }
        solver.saveToFile(curFile);
    }

    @FXML
    private void handleFileSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
        curFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        try {
            curFile.createNewFile();
         } catch (Exception er) {

        }
        handleFileSave();
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
        fileNameLabel.setText("Файл не используется");
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

    @FXML
    private void makeDiag() {
        if (basis.size() != solver.getConstraintsN()) {
            System.out.println("n of constraint should be equal to basis length");
            return;
        }
        int[] arr = new int[solver.getVarN()];
        for (int i = 0; i < solver.getVarN(); i++){
            arr[i] = i;
        }
        int[] arr1 = new int[solver.getConstraintsN()];
        for (int i = 0; i < solver.getConstraintsN(); i++){
            arr1[i] = i;
        }
        diagMatrix = new StepMatrix(solver.toMatrix(solver.getConstraintsN(), solver.getVarN()+1), arr, arr1);
        //move basis to the left of the matrix
        Collections.sort(basis);
        for (int i = 0; i < basis.size(); i ++) {
            diagMatrix.swapColumns(i, basis.get(i));
        }


        //convert to diagonal view
        diagMatrix.getMatrix().makeDiagonal();
        System.out.println("diag matrix:");
        diagMatrix.print();
        createDiagMatrixPane();


    }

    @FXML
    public void startIterations() {
        //fill simplex matrix from diagonal for the first time
        //free and basis vars
        int [] oY = Arrays.copyOf(diagMatrix.getoX(), diagMatrix.getRows());
        int [] oX = Arrays.copyOfRange(diagMatrix.getoX(), diagMatrix.getRows(), diagMatrix.getoX().length);

        Fraction[][] simplexMatrix = new Fraction[oY.length+1][oX.length+1];
        //fill basis rows
        for (int i = 0; i<oY.length; i++) {
            for (int j = 0; j < oX.length; j++) {
                simplexMatrix[i][j] = new Fraction(diagMatrix.getMatrix().getElement(i,j+oX.length));
            }
            simplexMatrix[i][oX.length] =diagMatrix.getMatrix().getElement(0, oX.length+oY.length);
        }
        //fill p
        //calc objective function coef
        Fraction coef;
        for (int j = 0; j < oX.length; j++) {
            //coef at free var
            coef = new Fraction(solver.getObjF()[oX[j]]);
            for (int i = 0; i < oY.length; i++) {
                coef = coef.add(simplexMatrix[i][j].multiply(new Fraction(-1,1)).multiply(solver.getObjF()[oY[i]]));
            }
            simplexMatrix[oY.length][j] = coef;
        }
        //calc beta
        //do the same but without multiplying by -1
        coef = new Fraction(solver.getObjF()[solver.getObjF().length-1]);
        for (int i = 0; i < oY.length; i++) {
            //todo beta at objective function??
            coef = coef.add(simplexMatrix[i][oX.length].multiply(solver.getObjF()[oY[i]]));
        }
        simplexMatrix[oY.length][oX.length] = coef;

        Matrix matrix = new Matrix(simplexMatrix, oY.length+1,oX.length+1 );


        steps.add(new StepMatrix(matrix, oX, oY));
        steps.get(steps.size()-1).print();
    }


}