package test;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.*;

import javafx.scene.layout.TilePane;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;
import Logger.MyLogger;
import jpos.loader.JposServiceInstance;
import jpos.services.EventCallbacks;
import org.apache.log4j.Logger;
import test.Utility;

import static test.Utility.extractLogicalName;
import static test.Utility.getJposEntryByLogicalName;

import Scanner.ScannerInstanceFactory;
import Scanner.ScannerService;

public class MainWindowController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label AvailableDeviceId;
    @FXML
    private Button ClaimID;
    @FXML
    private Button ClearID;
    @FXML
    private ComboBox<String> DeviceListId;
    @FXML
    private AnchorPane MainWindowID;
    @FXML
    private Button OpenID;
    @FXML
    private Button ReleaseID;
    @FXML
    private Label ScannedDataID;
    @FXML
    private TextField ScannedDataTextAreaID;

    private ScannerService scannerService;

    private Logger logger = MyLogger.createLoggerInstance(MainWindowController.class.getName());
    @FXML
    void initialize() {
        ClaimID.setDisable(true);
        ReleaseID.setDisable(true);
        List<String> values = extractLogicalName();
        DeviceListId.setItems(FXCollections.observableArrayList(values));
        DeviceListId.setValue(values.get(0));
        logger.debug("Initializing main window");
        OpenID.setOnAction(event -> {
            ScannerInstanceFactory scanner = new ScannerInstanceFactory();
            JposEntry jposEntry = getJposEntryByLogicalName(DeviceListId.getValue());
            try {
                scannerService = (ScannerService) scanner.createInstance("", jposEntry);
            } catch (JposException e) {
                throw new RuntimeException(e);
            }
            OpenID.setDisable(true);
            ClaimID.setDisable(false);
            ReleaseID.setDisable(false);
        });
        ClaimID.setOnAction(event -> {
            try {
                scannerService.open("COM3", null);
            } catch (JposException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
