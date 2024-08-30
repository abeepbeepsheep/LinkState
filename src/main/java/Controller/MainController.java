package Controller;

import Model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainController {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Pane graphView;
    @FXML
    public Slider slider;
    @FXML
    public TableView<PathProperty[]> tableView;
    @FXML
    public Button openFile;
    private final FileChooser fileChooser = new FileChooser();
    private Graph graph;
    private Circle currentRouterCircle;
    private Router currentRouter, tableRouter;
    @FXML
    public void initialize(){
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
                ,new FileChooser.ExtensionFilter("DOT Files", "*.dot")
        );
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        slider.setStyle(".axis{-fx-tick-label-font: 12px;}");
        slider.styleProperty().bind(
                Bindings.concat("-fx-font-size: 20;")
        );
        slider.setVisible(false);
        slider.valueProperty().addListener((a, b, newNumber) -> {
            if (currentRouter == null) return;
            displayRoutingTable(currentRouter.getRouterID(), newNumber.intValue());
            tableView.getSelectionModel().select(newNumber.intValue() - 1);
        });
        tableView.setVisible(false);
    }
    private void displayJSON(String json){
        graphView.getChildren().clear();
        currentRouter = null;
        tableRouter = null;
        tableView.setVisible(true);
        tableView.getColumns().clear();
        tableView.setRowFactory(a ->{
            TableRow<PathProperty[]> row = new TableRow<>();
            row.setOnMouseClicked(ax -> {
                slider.setValue(row.getIndex() + 1);
            });
            return row;
        });
        Platform.runLater(() -> {
            graphView.setTranslateX(anchorPane.getWidth()/4 - graphView.getWidth()/2);
            graphView.setTranslateY(anchorPane.getHeight()/2 - graphView.getHeight()/2);
            graphView.setScaleX(1.2);
            graphView.setScaleY(1.2);
        });
        graph = new Graph();
        try {
            graph.fromJSON(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (Edge e: graph.getEdges()){
            ArrayList<Double> points = new ArrayList<>();
            for (Pair<Double, Double> coord: e.getPoints()){
                points.add(coord.getKey());
                points.add(coord.getValue());
            }
            Polyline p = new Polyline();
            p.getPoints().addAll(points);
            p.setFill(Paint.valueOf("#00000000"));
            p.setStrokeWidth(3);
            e.setPolyline(p);
            graphView.getChildren().add(p);
            Label lp = new Label(Integer.toString(e.getWeight()));
            Pair<Double, Double> position = e.getLp();
            lp.setLayoutX(position.getKey());
            lp.setLayoutY(position.getValue());
            lp.setFont(new Font("Cambria", 15));
            graphView.getChildren().add(lp);
        }
        for (Router r: graph.getRouters()){
            Pair<Double, Double> position = r.getPosition();
            Circle c = new Circle(position.getKey(), position.getValue(), 20);
            c.setFill(Paint.valueOf("#888888"));
            c.setStrokeWidth(3);
            c.setStroke(Paint.valueOf("#000000"));
            c.setOnMouseClicked(xx -> {
                displayRoutingTable(r.getRouterID(), (int) slider.getValue());
                if (currentRouterCircle != null){
                    currentRouterCircle.setStroke(Paint.valueOf("#000000"));
                }
                currentRouterCircle = c;
                currentRouter = r;
                c.setStroke(Paint.valueOf("#FF0000"));
            });
            Label l = new Label(r.getRouterID());
            l.setFont(new Font("Cambria", 20));
            l.setTextFill(Paint.valueOf("#000000"));
            l.setMouseTransparent(true);
            graphView.getChildren().add(c);
            graphView.getChildren().add(l);
            Platform.runLater(() -> {
                l.setLayoutX(c.getCenterX()-l.getWidth()/2);
                l.setLayoutY(c.getCenterY()-l.getHeight()/2);
            });

        }
        slider.setVisible(true);
        slider.setMin(1);
        slider.setMax(graph.getNumRouters());
    }

    public void displayRoutingTable(String routerID, int time){
        for (Edge e: graph.getEdges()){
            e.getPolyline().setStroke(Paint.valueOf("#000000"));
        }
        currentRouter = graph.getRouter(routerID);
        ArrayList<PathProperty[]> table = currentRouter.doRouting();
        for (PathProperty pp: table.get(time - 1)){
            Edge e = null;
            if (pp.accessed == 0) continue;
            for (int i = 0; i < pp.path.size() - 1; i++){
                Router head = graph.getRouter(pp.path.get(i));
                Router tail = graph.getRouter(pp.path.get(i+1));
                e = graph.getEdge(head, tail);
                Polyline polyline = e.getPolyline();
                polyline.setStroke(Paint.valueOf("#FF0000"));
            }
        }
        if (tableRouter != currentRouter){
            tableView.getColumns().clear();
            tableView.getItems().clear();
            ObservableList<PathProperty[]> data = FXCollections.observableArrayList();
            data.addAll(table);
            tableView.getColumns().add(getIndexColumn());
            tableView.getColumns().add(getSetColumn());
            for (int i = 0; i < graph.getNumRouters(); i++) {
                TableColumn<PathProperty[], Integer> routerHeader = new TableColumn<>(table.get(table.size()-1)[i].routerID);
                TableColumn<PathProperty[], String> lengthColumn = getLengthColumn(i);
                TableColumn<PathProperty[], String> pathColumn = getPathColumn(i);
                lengthColumn.setPrefWidth(60);
                routerHeader.getColumns().add(lengthColumn);
                lengthColumn.setReorderable(false);
                lengthColumn.setSortable(false);
                routerHeader.getColumns().add(pathColumn);
                pathColumn.setReorderable(false);
                pathColumn.setSortable(false);
                tableView.getColumns().add(routerHeader);
            }
            tableView.setItems(data);
        }
        tableRouter = currentRouter;
        tableView.refresh();
    }
    private static @NotNull TableColumn<PathProperty[], Integer> getIndexColumn(){
        TableColumn<PathProperty[], Integer> indexColumn = new TableColumn<>("Step");
        indexColumn.setCellFactory(p -> {
            TableCell<PathProperty[], Integer> cell = new TableCell<>() {
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        this.setText(Integer.toString(
                                1 + getTableRow().getIndex()));
                    }
                }
            };
            return cell;
        });
        indexColumn.setCellValueFactory(p ->{
            return new SimpleIntegerProperty(0).asObject();
        });
        indexColumn.setReorderable(false);
        indexColumn.setSortable(false);
        return indexColumn;
    }
    private static @NotNull TableColumn<PathProperty[], String> getSetColumn(){
        TableColumn<PathProperty[], String> setColumn = new TableColumn<>("Set");
        setColumn.setCellValueFactory(p -> {
            ArrayList<String> explored = new ArrayList<>();
            for (PathProperty pp: p.getValue()){
                if (pp.accessed != 0) explored.add(pp.routerID);
            }
            return new SimpleStringProperty(explored.toString());
        });
        setColumn.setSortable(false);
        setColumn.setReorderable(false);
        return setColumn;
    }
    private static @NotNull TableColumn<PathProperty[], String> getLengthColumn(int i) {
        TableColumn<PathProperty[], String> tc =
                new TableColumn<>("Length");
        final int colNo = i;
        tc.setCellValueFactory(p -> {
            String out = "∞";
            if (p.getValue()[colNo].pathLength != Integer.MAX_VALUE)
                out = Integer.toString(p.getValue()[colNo].pathLength);
            return new SimpleStringProperty(out);
        });
        tc.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    if (getTableRow().getItem()[colNo].accessed != 0 &&
                            getTableRow().getItem()[colNo].accessed <= getTableRow().getIndex() + 1) {
                        this.setStyle("-fx-background-color: rgba(236,177,65,0.37);");
                    }
                    this.setText(item);
                }
            }
        });
        return tc;
    }
    private static @NotNull TableColumn<PathProperty[], String> getPathColumn(int i) {
        TableColumn<PathProperty[], String> tc =
                new TableColumn<>("Path");
        final int colNo = i;
        tc.setCellValueFactory(p -> {
            if (p.getValue()[colNo].path.isEmpty()){
                return new SimpleStringProperty("-");
            }
            return new SimpleStringProperty(
                    p.getValue()[colNo].path.toString());
        });
        tc.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    if (getTableRow().getItem()[colNo].accessed != 0 &&
                            getTableRow().getItem()[colNo].accessed <= getTableRow().getIndex() + 1)
                        this.setStyle("-fx-background-color: rgba(236,177,65,0.37);");
                    this.setText(item);
                }
            }
        });
        return tc;
    }
    @FXML
    protected void openFileChooser() {
        File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        displayFile(selectedFile);
    }
    private void displayFile(File file){
        if (file == null) {
            return;
        }
        if (getFileExtension(file.getName()).equalsIgnoreCase("json")){
            try {
                String json = Files.readString(Path.of(file.getPath()));
                displayJSON(json);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        } else if (getFileExtension(file.getName()).equalsIgnoreCase("dot")){
            String dot, json;
            try {
                dot = Files.readString(Path.of(file.getPath()));
                json = DOTtoJSON.parseDOT(dot);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            displayJSON(json);
        }
    }
    public static String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
//TODO: file io on dot language and jsons
//TODO: clicking two nodes will show the shortest path
//TODO: display table on the javafx program
//TODO: optimise time complexity