package edu.wpi.teamR.controllers;

import edu.wpi.teamR.App;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.Main;
import edu.wpi.teamR.controllers.mapeditor.*;
import edu.wpi.teamR.csv.CSVParameterException;
import edu.wpi.teamR.csv.CSVWriter;
import edu.wpi.teamR.datahandling.MapStorage;
import edu.wpi.teamR.mapdb.update.*;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import edu.wpi.teamR.mapdb.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javafx.geometry.Point2D;
import org.controlsfx.control.PopOver;

public class MapEditorController {
    @FXML
    Button importCSVButton;
    @FXML Button exportCSVButton;
    @FXML
    Button editButton;
    @FXML Button saveButton;
    @FXML Button newLocationButton;
    @FXML Button newNodeButton;
    @FXML Button newEdgeButton;
    @FXML Button newMoveButton;
    @FXML Button undoButton;
    @FXML Button editLocationButton;
    @FXML Button cancelEdgeButton;
    @FXML Button futureMoveButton;

    static File selectedFile;
    static File selectedDirectory;
    FileChooser fileChooser;

    DirectoryChooser directoryChooser;

    @FXML
    BorderPane borderPane;
    @FXML AnchorPane anchorPane;

    @FXML
    GesturePane gesturePane;

    @FXML
    ComboBox<String> floorComboBox;
    @FXML
    ComboBox<String> tableComboBox;
    @FXML
    MFXCheckbox locationCheckbox;
    @FXML Text dialogText;
    ObservableList<String> DAOType =
            FXCollections.observableArrayList("Node", "Edge", "LocationName", "Moves");
    ObservableList<String> floors =
            FXCollections.observableArrayList("Lower Level Two",
                    "Lower Level One",
                    "First Floor",
                    "Second Floor",
                    "Third Floor");

    URL firstFloorLink = Main.class.getResource("images/01_thefirstfloor.png");
    URL secondFloorLink = Main.class.getResource("images/02_thesecondfloor.png");
    URL thirdFloorLink = Main.class.getResource("images/03_thethirdfloor.png");
    URL LLOneLink = Main.class.getResource("images/00_thelowerlevel1.png");
    URL LLTwoLink = Main.class.getResource("images/00_thelowerlevel2.png");

    ImageView imageView;
    int currentFloor = 2;

    URL[] linkArray = {
            LLTwoLink, LLOneLink, firstFloorLink, secondFloorLink, thirdFloorLink,
    };

    String[] floorNames = {
            "Lower Level Two",
            "Lower Level One",
            "First Floor",
            "Second Floor",
            "Third Floor"
    };

    String[] nodeFloorNames = {
            "L2",
            "L1",
            "1",
            "2",
            "3"
    };

    AnchorPane[] nodePanes = new AnchorPane[5];
    AnchorPane[] locationPanes = new AnchorPane[5];

    final private AnchorPane mapPane = new AnchorPane();

    HashMap<String, Integer> floorNamesMap = new HashMap<>();

    private MapDatabase mapdb;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<LocationName> locationNames;
    ArrayList<Move> moves;
    Node selectedNode;
    Circle selectedCircle;
    boolean drawEdgesMode = false;
    @FXML
    HBox edgeHBox;

    MapUpdater updater;

    Map<Integer, Circle> circlesMap = new HashMap<>();
    Map<Integer, List<Line>> linesMap = new HashMap<>();

