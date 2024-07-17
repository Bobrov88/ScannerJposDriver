package test;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.*;

import jpos.JposException;
import jpos.config.JposEntry;
import Logger.MyLogger;
import org.apache.log4j.Logger;

import static test.Utility.extractLogicalName;
import static test.Utility.getJposEntryByLogicalName;

import Scanner.ScannerInstanceFactory;
import Scanner.ScannerService;

public class MainWindowController {
    @FXML
    private Label AvailableDeviceId;
    @FXML
    private Button ClaimID;
    @FXML
    private Button ClearID;
    @FXML
    private ComboBox<String> DeviceListId;
    private Map<String, DeviceListState> deviceLists;
    @FXML
    private AnchorPane MainWindowID;
    @FXML
    private Button OpenID;
    @FXML
    private Button CloseID;
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
        logger.debug("Initializing main window");
        setButtonsVisibility("");
        deviceLists = new HashMap<>();
        for (String deviceName : extractLogicalName()) {
            deviceLists.put(deviceName, new DeviceListState());
        }
        DeviceListId.setItems(FXCollections.observableArrayList(deviceLists.keySet()));
        DeviceListId.setValue("Choose device");

        DeviceListId.setOnAction(event -> {
            setButtonsVisibility(DeviceListId.getValue());
        });

        OpenID.setOnAction(event -> {
            String deviceName = DeviceListId.getValue();
            logger.debug("OpenId with device: " + deviceName);
            JposEntry jposEntry = getJposEntryByLogicalName(deviceName);
            try {
                ScannerInstanceFactory scanner = new ScannerInstanceFactory();
                scannerService = (ScannerService) scanner.createInstance("", jposEntry);
                scannerService.open(deviceName, null);
            } catch (JposException e) {
                logger.fatal(e.getMessage());
                throw new RuntimeException(e);
            }
            deviceLists.get(deviceName).onOpenClicked();
            setButtonsVisibility(deviceName);
            ScannedDataTextAreaID.textProperty().bind(scannerService.scannedBarcodeProperty());
        });

        ClaimID.setOnAction(event -> {
            logger.debug("Claiming");
            try {
                scannerService.claim(1000);
            } catch (JposException e) {
                logger.fatal(e.getMessage());
                throw new RuntimeException(e);
            }
            String deviceName = DeviceListId.getValue();
            deviceLists.get(deviceName).onClaimClicked();
            setButtonsVisibility(deviceName);
        });

        ReleaseID.setOnAction(event -> {
            logger.debug("Releasing");
            try {
                scannerService.release();
            } catch (JposException e) {
                logger.fatal(e.getMessage());
                throw new RuntimeException(e);
            }
            String deviceName = DeviceListId.getValue();
            deviceLists.get(deviceName).onReleaseClicked();
            setButtonsVisibility(deviceName);
        });

        CloseID.setOnAction(event -> {
            logger.debug("Closing");
            try {
                scannerService.close();
            } catch (JposException e) {
                logger.fatal(e.getMessage());
                throw new RuntimeException(e);
            }
            String deviceName = DeviceListId.getValue();
            deviceLists.get(deviceName).onCloseClicked();
            setButtonsVisibility(deviceName);
            ScannedDataTextAreaID.clear();
        });
    }
    void setButtonsVisibility(String chosenDevice) {
        if (chosenDevice.compareTo("") == 0) {
            OpenID.setDisable(true);
            ClaimID.setDisable(true);
            ReleaseID.setDisable(true);
            CloseID.setDisable(true);
            ScannedDataTextAreaID.setDisable(true);
        } else {
            DeviceListState d = deviceLists.get(chosenDevice);
            OpenID.setDisable(d.isOpenDisable);
            ClaimID.setDisable(d.isClaimDisable);
            ReleaseID.setDisable(d.isReleaseDisable);
            CloseID.setDisable(d.isCloseDisable);
            ScannedDataTextAreaID.setDisable(d.isScannedTextFiledDisable);
        }
    }
}