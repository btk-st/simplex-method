package ru.yarsu.molab;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class LabController {

    @FXML
    public MenuBar menuBar;
    @FXML
    public VBox simplexSteps;
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
    @FXML
    Button startIterationButton;
    @FXML
    Button stepBack;
    @FXML
    Label answer;
    @FXML
    CheckBox autoSolve;

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
        tf.setFocusTraversable(false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        objectiveFunction.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell("c" + i, false);
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, i);
            tf.setFocusTraversable(false);
            objectiveFunction.getChildren().add(tf);
        }
        tf = generateCell("c", false);
        tf.setFocusTraversable(false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, varN + 1);
        objectiveFunction.getChildren().add(tf);

        tf = generateCell("f(x)", false);
        tf.setFocusTraversable(false);
        GridPane.setRowIndex(tf, 1);
        GridPane.setColumnIndex(tf, 0);
        objectiveFunction.getChildren().add(tf);
        //data
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

    private void saveObjFTable(int cols) throws IllegalArgumentException {
        for (int i = cols + 3; i < cols * 2 + 3; i++) {
            String value = ((TextField) (objectiveFunction.getChildren().get(i))).getText();
            objF[i - cols - 3] = new Fraction(value);
        }
        objF[objF.length - 1] = new Fraction(((TextField) (objectiveFunction.getChildren().get(cols * 2 + 3))).getText());

    }

    private void createConstraintsTable(int constraintsN, int varN) {
        table.getChildren().clear();
        basis.clear();
        TextField tf = generateCell("", false);
        tf.setFocusTraversable(false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        table.getChildren().add(tf);
        for (int i = 1; i < varN + 1; i++) {
            tf = generateCell("a" + i, false);
            tf.setFocusTraversable(false);
            //eventhandler to get chosen basis vars
            int finalI = i;
            tf.setOnMouseClicked(e -> {
                TextField textField = (TextField) e.getSource();
                if (!basis.contains(finalI - 1) && basis.size() < solver.getConstraintsN()) {
                    basis.add(finalI - 1);
                    textField.setStyle("-fx-border-color:red;");
                } else {
                    basis.remove(Integer.valueOf(finalI - 1));
                    textField.setStyle("-fx-border-color:lightgrey;");
                }
            });
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, i);
            table.getChildren().add(tf);
        }
        tf = generateCell("b", false);
        tf.setFocusTraversable(false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, varN + 1);
        table.getChildren().add(tf);

        for (int i = 1; i < constraintsN + 1; i++) {
            tf = generateCell("f" + i + "(x)", false);
            GridPane.setRowIndex(tf, i);
            GridPane.setColumnIndex(tf, 0);
            tf.setFocusTraversable(false);
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

    private void saveConstraintsTable(int rows, int cols) throws IllegalArgumentException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String value = ((TextField) (table.getChildren().get((i + 1) * (cols + 2) + j + 1))).getText();
                constraints[i][j] = new Fraction(value);
            }
            constraints[i][16] = new Fraction(((TextField) (table.getChildren().get((i + 1) * (cols + 2) + cols + 1))).getText());
        }
    }

    private void createDiagMatrixPane() {
        diagMatrixPane.getChildren().clear();
        TextField tf;

        //header
        for (int i = 0; i < diagMatrix.getCols(); i++) {
            tf = generateCell("x" + (diagMatrix.getoX()[i] + 1), false);
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
            for (int j = 0; j < diagMatrix.getCols() + 1; j++) {
                tf = generateCell(((diagMatrix.getMatrix()).getElement(i, j)).toString(), false);
                GridPane.setRowIndex(tf, i + 1);
                GridPane.setColumnIndex(tf, j);
                diagMatrixPane.getChildren().add(tf);
            }
        }
    }

    @FXML
    private void handleNewFile() {
        answer.setText("");
        startIterationButton.setDisable(true);
        stepBack.setDisable(true);
        diagMatrixPane.getChildren().clear();
        simplexSteps.getChildren().clear();
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

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (file == null) return;
        stepBack.setDisable(true);
        startIterationButton.setDisable(true);
        answer.setText("");
        diagMatrixPane.getChildren().clear();
        simplexSteps.getChildren().clear();
        curFile = file;
        fileNameLabel.setText("Текущий файл: " + file.getAbsolutePath());
        solver.readFromFile(file);
        //перерисовать вручную если размеры совпадают
        if (varNSpinner.getValue() == solver.getVarN()) {
            createObjFTable(solver.getVarN());
        }
        if (constraintsNSpinner.getValue() == solver.getConstraintsN()) {
            createConstraintsTable(solver.getConstraintsN(), solver.getVarN());
        }
        //обновляем значения счетчиков
        constraintsNSpinner.getValueFactory().setValue(solver.getConstraintsN());
        varNSpinner.getValueFactory().setValue(solver.getVarN());
    }

    @FXML
    private void handleFileSave() {
        try {
            apply();
        } catch (IllegalArgumentException e) {
            alert("Проверьте правильность введенных данных. Файл не был сохранен");
        }
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
    private void apply() throws IllegalArgumentException{
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

    public void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(message);
        alert.setContentText("");

        alert.showAndWait();
    }

    @FXML
    private void startCalc() {
        //save data
        try {
            apply();
        } catch (IllegalArgumentException e) {
            alert("Праверьте правильность введенных данных");
            return;
        }
        answer.setText("");
        //delete simplex steps
        simplexSteps.getChildren().clear();
        steps.clear();
        diagMatrix = null;
        diagMatrixPane.getChildren().clear();
        if (basis.size() != solver.getConstraintsN()) {
            startIterationButton.setDisable(true);
            alert("Выбрано недостаточно базисных переменных");
            return;
        }
        //enable step button
        startIterationButton.setDisable(false);
        int[] arr = new int[solver.getVarN()];
        for (int i = 0; i < solver.getVarN(); i++) {
            arr[i] = i;
        }
        int[] arr1 = new int[solver.getConstraintsN()];
        for (int i = 0; i < solver.getConstraintsN(); i++) {
            arr1[i] = i;
        }
        diagMatrix = new StepMatrix(solver.toMatrix(solver.getConstraintsN(), solver.getVarN() + 1), arr, arr1);
        //move basis to the left of the matrix
        Collections.sort(basis);
        for (int i = 0; i < basis.size(); i++) {
            diagMatrix.swapColumns(i, basis.get(i));
        }


        //convert to diagonal view
        diagMatrix.getMatrix().makeDiagonal();
        System.out.println("diag matrix:");
        diagMatrix.print();
        createDiagMatrixPane();

        //auto solve
        if (autoSolve.isSelected()) {
            while (steps.size() == 0 || steps.get(steps.size() - 1).getPivotElements().size() != 0) {
                startIterations();
            }
        } else {
            startIterations();
        }

    }

    public void startIterations() {
        StepMatrix stepMatrix;
        //if first step
        if (steps.size() == 0) {
            //fill simplex matrix from diagonal for the first time
            //free and basis vars
            int[] oY = Arrays.copyOf(diagMatrix.getoX(), diagMatrix.getRows());
            int[] oX = Arrays.copyOfRange(diagMatrix.getoX(), diagMatrix.getRows(), diagMatrix.getoX().length);

            Fraction[][] simplexMatrix = new Fraction[oY.length + 1][oX.length + 1];
            //fill basis rows
            for (int i = 0; i < oY.length; i++) {
                for (int j = 0; j < oX.length; j++) {
                    simplexMatrix[i][j] = new Fraction(diagMatrix.getMatrix().getElement(i, j + oY.length));
                }
                simplexMatrix[i][oX.length] = diagMatrix.getMatrix().getElement(i, oX.length + oY.length);
            }
            //fill p
            //calc objective function coef
            Fraction coef;
            for (int j = 0; j < oX.length; j++) {
                //coef at free var
                coef = new Fraction(solver.getObjF()[oX[j]]);
                for (int i = 0; i < oY.length; i++) {
                    coef = coef.add(simplexMatrix[i][j].multiply(new Fraction(-1, 1)).multiply(solver.getObjF()[oY[i]]));
                }
                simplexMatrix[oY.length][j] = coef;
            }
            //calc beta
            //do the same but without multiplying by -1
            coef = new Fraction(solver.getObjF()[solver.getObjF().length - 1]);
            for (int i = 0; i < oY.length; i++) {
                //todo beta at objective function??
                coef = coef.add(simplexMatrix[i][oX.length].multiply(solver.getObjF()[oY[i]]));
            }
            simplexMatrix[oY.length][oX.length] = coef.multiply(new Fraction(-1, 1));

            Matrix matrix = new Matrix(simplexMatrix, oY.length + 1, oX.length + 1);
            stepMatrix = new StepMatrix(matrix, oX, oY);
        } else {
            stepMatrix = steps.get(steps.size() - 1).nextStepMatrix();
        }
        steps.add(stepMatrix);
        stepMatrix.findPivotElements();
        if (stepMatrix.getPivotElements().size() == 0) {
            startIterationButton.setDisable(true);
            answer.setText(stepMatrix.getAnswer());
            // end or -inf ????
        }
        stepBack.setDisable(steps.size() == 1);
        createSimplexStepTable(stepMatrix);

    }

    @FXML
    private void stepBack() {
        answer.setText("");
        int stepToRemove = steps.size() - 1;
        steps.remove(stepToRemove);
        simplexSteps.getChildren().remove(stepToRemove);
        startIterationButton.setDisable(false);
        //if first step
        stepBack.setDisable(steps.size() == 1);
    }

    private void createSimplexStepTable(StepMatrix stepMatrix) {
        GridPane stepPane = new GridPane();

        TextField tf;

        //header
        tf = generateCell("x(" + (steps.size() - 1) + ")", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, 0);
        stepPane.getChildren().add(tf);
        for (int j = 0; j < stepMatrix.getoX().length; j++) {
            tf = generateCell("x" + (stepMatrix.getoX()[j] + 1), false);
            GridPane.setRowIndex(tf, 0);
            GridPane.setColumnIndex(tf, j + 1);
            stepPane.getChildren().add(tf);
        }
        tf = generateCell("b", false);
        GridPane.setRowIndex(tf, 0);
        GridPane.setColumnIndex(tf, stepMatrix.getoX().length + 1);
        stepPane.getChildren().add(tf);

        for (int i = 0; i < stepMatrix.getoY().length; i++) {
            tf = generateCell("x" + (stepMatrix.getoY()[i] + 1), false);
            GridPane.setRowIndex(tf, i + 1);
            GridPane.setColumnIndex(tf, 0);
            stepPane.getChildren().add(tf);
        }
        tf = generateCell("p", false);
        GridPane.setRowIndex(tf, stepMatrix.getoY().length + 1);
        GridPane.setColumnIndex(tf, 0);
        stepPane.getChildren().add(tf);

        //data
        for (int i = 0; i < stepMatrix.getRows() + 1; i++) {
            for (int j = 0; j < stepMatrix.getCols() + 1; j++) {
                tf = generateCell(((stepMatrix.getMatrix()).getElement(i, j)).toString(), false);
                GridPane.setRowIndex(tf, i + 1);
                GridPane.setColumnIndex(tf, j + 1);
                stepPane.getChildren().add(tf);
            }
        }

        //highlight pivot elements
        int startIndex = stepMatrix.getCols() + stepMatrix.getRows() + 3;
        for (PivotElement el : stepMatrix.getPivotElements()) {
            int index = el.getI() * (stepMatrix.getCols() + 1) + el.getJ();
            tf = (TextField) stepPane.getChildren().get(startIndex + index);
            if (el.isBest()) {
                tf.setStyle("-fx-border-color:red;-fx-control-inner-background:#2494c4;");
                stepMatrix.setSelectedPivot(el);
            } else
                tf.setStyle("-fx-border-color:#27e827;");
            //what to do when choosing pivot el
            final int curStep = steps.size();
            tf.setOnMouseClicked(e -> {
                //block selection if it is not our current step
                if (steps.size() != curStep) return;
                //old el
                PivotElement oldPivotElement = stepMatrix.getSelectedPivot();
                if (oldPivotElement != null) {
                    TextField textField = (TextField) stepPane.getChildren().get(startIndex + oldPivotElement.getI() * (stepMatrix.getCols() + 1) + oldPivotElement.getJ());
                    textField.setStyle(textField.getStyle() + "-fx-control-inner-background:white;");
                }
                //new
                TextField textField = (TextField) e.getSource();
                stepMatrix.setSelectedPivot(el);
                System.out.println(stepMatrix.getSelectedPivot().getI() + " " + stepMatrix.getSelectedPivot().getJ());
                textField.setStyle(textField.getStyle() + "-fx-control-inner-background:#2494c4;");
            });

        }

        simplexSteps.getChildren().add(stepPane);
    }

}