    Map<Integer, Node> nodeMap = new HashMap<>();
    Boolean dragged = false;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException, ItemNotFoundException {
        for (int i = 0; i < 5; i++) {
            nodePanes[i] = new AnchorPane();
            locationPanes[i] = new AnchorPane();
            floorNamesMap.put(nodeFloorNames[i], i);
            floorNamesMap.put(floorNames[i], i);
        }

        floorComboBox.setItems(floors);
        tableComboBox.setItems(DAOType);
        importCSVButton.setOnAction(event -> {
            try {
                importCSV();
            } catch (IOException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException | CSVParameterException | SQLException | ItemNotFoundException |
                     ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        exportCSVButton.setOnAction(event -> {
            try {
                export();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        editButton.setOnAction(event -> gesturePane.setGestureEnabled(false));
        saveButton.setOnAction(event -> {
            try {
                saveChanges();
            } catch (SQLException | ItemNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        floorComboBox.setOnAction(event -> {
            try {
                changeFloor(floorNamesMap.get(floorComboBox.getValue()));
            } catch (SQLException | ItemNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        newLocationButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/NewLocationPopup.fxml"));
            try {
                Parent popupRoot = loader.load();
                NewLocationPopupController popupController = loader.getController();
                popupController.setMapUpdater(updater);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Create New Location");
                popupStage.setScene(new Scene(popupRoot, 400, 200));
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        newNodeButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/NewNodePopup.fxml"));
            try {
                Parent popupRoot = loader.load();
                NewNodePopupController popupController = loader.getController();
                popupController.setUpdater(updater);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Create New Node");
                popupStage.setScene(new Scene(popupRoot, 400, 200));
                popupStage.showAndWait();

                Node newNode = nodes.get(nodes.size()-1);
                Circle newCircle = new Circle(newNode.getXCoord(), newNode.getYCoord(), 4, Color.RED);
                nodePanes[currentFloor].getChildren().add(newCircle);
                circlesMap.put(newNode.getNodeID(), newCircle);

                setupMapNode(currentFloor, newNode, newCircle);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        edgeDialog(false);
        newEdgeButton.setOnAction(event -> edgeDialog(true));

        undoButton.setOnAction(event -> {
            try {
                undoAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        locationCheckbox.setOnAction(event -> {
            if (locationCheckbox.isSelected()) {
                mapPane.getChildren().remove(nodePanes[currentFloor]);
                mapPane.getChildren().add(locationPanes[currentFloor]);
                mapPane.getChildren().add(nodePanes[currentFloor]);
            } else {
                mapPane.getChildren().remove(locationPanes[currentFloor]);
            }
        });


        imageView = new ImageView(linkArray[currentFloor].toExternalForm());
        gesturePane.setContent(mapPane);
        mapPane.getChildren().add(imageView);
        gesturePane.setMinScale(0.25);
        gesturePane.setMaxScale(2);

        try {
            App.setMapData(new MapStorage());
            mapdb = App.getMapData().getMapdb();
            updater = new MapUpdater(mapdb);
            nodes = App.getMapData().getNodes();
            edges = App.getMapData().getEdges();
            locationNames = App.getMapData().getLocationNames();
            moves = App.getMapData().getMoves();
            ArrayList<MapLocation> locations = mapdb.getMapLocationsByFloor(nodeFloorNames[currentFloor]);
            if (locations.size() > 0) {
                for (MapLocation location : locations) {
                    nodeMap.put(location.getNode().getNodeID(), location.getNode());
                }
                displayEdgesByFloor(currentFloor);
                displayNodesByFloor(currentFloor);
            }
            else {
                System.out.println("tables are empty");
            }
        }
        catch (ItemNotFoundException e) {
            e.printStackTrace();
        }

        editLocationButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/EditLocationPopup.fxml"));
            try {
                Parent popupRoot = loader.load();
                EditLocationPopupController popupController = loader.getController();
                popupController.setUpdater(updater);

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Edit Location");
                popupStage.setScene(new Scene(popupRoot, 300, 400));
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        futureMoveButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/FutureMovesTable.fxml"));
            try {
                Parent popupRoot = loader.load();

                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Future Moves");
                popupStage.setScene(new Scene(popupRoot, 400, 500));
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cancelEdgeButton.setOnAction(event -> edgeDialog(false));

        reset();
    }

    private void edgeDialog(boolean setting) {
        drawEdgesMode=setting;
        edgeHBox.setVisible(setting);
        edgeHBox.setManaged(setting);
        if (selectedCircle != null) {
            selectedNode = null;
            selectedCircle.setFill(Color.RED);
            selectedCircle = null;
        }
        dialogText.setText("Select Two Nodes to Draw Edge");
    }

    public void changeFloor(int floorNum) throws SQLException, ItemNotFoundException {
        if (floorNum < 4) {
            currentFloor = floorNum;
            imageView = new ImageView(linkArray[currentFloor].toExternalForm());
            mapPane.getChildren().clear();
            mapPane.getChildren().add(imageView);
            if (selectedNode == null) {
                reset();
            }
            redraw();
        }
    }

    public void reset() {
        // zoom to 1x
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.zoomTo(0.25, 0.25, new Point2D(2500, 1700));
        gesturePane.centreOn(new Point2D(2500, 1700));
    }


    public void displayNodesByFloor(int floor) throws SQLException, ItemNotFoundException {
        String f = nodeFloorNames[floor];
        mapPane.getChildren().add(locationPanes[currentFloor]);
        mapPane.getChildren().add(nodePanes[floor]);

        ArrayList<MapLocation> mapLocations = mapdb.getMapLocationsByFloor(f);
        for (MapLocation l: mapLocations) {
            drawNode(l, floor);
        }
    }

    public void displayEdgesByFloor(int floor) throws SQLException {
        if (floor < 4) {
            String f = nodeFloorNames[floor];
            ArrayList<Edge> edges = mapdb.getEdgesByFloor(f);
            for (Edge e: edges) {
                Node n1 = mapdb.getNodeByID(e.getStartNode());
                Node n2 = mapdb.getNodeByID(e.getEndNode());

                if (n1.getFloorNum().equals(n2.getFloorNum())) {
                    final Line l1 = new Line(n1.getXCoord(), n1.getYCoord(), n2.getXCoord(), n2.getYCoord());
                    l1.setStroke(Color.RED);
                    l1.setStrokeWidth(4);
                    addLine(e.getStartNode(), l1);
                    addLine(e.getEndNode(), l1);
                    nodePanes[floor].getChildren().add(l1);
                    l1.toBack();

                    l1.setOnMouseClicked(event -> {
                        if (event.getButton().equals(MouseButton.SECONDARY)) {
                            updater.deleteEdge(n1.getNodeID(), n2.getNodeID());
                            try {
                                mapdb.deleteEdge(n1.getNodeID(), n2.getNodeID());
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            updater.endAction();
                            linesMap.remove(e.getStartNode());
                            linesMap.remove(e.getEndNode());
                            nodePanes[floor].getChildren().remove(l1);
                            nodePanes[floor].requestLayout();
                        }
                    });
                }
            }
        }
    }

    public void importCSV () throws IOException, CSVParameterException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException, ItemNotFoundException, ClassNotFoundException {
        openFile();
        String choice = tableComboBox.getValue();
        switch (choice) {
            case "Node" -> mapdb.readCSV(selectedFile.getAbsolutePath(), Node.class);
            case "Edge" -> mapdb.readCSV(selectedFile.getAbsolutePath(), Edge.class);
            case "LocationName" -> mapdb.readCSV(selectedFile.getAbsolutePath(), LocationName.class);
            case "Moves" -> mapdb.readCSV(selectedFile.getAbsolutePath(), Move.class);
        }
        tableComboBox.getSelectionModel().clearSelection();
        tableComboBox.setValue(null);
    }

    public void openFile() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setTitle("Open File");
        selectedFile = fileChooser.showOpenDialog(importCSVButton.getScene().getWindow());
    }

    public void chooseDirectory() {
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        selectedDirectory = directoryChooser.showDialog(exportCSVButton.getScene().getWindow());
    }

    public void export() throws IOException, SQLException {
        String choice = tableComboBox.getValue();
        chooseDirectory();
        switch (choice) {
            case "Node" -> {
                CSVWriter<Node> nodeCSVWriter = new CSVWriter<>();
                nodeCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Nodes.csv", mapdb.getNodes());
            }
            case "Edge" -> {
                CSVWriter<Edge> edgeCSVWriter = new CSVWriter<>();
                edgeCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Edges.csv", mapdb.getEdges());
            }
            case "LocationName" -> {
                CSVWriter<LocationName> locationCSVWriter = new CSVWriter<>();
                locationCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/LocationNames.csv", mapdb.getLocationNames());
            }
            case "Moves" -> {
                CSVWriter<Move> moveCSVWriter = new CSVWriter<>();
                moveCSVWriter.writeCSV(selectedDirectory.getAbsolutePath() + "/Moves.csv", mapdb.getMoves());
            }
        }
        selectedDirectory = null;
        tableComboBox.getSelectionModel().clearSelection();
        tableComboBox.setValue(null);
    }

    public void drawNode(MapLocation l, int floor) {
        if (floor < 4) {
            ArrayList<LocationName> ln = l.getLocationNames();
            Node n = l.getNode();
            //Move m = mapdb.getLatestMoveByLocationName(ln.get(0).getLongName());

            Circle c = new Circle(n.getXCoord(), n.getYCoord(), 5, Color.RED);
            nodePanes[floor].getChildren().add(c);
            circlesMap.put(n.getNodeID(), c);

            setupMapNode(floor, n, c);

            if (ln.size() > 0) {
                String shortName = ln.get(0).getShortName();
                if (!shortName.contains("Hall")) {
                    Text t = new Text(ln.get(0).getShortName());
                    t.setX(n.getXCoord() + 10);
                    t.setY(n.getYCoord());
                    t.setFill(Color.RED);
                    locationPanes[floor].getChildren().add(t);
                }
                locationCheckbox.setSelected(true);
            }
        }
    }

    private void setupMapNode(int floor, Node n, Circle c) {
        c.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !dragged) {
                if (selectedNode == null) {
                    selectedNode = n;
                    selectedCircle = c;
                    selectedCircle.setFill(Color.YELLOW);
                } else {
                    if (selectedNode.equals(n)) {
                        selectedCircle.setFill(Color.RED);
                        selectedNode = null;
                        edgeDialog(false);
                        return;
                    }
                    if (selectedNode.getFloorNum().equals(n.getFloorNum())) {
                        Line l1 = new Line(selectedNode.getXCoord(), selectedNode.getYCoord(), n.getXCoord(), n.getYCoord());
                        l1.setStrokeWidth(4);
                        l1.setStroke(Color.RED);
                        nodePanes[floor].getChildren().add(l1);
                        addLine(n.getNodeID(), l1);
                        addLine(selectedNode.getNodeID(), l1);
                        l1.toBack();
                        selectedCircle.setFill(Color.RED);
                        l1.setOnMouseClicked(evt -> {
                            if (!gesturePane.isGestureEnabled()) {
                                linesMap.remove(n.getNodeID());
                                nodePanes[floor].getChildren().remove(l1);
                                System.out.println("Edge removed");
                            }
                        });
                    }
                    else {
                        String startNodeType;
                        String endNodeType;
                        try {
                            startNodeType = mapdb.getNodeTypeByNodeID(selectedNode.getNodeID());
                            endNodeType = mapdb.getNodeTypeByNodeID(n.getNodeID());
                        } catch (SQLException | ItemNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        if (!(startNodeType.equals(endNodeType) && (startNodeType.equals("STAI") || startNodeType.equals("ELEV")))) {
                            dialogText.setText("Cannot create edge!");
                            selectedNode = null;
                            return;
                        }
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/EdgeCreatedPopup.fxml"));
                    try {
                        Parent popupRoot = loader.load();
                        EdgeCreatedPopupController popupController = loader.getController();
                        popupController.setText(selectedNode.getNodeID(), n.getNodeID());

                        Stage popupStage = new Stage();
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setTitle("Edge Created");
                        popupStage.setScene(new Scene(popupRoot, 400, 200));
                        popupStage.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updater.addEdge(selectedNode.getNodeID(), n.getNodeID());
                    try {
                        mapdb.addEdge(selectedNode.getNodeID(), n.getNodeID());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    updater.endAction();
                    selectedNode = null;
                    edgeDialog(false);
                }
            }
            else if (dragged) {
                dragged = false;
            }
            else {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    if(gesturePane.isGestureEnabled()) {
                        PopOver popOver = new PopOver();
                        final FXMLLoader loader =
                                new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/mapeditor/MapPopup.fxml"));
                        Parent popup;
                        try {
                            popup = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        MapPopupController controller = loader.getController();
                        try {
                            controller.showNodeInformation(updater, n);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        popOver.setContentNode(popup);
                        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                        popOver.setAutoHide(true);

                        popOver.showingProperty().addListener((observable, oldValue, newValue) -> {
                            if (controller.isDeleteButtonPressed()) {
                                nodePanes[floor].getChildren().remove(c);
                                if(linesMap.containsKey(n.getNodeID())) {
                                    nodePanes[floor].getChildren().removeAll(linesMap.get(n.getNodeID()));
                                }
                            }
                        });
                        popOver.show(c);
                    }
                }
            }
        });

        c.setOnMouseDragged(dragEvent -> {
            if (dragEvent.getButton().equals(MouseButton.SECONDARY)) return;
            gesturePane.setGestureEnabled(false);
            c.setCenterX(dragEvent.getX());
            c.setCenterY(dragEvent.getY());
            c.setFill(Color.LIMEGREEN);
            dragged = true;
        });
        c.setOnMouseReleased(dragEvent -> {
            if (dragEvent.getButton().equals(MouseButton.SECONDARY)) return;
            gesturePane.setGestureEnabled(true);
            c.setFill(Color.RED);
            try {
                if(linesMap.containsKey(n.getNodeID())) {
                    nodePanes[floor].getChildren().removeAll(linesMap.get(n.getNodeID()));
                }
                List<Edge> n_edges;
                n_edges = mapdb.getEdgesByNode(n.getNodeID());
                Line l1 = null;
                for(Edge e: n_edges) {
                    Node startNode = mapdb.getNodeByID(e.getStartNode());
                    Node endNode = mapdb.getNodeByID(e.getEndNode());
                    if (n.getNodeID() == e.getStartNode() && endNode.getFloorNum().equals(nodeFloorNames[floor])) {
                        l1 = new Line(dragEvent.getX(), dragEvent.getY(), endNode.getXCoord(), endNode.getYCoord());
                    }
                    else if (n.getNodeID() == e.getEndNode() && startNode.getFloorNum().equals(nodeFloorNames[floor])){
                        l1 = new Line(startNode.getXCoord(), startNode.getYCoord(), dragEvent.getX(), dragEvent.getY());
                    }
                    if (l1 != null && !nodePanes[floor].getChildren().contains(l1)) {
                        l1.setStroke(Color.RED);
                        l1.setStrokeWidth(4);
                        nodePanes[floor].getChildren().add(l1);
                        l1.toBack();
                        addLine(startNode.getNodeID(), l1);
                        addLine(endNode.getNodeID(), l1);
                    }
                }
                updater.modifyCoords(n.getNodeID(), (int) dragEvent.getX(), (int) dragEvent.getY());
                mapdb.modifyCoords(n.getNodeID(), (int) dragEvent.getX(), (int) dragEvent.getY());
                updater.endAction();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void redraw() throws SQLException, ItemNotFoundException {
        locationPanes[currentFloor].getChildren().clear();
        nodePanes[currentFloor].getChildren().clear();
        mapPane.getChildren().remove(nodePanes[currentFloor]);
        mapPane.getChildren().remove(locationPanes[currentFloor]);
        displayEdgesByFloor(currentFloor);
        displayNodesByFloor(currentFloor);
    }

    public void undoAction() throws SQLException {
        List<UndoData> data = updater.undo();
        System.out.println(data);
        for (UndoData undo: data) {
            MapDataType type = undo.data().getDataType();
            switch (type) {
                case NODE -> {
                    EditType editType = undo.editType();
                    Node node = (Node) (undo.data());
                    nodes.add(node);
                    switch (editType) {
                        case ADDITION -> {
                            Circle add = circlesMap.get(node.getNodeID());
                            nodePanes[currentFloor].getChildren().remove(add);
                            nodes.remove(node);
                            mapdb.deleteNode(node.getNodeID());
                        }
                        case MODIFICATION -> {
                            Circle mod = circlesMap.get(node.getNodeID());
                            mod.setCenterX(node.getXCoord());
                            mod.setCenterY(node.getYCoord());
                            mapdb.modifyCoords(node.getNodeID(), node.getXCoord(), node.getYCoord());
                            redrawEdges(node);
                        }
                        case DELETION -> {
                            Circle deleted = new Circle(node.getXCoord(), node.getYCoord(), 4, Color.RED);

                            setupMapNode(currentFloor, node, deleted);

                            nodePanes[currentFloor].getChildren().add(deleted);
                            nodes.add(node);
                            mapdb.addNode(node);
                        }
                    }
                }
                case EDGE -> {
                    EditType editType = undo.editType();
                    Edge edge = (Edge) (undo.data());
                    edges.add(edge);
                    switch(editType) {
                        case ADDITION -> {
                            List<Line> line_list = linesMap.get(edge.getStartNode());
                            nodePanes[currentFloor].getChildren().remove(line_list.get(line_list.size()-1));
                            edges.remove(edge);
                            mapdb.deleteEdge(edge.getStartNode(), edge.getEndNode());
                        }
                        case MODIFICATION -> {
                            Node startNode = mapdb.getNodeByID(edge.getStartNode());
                            Node endNode = mapdb.getNodeByID(edge.getEndNode());
                            if (linesMap.containsKey(edge.getStartNode())) {
                                nodePanes[currentFloor].getChildren().removeAll(linesMap.get(edge.getStartNode()));
                            }
                            if (linesMap.containsKey(edge.getEndNode())) {
                                nodePanes[currentFloor].getChildren().removeAll(linesMap.get(edge.getEndNode()));
                            }
                            Line line = new Line(startNode.getXCoord(), startNode.getYCoord(), endNode.getXCoord(), endNode.getYCoord());
                            line.setStrokeWidth(4);
                            line.setStroke(Color.RED);
                            nodePanes[currentFloor].getChildren().add(line);
                            edges.add(edge);
                            mapdb.addEdge(edge.getStartNode(), edge.getEndNode());
                        }
                        case DELETION -> {
                            Node startNode = mapdb.getNodeByID(edge.getStartNode());
                            Node endNode = mapdb.getNodeByID(edge.getEndNode());
                            Line line = new Line(startNode.getXCoord(), startNode.getYCoord(), endNode.getXCoord(), endNode.getYCoord());
                            line.setStrokeWidth(4);
                            line.setStroke(Color.RED);
                            nodePanes[currentFloor].getChildren().add(line);
                            edges.add(edge);
                            mapdb.addEdge(edge.getStartNode(), edge.getEndNode());
                        }
                    }
                }
                case MOVE -> {
                    Move move = (Move) (undo.data());
                    EditType editType = undo.editType();
                    moves.add(move);
                    switch(editType) {
                        case ADDITION -> {
                            moves.remove(move);
                            mapdb.deleteMove(move.getNodeID(), move.getLongName(), move.getMoveDate());
                        }
                        case MODIFICATION -> {
                        }
                        case DELETION -> {
                            moves.add(move);
                            mapdb.addMove(move.getNodeID(), move.getLongName(), move.getMoveDate());
                        }
                    }
                }
                case LOCATION_NAME -> {
                    LocationName locationName = (LocationName) (undo.data());
                    locationNames.add(locationName);
                    mapdb.addLocationName(locationName.getLongName(), locationName.getShortName(), locationName.getNodeType());
                }
            }
        }
    }

    private void saveChanges() throws SQLException, ItemNotFoundException {
        gesturePane.setGestureEnabled(true);
        updater.submitUpdates();
        redraw();
    }

    private void addLine(int nodeID, Line line) {
        if (linesMap.containsKey(nodeID)) {
            linesMap.get(nodeID).add(line);
        } else {
            ArrayList<Line> lines = new ArrayList<>();
            lines.add(line);
            linesMap.put(nodeID, lines);
        }
    }

    private void redrawEdges(Node n) {
        try {
            if(linesMap.containsKey(n.getNodeID())) {
                nodePanes[currentFloor].getChildren().removeAll(linesMap.get(n.getNodeID()));
                System.out.println("removing edges");
            }
            List<Edge> n_edges;
            n_edges = mapdb.getEdgesByNode(n.getNodeID());
            for(Edge e: n_edges) {
                Node startNode = mapdb.getNodeByID(e.getStartNode());
                Node endNode = mapdb.getNodeByID(e.getEndNode());
                if (startNode.getFloorNum().equals(endNode.getFloorNum())) {
                    final Line l1 = new Line(startNode.getXCoord(), startNode.getYCoord(), endNode.getXCoord(), endNode.getYCoord());
                    l1.setStroke(Color.RED);
                    l1.setStrokeWidth(4);
                    addLine(e.getStartNode(), l1);
                    addLine(e.getEndNode(), l1);
                    nodePanes[currentFloor].getChildren().add(l1);
                    l1.toBack();

                    l1.setOnMouseClicked(event -> {
                        if (event.getButton().equals(MouseButton.SECONDARY)) {
                            updater.deleteEdge(startNode.getNodeID(), endNode.getNodeID());
                            try {
                                mapdb.deleteEdge(startNode.getNodeID(), endNode.getNodeID());
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            updater.endAction();
                            linesMap.remove(e.getStartNode());
                            linesMap.remove(e.getEndNode());
                            nodePanes[currentFloor].getChildren().remove(l1);
                            nodePanes[currentFloor].requestLayout();
                        }
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
