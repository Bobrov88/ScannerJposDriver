package test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
    public Button ShowDeviceInfoButtonId;
    @FXML
    public Label TitleId;
    public ImageView LoadingId;
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
    private TextArea ScannedDataTextAreaID;
    @FXML
    private Button CopyScannedId;
    @FXML
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
        if (!deviceLists.isEmpty()) {
            DeviceListId.setValue("Choose device");

            DeviceListId.setOnAction(event -> {
                setButtonsVisibility(DeviceListId.getValue());
            });

            OpenID.setOnAction(event -> {
                String deviceName = DeviceListId.getValue();
                logger.debug("OpenId with device: " + deviceName);
                JposEntry jposEntry = getJposEntryByLogicalName(deviceName);
                if (jposEntry != null) {
                    try {
                        ScannerInstanceFactory scanner = new ScannerInstanceFactory();
                        scannerService = (ScannerService) scanner.createInstance("", jposEntry);
                        scannerService.open(deviceName, null);
                        deviceLists.get(deviceName).onOpenClicked();
                        setButtonsVisibility(deviceName);
                        ScannedDataTextAreaID.textProperty().bind(scannerService.scannedBarcodeProperty());
                    } catch (JposException | RuntimeException e) {
                        logger.fatal(e.getMessage());
                        showErrorWindow(e.getMessage());
                    }
                } else {
                    String message ="JposEntry is null. Please, check xml syntax or entry's data!";
                    logger.fatal(message);
                    showErrorWindow(message);
                }
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

            ClearID.setOnAction(event -> {
                logger.debug("Clearing");
                scannerService.setScannedBarcode("");
                try {
                    scannerService.clearInput();
                } catch (JposException e) {
                    throw new RuntimeException(e);
                }
            });

            CloseID.setOnAction(event -> {
                logger.debug("Closing");
                try {
                    scannerService.setScannedBarcode("");
                    scannerService.close();
                } catch (JposException e) {
                    logger.fatal(e.getMessage());
                    throw new RuntimeException(e);
                }
                String deviceName = DeviceListId.getValue();
                deviceLists.get(deviceName).onCloseClicked();
                setButtonsVisibility(deviceName);
            });
            CopyScannedId.setTooltip(new Tooltip("Copy to clipboard"));
            CopyScannedId.setOnAction(event -> {
                copyToClipboard(ScannedDataTextAreaID.getText());
            });

            ShowDeviceInfoButtonId.setOnAction(event -> {
                LoadingId.setVisible(true);
                ScannedDataTextAreaID.textProperty().unbind();
                ScannedDataTextAreaID.setDisable(true);
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            scannerService.getDeviceInfo();
                        } catch (jssc.SerialPortException | org.json.simple.parser.ParseException | JposException |
                                 InterruptedException e) {
                            logger.fatal(e.getMessage());
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                    @Override
                    protected void succeeded() {
                        LoadingId.setVisible(false);
                        ScannedDataTextAreaID.setDisable(false);
                        ScannedDataTextAreaID.textProperty().bind(scannerService.scannedBarcodeProperty());
                    }
                    @Override
                    protected void failed() {
                        succeeded();
                    }
                };
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            });
        } else {
            String message = "Jpos.xml not found or does not contain entries";
            logger.fatal(message);
            showErrorWindow(message);
        }
    }

    private void setButtonsVisibility(String chosenDevice) {
        if (chosenDevice.compareTo("") == 0) {
            OpenID.setDisable(true);
            ClaimID.setDisable(true);
            ReleaseID.setDisable(true);
            CloseID.setDisable(true);
            ScannedDataTextAreaID.setDisable(true);
            ShowDeviceInfoButtonId.setDisable(true);
            CopyScannedId.setDisable(true);
            ClearID.setDisable(true);
            LoadingId.setVisible(false);
        } else {
            DeviceListState d = deviceLists.get(chosenDevice);
            OpenID.setDisable(d.isOpenDisable);
            ClaimID.setDisable(d.isClaimDisable);
            ReleaseID.setDisable(d.isReleaseDisable);
            CloseID.setDisable(d.isCloseDisable);
            ScannedDataTextAreaID.setDisable(d.isScannedTextAreaDisable);
            ShowDeviceInfoButtonId.setDisable(d.isShowDeviceInfoDisable);
            CopyScannedId.setDisable(d.isCopyScannedDisable);
            ClearID.setDisable(d.isClearDisable);
        }
    }

    public void copyToClipboard(String str) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(str);
        clipboard.setContent(clipboardContent);
    }

    private void showErrorWindow(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}