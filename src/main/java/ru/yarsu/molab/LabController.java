package ru.yarsu.molab;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.scene.media.Media;
import javafx.util.Duration;

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
    public CheckBox artifBasis;
    @FXML
    public VBox artificialBasisPane;
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
    private Solver artificialSolver;
    private StepMatrix diagMatrix;
    private ArrayList<StepMatrix> steps = new ArrayList<StepMatrix>();
    private ArrayList<StepMatrix> artSteps = new ArrayList<StepMatrix>();
    private ArrayList<Integer> usedRows = new ArrayList<>();

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
        tf.setVisible(false);

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
        tf = generateCell(objF[solver.getMAX_SIZE()-1].toString(), true);
        GridPane.setRowIndex(tf, 1);
        GridPane.setColumnIndex(tf, varN + 1);
        objectiveFunction.getChildren().add(tf);
        tf.setVisible(false);
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
            //обработчик событий на клики
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
            tf = generateCell(constraints[i - 1][solver.getMAX_SIZE()-1].toString(), true);
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
            constraints[i][solver.getMAX_SIZE()-1] = new Fraction(((TextField) (table.getChildren().get((i + 1) * (cols + 2) + cols + 1))).getText());
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
    public void clear() {
        answer.setText("");
        simplexSteps.getChildren().clear();
        steps.clear();
        artificialBasisPane.getChildren().clear();
        artSteps.clear();
        diagMatrix = null;
        diagMatrixPane.getChildren().clear();
        usedRows.clear();
    }
    @FXML
    private void handleNewFile() {
        startIterationButton.setDisable(true);
        stepBack.setDisable(true);
        clear();
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
        clear();
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
        Image cat = new Image(new File("src/main/resources/ru/yarsu/molab/Zxcursed.gif").toURI().toString());
        ImageView imageView = new ImageView(cat);
        imageView.setStyle( "-fx-alignment: BOTTOM;");
        Media pick = new Media(new File("src/main/resources/ru/yarsu/molab/galileo.mp3").toURI().toString());
        MediaPlayer player = new MediaPlayer(pick);
        player.setOnEndOfMedia(() -> {
            player.seek(Duration.ZERO);
            player.play();
        });
        player.play();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setGraphic(imageView);
        alert.getDialogPane().getChildren().get(0).setStyle("-fx-background-color: #fcfcff;");
        alert.getDialogPane().getChildren().get(1).setStyle("-fx-background-color: #fcfcff; -fx-font-size: 30");
        alert.getDialogPane().getChildren().get(2).setStyle("-fx-background-color: #fcfcff;");
//        alert.getDialogPane().getChildren().get(0).setStyle("-fx-background-color: white;");
//        alert.getDialogPane().getChildren().get(1).setStyle("-fx-background-color: white; -fx-font-size: 30");
//        alert.getDialogPane().getChildren().get(2).setStyle("-fx-background-color: white;");

        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText("Волков Андрей ИВТ32БО");
        alert.showAndWait();
        player.stop();
    }

    @FXML
    private void handleHelp() {

    }

    @FXML
    private void apply() throws IllegalArgumentException {
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

    private void artificialBasis() {
        StepMatrix stepMatrix;

        startIterationButton.setDisable(false);
        //конвертировали во вспомогательную задачу
        artificialSolver = solver.toSupportingTask();
        artificialSolver.print();


        //делаем диагональную
        //oX и oY
        int[] arr = new int[artificialSolver.getVarN()];
        for (int i = 0; i < artificialSolver.getVarN(); i++) {
            arr[i] = i;
        }
        int[] arr1 = new int[artificialSolver.getConstraintsN()];
        for (int i = 0; i < artificialSolver.getConstraintsN(); i++) {
            arr1[i] = i;
        }
        diagMatrix = new StepMatrix(artificialSolver.toMatrix(artificialSolver.getConstraintsN(), artificialSolver.getVarN() + 1), arr, arr1);

        //поменяем колонки (n+1..m - влево)
        //tmp базис чтобы не портить основной
        ArrayList<Integer> curBasis = new ArrayList<>();
        for (int i = artificialSolver.getVarN() - artificialSolver.getConstraintsN(); i < artificialSolver.getVarN(); i++)
            curBasis.add(i);
        for (int dest = 0; dest < curBasis.size(); dest++) {
            int src = curBasis.get(dest);
            for (int i = src; i > dest; i--)
                diagMatrix.swapColumns(i, i-1);
        }

        //так как уже приведена к диагональному, дальнейшие действия не требуются
        diagMatrix.print();
        createDiagMatrixPane();

    }

    private boolean defaultCalc() {
        System.out.println(basis);
        //пользователь выбрал базисные переменные самостоятельно
        if (basis.size() != solver.getConstraintsN()) {
            stepBack.setDisable(true);
            startIterationButton.setDisable(true);
            alert("Выбрано недостаточно базисных переменных.\nВыбор осуществляется кликом на a1,a2...\nПорядок выбора имеет значение");
            return false;
        }
        startIterationButton.setDisable(false);

        //oX and oY
        int[] arr = new int[solver.getVarN()];
        for (int i = 0; i < solver.getVarN(); i++) {
            arr[i] = i;
        }
        int[] arr1 = new int[solver.getConstraintsN()];
        for (int i = 0; i < solver.getConstraintsN(); i++) {
            arr1[i] = i;
        }
        diagMatrix = new StepMatrix(solver.toMatrix(solver.getConstraintsN(), solver.getVarN() + 1), arr, arr1);
        //поменяем колонки (выбранные базисные - влево)
        //tmp базис чтобы не портить основной
        ArrayList<Integer> curBasis = new ArrayList<>(basis);
        for (int i = 0; i < basis.size(); i++) {
            if (i == basis.get(i)) continue;
            diagMatrix.swapColumns(i, curBasis.get(i));
            if (curBasis.contains(i)) {
                int old = curBasis.get(i);
                curBasis.remove(Integer.valueOf(i));
                curBasis.add(old);
            }
        }
        //считаем определитель
        Fraction det = diagMatrix.getMatrix().calcDet();
        if (det.getNumerator() == 0) {
            alert("Определитель матрицы по выбранным базисным столбцам = 0");
            startIterationButton.setDisable(true);
            stepBack.setDisable(true);
            return false;
        }
        //если на главной диагонали есть 0
        if (diagMatrix.getMatrix().zeroOnMainDiagonal()) {
            alert("На главной диагонали есть 0. Выберите другие базисные столбцы/их порядок.");
            startIterationButton.setDisable(true);
            stepBack.setDisable(true);
            return false;
        }
        System.out.println("determinant = " + det);


        //приводим в диагональный вид
        diagMatrix.getMatrix().makeDiagonal();
        System.out.println("diag matrix:");
        createDiagMatrixPane();
        return true;
    }

    @FXML
    private void startCalc() {
        //сохраняем перед вычислениями
        try {
            apply();
        } catch (IllegalArgumentException e) {
            alert("Проверьте правильность введенных данных");
            return;
        }
        clear();

        //искусственный базис или обычный метод
        if (artifBasis.isSelected()) {
            artificialBasis();
        } else {
            if (!defaultCalc()) return;
        }

        //авто-решение
        if (autoSolve.isSelected()) {
            while (steps.size() == 0 || steps.get(steps.size() - 1).getPivotElements().size() != 0) {
                nextStep();
            }
        } else {
            nextStep();
        }

    }

    public StepMatrix diagToFirstSimplexStep(Solver solver) {
        StepMatrix stepMatrix;
        //диагональная -> шаг симплекс метода
        //свободные и базисные переменные
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
        //вычисляем p
        //считаем коеф у целевой ф-ии
        Fraction coef;
        for (int j = 0; j < oX.length; j++) {
            //coef at free var
            coef = new Fraction(solver.getObjF()[oX[j]]);
            for (int i = 0; i < oY.length; i++) {
                coef = coef.add(simplexMatrix[i][j].multiply(new Fraction(-1, 1)).multiply(solver.getObjF()[oY[i]]));
            }
            simplexMatrix[oY.length][j] = coef;
        }
        //считаем b
        //то же самое, без умножения на -1
        coef = new Fraction(solver.getObjF()[solver.getObjF().length - 1]);
        for (int i = 0; i < oY.length; i++) {
            coef = coef.add(simplexMatrix[i][oX.length].multiply(solver.getObjF()[oY[i]]));
        }
        simplexMatrix[oY.length][oX.length] = coef.multiply(new Fraction(-1, 1));

        Matrix matrix = new Matrix(simplexMatrix, oY.length + 1, oX.length + 1);
        stepMatrix = new StepMatrix(matrix, oX, oY);
        return stepMatrix;
    }

    public StepMatrix artificialToFirstSimplexStep() {
        //берем последнюю матрицу из шагов искусственного базиса
        StepMatrix stepMatrix = new StepMatrix(artSteps.get(artSteps.size()-1));
        //остается только вычислить новое p
        Fraction coef;
        for (int j = 0; j < stepMatrix.getoX().length; j++) {
            //coef at free var
            coef = new Fraction(solver.getObjF()[stepMatrix.getoX()[j]]);
            for (int i = 0; i < stepMatrix.getoY().length; i++) {
                coef = coef.add(stepMatrix.getMatrix().getElement(i,j).multiply(new Fraction(-1, 1)).multiply(solver.getObjF()[stepMatrix.getoY()[i]]));
            }
            stepMatrix.getMatrix().setElement(stepMatrix.getoY().length, j, coef);
        }
        //todo контроль отрицательных b - такого быть не должно (10 пример, искусственный базис)
        //и b
        coef = new Fraction(solver.getObjF()[solver.getObjF().length - 1]);
        for (int i = 0; i < stepMatrix.getoY().length; i++) {
            coef = coef.add(stepMatrix.getMatrix().getElement(i, stepMatrix.getoX().length).multiply(solver.getObjF()[stepMatrix.getoY()[i]]));
        }
        stepMatrix.getMatrix().setElement(stepMatrix.getoY().length, stepMatrix.getoX().length, coef.multiply(new Fraction(-1, 1)));
        return stepMatrix;
    }
    public void nextStep() {
        //если искусственный И не завершили искусственный базис
        if (artifBasis.isSelected() && steps.size() == 0) {
            //если шагов было сделано достаточно для свапа переменных, то переходим к симплекс методу
            if (artSteps.size() == artificialSolver.getConstraintsN()+1) {
                StepMatrix firstSimplexStep = artificialToFirstSimplexStep();

                steps.add(firstSimplexStep);
                firstSimplexStep.findPivotElements();
                if (firstSimplexStep.getPivotElements().size() == 0) {
                    startIterationButton.setDisable(true);
                    answer.setText(firstSimplexStep.getAnswer());
                    // end or -inf ????
                }
                createSimplexStepTable(firstSimplexStep, false);
            } else {
                StepMatrix artificialStepMatrix;
                //все еще решаем искусственный
                if (artSteps.size() == 0) {
                    //из диагональной делаем первый симплекс шаг
                    artificialStepMatrix = diagToFirstSimplexStep(artificialSolver);
                    //ищем опорные
                    artificialStepMatrix.findPivotElements();
                } else {
                    //очередной шаг
                    PivotElement selectedPivot = artSteps.get(artSteps.size() - 1).getSelectedPivot();
                    //добавляем в занятые строки
                    usedRows.add(selectedPivot.getI());
                    System.out.println("cur used rows" + usedRows);
                    //получаем следующую матрицу
                    artificialStepMatrix = artSteps.get(artSteps.size() - 1).nextStepMatrix();
                    //удаляем ненужный столбец
                    artificialStepMatrix.deleteCol(selectedPivot.getJ());
                    //ищем в ней опорные
                    artificialStepMatrix.findPivotElements();
                    //удаляем в ней опорные элементы в строках, которые уже были использованы
                    for (int i:usedRows) {
                        artificialStepMatrix.deletePivotsByRow(i);
                    }
                    //находим новый лучший опорный эл-т
                    artificialStepMatrix.findBestPivot();
                }
                artSteps.add(artificialStepMatrix);
                stepBack.setDisable(artSteps.size() == 1);
                createSimplexStepTable(artificialStepMatrix, true);
            }
        }
        else {
            StepMatrix stepMatrix;

            if (steps.size() == 0) {
                stepMatrix = diagToFirstSimplexStep(solver);
            } else {
                stepMatrix = steps.get(steps.size() - 1).nextStepMatrix();
            }
            steps.add(stepMatrix);
            stepMatrix.findPivotElements();
            //нашли ответ
            if (stepMatrix.getPivotElements().size() == 0) {
                startIterationButton.setDisable(true);
                answer.setText(stepMatrix.getAnswer());
            }
            stepBack.setDisable(steps.size() == 1 && !artifBasis.isSelected());
            createSimplexStepTable(stepMatrix, false);
        }
    }
    @FXML
    private void stepBack() {
        //если решаем с искусственным базисом И в шагах симплекса пусто
        if (steps.size() == 0 && artifBasis.isSelected()) {
            int stepToRemove = artSteps.size() - 1;
            artSteps.remove(stepToRemove);
            artificialBasisPane.getChildren().remove(stepToRemove);
            startIterationButton.setDisable(false);
            //если первый шаг
            stepBack.setDisable(artSteps.size() == 1);
            //удаляем последний элемент из usedRows т.к. строка освободилась
            usedRows.remove(usedRows.size()-1);
            System.out.println("cur used rows" + usedRows);
        } else if (steps.size() > 0) {
            answer.setText("");
            int stepToRemove = steps.size() - 1;
            steps.remove(stepToRemove);
            simplexSteps.getChildren().remove(stepToRemove);
            startIterationButton.setDisable(false);
            //если не искусственный И на первом шаге
            stepBack.setDisable(steps.size() == 1 && !artifBasis.isSelected());
        }


    }

    private void createSimplexStepTable(StepMatrix stepMatrix, boolean artificialBasisStep) {
        //чистим занятые строки если первый шаг искусственного базиса
        if (artificialBasisStep && artSteps.size() == 1) {
            usedRows.clear();
        }
        GridPane stepPane = new GridPane();

        TextField tf;

        //header
        tf = artificialBasisStep ? generateCell("x̄(" + (artificialBasisPane.getChildren().size()) + ")", false) : generateCell("x(" + (steps.size() - 1) + ")", false);
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

        //подсветка опорных элементов
        int startIndex = stepMatrix.getCols() + stepMatrix.getRows() + 3;
        for (PivotElement el : stepMatrix.getPivotElements()) {
            int index = el.getI() * (stepMatrix.getCols() + 1) + el.getJ();
            tf = (TextField) stepPane.getChildren().get(startIndex + index);
            if (el.isBest()) {
                tf.setStyle("-fx-border-color:red;-fx-control-inner-background:#2494c4;");
                stepMatrix.setSelectedPivot(el);
            } else
                tf.setStyle("-fx-border-color:#27e827;");
            //текущий шаг
            final int curStep = (artificialBasisStep ? artSteps.size() : steps.size());
            //обработка клика
            tf.setOnMouseClicked(e -> {
                //если не текущий шаг, то не даем возможность выбора
                if ((artificialBasisStep ? artSteps : steps).size() != curStep) return;

                PivotElement oldPivotElement = stepMatrix.getSelectedPivot();
                if (oldPivotElement != null) {
                    TextField textField = (TextField) stepPane.getChildren().get(startIndex + oldPivotElement.getI() * (stepMatrix.getCols() + 1) + oldPivotElement.getJ());
                    textField.setStyle(textField.getStyle() + "-fx-control-inner-background:white;");
                }

                TextField textField = (TextField) e.getSource();
                stepMatrix.setSelectedPivot(el);
                System.out.println(stepMatrix.getSelectedPivot().getI() + " " + stepMatrix.getSelectedPivot().getJ());
                textField.setStyle(textField.getStyle() + "-fx-control-inner-background:#2494c4;");
            });
        }

        if (artificialBasisStep) artificialBasisPane.getChildren().add(stepPane);
        else simplexSteps.getChildren().add(stepPane);
    }

}