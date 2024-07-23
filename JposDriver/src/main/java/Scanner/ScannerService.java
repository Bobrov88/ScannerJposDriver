package Scanner;

import Bytes.SendBytes;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jpos.JposConst;
import jpos.JposException;
import jpos.events.DataEvent;
import jpos.events.ErrorEvent;
import jpos.services.EventCallbacks;
import jpos.services.ScannerService114;
import jssc.*;
import org.apache.log4j.Logger;
import Logger.MyLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ScannerService implements ScannerService114 {
    private final Logger logger = MyLogger.createLoggerInstance(ScannerService.class.getName());
    private String comPort = "/dev/ttyACM0";
    private int state = JposConst.JPOS_S_CLOSED;
    private int timeout = -1;
    private EventCallbacks callBack = null;
    private DataEvent dataEvent = null;
    private byte[] receivedData;
    private final StringProperty scannedBarcode = new SimpleStringProperty("");
    private boolean deviceEnable = false;
    private boolean claimed = false;
    private SerialPort serialPort;
    private int powerNotify = 0;
    private boolean autoDisable = false;
    private boolean dataEventEnabled = false;
    private boolean decodeData = false;
    private boolean freezeEvents = false;
    private String physicalDeviceDescription = "";
    private String physicalDeviceName = "";
    private int baudRate = SerialPort.BAUDRATE_9600;
    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;

    public void setComPortNumber(String comPort) throws JposException {
        this.comPort = comPort;
        if (!comPort.isEmpty()) {
            logger.fatal("Port name is empty. Connection refused!");
            throw new JposException(JposConst.JPOS_E_FAILURE, "Invalid com port number");
        }
    }

    public String getComPortNumber() {
        return this.comPort;
    }

    public void setScannedBarcode(String receivedData) {
        scannedBarcode.set(receivedData);
    }

    public StringProperty scannedBarcodeProperty() {
        return scannedBarcode;
    }

    @Override
    public void clearInputProperties() throws JposException {
        if (this.deviceEnable) {
            dataEvent = null;
            receivedData = null;
            setScannedBarcode("");
        } else {
            throw new JposException(JposConst.JPOS_E_CLOSED, "Device disable");
        }
    }

    @Override
    public boolean getCapCompareFirmwareVersion() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "To update the firmware, please use the included utility.");
    }

    @Override
    public boolean getCapUpdateFirmware() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "To update the firmware, please use the included utility.");
    }

    @Override
    public void compareFirmwareVersion(String s, int[] ints) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void updateFirmware(String s) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "To update the firmware, please use the included utility.");
    }

    @Override
    public boolean getCapStatisticsReporting() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public boolean getCapUpdateStatistics() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void resetStatistics(String s) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void retrieveStatistics(String[] strings) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void updateStatistics(String s) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void deleteInstance() throws JposException {
    }

    @Override
    public int getCapPowerReporting() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public int getPowerNotify() throws JposException {
        return this.powerNotify;
    }

    @Override
    public void setPowerNotify(int i) throws JposException {
        this.powerNotify = i;
    }

    @Override
    public int getPowerState() throws JposException {
        return 2000;
    }

    @Override
    public boolean getAutoDisable() throws JposException {
        return autoDisable;
    }

    @Override
    public void setAutoDisable(boolean b) throws JposException {
        this.autoDisable = b;
    }

    @Override
    public int getDataCount() throws JposException {
        return 0;
    }

    @Override
    public boolean getDataEventEnabled() throws JposException {
        return dataEventEnabled;
    }

    @Override
    public void setDataEventEnabled(boolean b) throws JposException {
        this.dataEventEnabled = b;
    }

    @Override
    public boolean getDecodeData() throws JposException {
        return this.decodeData;
    }

    @Override
    public void setDecodeData(boolean b) throws JposException {
        this.decodeData = b;
    }

    @Override
    public byte[] getScanData() throws JposException {
        return receivedData;
    }

    @Override
    public byte[] getScanDataLabel() throws JposException {
        return new byte[0];
    }

    @Override
    public int getScanDataType() throws JposException {
        return 0;
    }

    @Override
    public void clearInput() throws JposException {
        this.clearInputProperties();
    }

    @Override
    public String getCheckHealthText() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public boolean getClaimed() throws JposException {
        return this.claimed;
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        return this.deviceEnable;
    }

    @Override
    public void setDeviceEnabled(boolean b) throws JposException {
        this.deviceEnable = b;
    }

    @Override
    public String getDeviceServiceDescription() throws JposException {
        if (serialPort.isOpened() && deviceEnable) {
            try {
                serialPort.writeBytes(SendBytes.GET_DEVICE_INFO);
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
            return Arrays.toString(this.getScanData());
        } else {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Device is not enable. Please, check if service instance is created or port is opened!");
        }
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        return 1014000;
    }

    @Override
    public boolean getFreezeEvents() throws JposException {
        return this.freezeEvents;
    }

    @Override
    public void setFreezeEvents(boolean b) throws JposException {
        this.freezeEvents = b;
    }

    @Override
    public String getPhysicalDeviceDescription() throws JposException {
        return this.physicalDeviceDescription;
    }

    public void setPhysicalDeviceDescription(String physicalDeviceDescription) throws JposException {
        this.physicalDeviceDescription = physicalDeviceDescription;
    }

    @Override
    public String getPhysicalDeviceName() throws JposException {
        return physicalDeviceName;
    }

    public void setPhysicalDeviceName(String physicalDeviceName) throws JposException {
        this.physicalDeviceName = physicalDeviceName;
    }

    @Override
    public int getState() throws JposException {
        return state;
    }

    @Override
    public void claim(int timeout) throws JposException {
        if (claimed) {
            logger.fatal("JPOS_E_ILLEGAL state. Device cannot be claimed!");
            throw new JposException(JposConst.JPOS_E_CLAIMED, "JPOS_E_ILLEGAL state. Device cannot be claimed!");
        }
        this.claimed = true;
        this.timeout = timeout;
    }

    @Override
    public void close() throws JposException {
        logger.debug("Close device");
        this.deviceEnable = false;
        this.claimed = false;
        this.state = JposConst.JPOS_S_CLOSED;
        if (serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void checkHealth(int i) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void directIO(int i, int[] ints, Object o) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL, "Method is not supported by this service");
    }

    @Override
    public void open(String logicalName, EventCallbacks eventCallbacks) throws JposException {
        logger.debug("Opening " + logicalName + "with comPort" + this.comPort);
        try {
            serialPort = new SerialPort(this.comPort);
            serialPort.openPort();
            if (serialPort.isOpened()) {
                logger.debug("Serial port opened");
                serialPort.setParams(baudRate, dataBits, stopBits, parity);
                serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
                state = JposConst.JPOS_S_IDLE;
                this.callBack = eventCallbacks;
                this.deviceEnable = true;
            }
        } catch (SerialPortException e) {
            logger.fatal(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void release() throws JposException {
        logger.debug("Release device");
        if (!this.claimed || !this.deviceEnable)
            return;
        this.claimed = false;
        this.state = JposConst.JPOS_S_IDLE;
    }

    public void setStopBits(int stopBits) {
        switch (stopBits) {
            case 1:
                this.stopBits = SerialPort.STOPBITS_1;
            case 2:
                this.stopBits = SerialPort.STOPBITS_2;
            case 3:
                this.stopBits = SerialPort.STOPBITS_1_5;
            default:
                this.stopBits = SerialPort.STOPBITS_1;
        }
    }

    public void setParity(String parity) {
        switch (parity) {
            case "Odd":
                this.parity = SerialPort.PARITY_ODD;
            case "Even":
                this.parity = SerialPort.PARITY_EVEN;
            case "Space":
                this.parity = SerialPort.PARITY_SPACE;
            case "Mark":
                this.parity = SerialPort.PARITY_MARK;
            case "None":
                this.parity = SerialPort.PARITY_NONE;
            default:
                this.parity = SerialPort.PARITY_NONE;
        }
    }

    public void setDataBits(int dataBits) {
        switch (dataBits) {
            case 5:
                this.dataBits = SerialPort.DATABITS_5;
            case 6:
                this.dataBits = SerialPort.DATABITS_6;
            case 7:
                this.dataBits = SerialPort.DATABITS_7;
            case 8:
                this.dataBits = SerialPort.DATABITS_8;
            default:
                this.dataBits = SerialPort.DATABITS_8;
        }
    }

    public void setBaudRate(int baudRate) {
        switch (baudRate) {
            case 9600:
                this.baudRate = SerialPort.BAUDRATE_9600;
            case 14400:
                this.baudRate = SerialPort.BAUDRATE_14400;
            case 19200:
                this.baudRate = SerialPort.BAUDRATE_19200;
            case 38400:
                this.baudRate = SerialPort.BAUDRATE_38400;
            case 57600:
                this.baudRate = SerialPort.BAUDRATE_57600;
            case 115200:
                this.baudRate = SerialPort.BAUDRATE_115200;
            default:
                this.baudRate = SerialPort.BAUDRATE_9600;
        }
    }

    public void getDeviceInfo() throws SerialPortException, ParseException, JposException, InterruptedException {
        logger.debug("Getting device full info");
        clearInput();
        StringBuilder str = new StringBuilder();
        if (serialPort.isOpened() && deviceEnable) {
            serialPort.writeBytes(SendBytes.GET_CONFIG_01);
        }
        Thread.sleep(1000);
        logger.debug(scannedBarcode.getValue());
        Object obj = new JSONParser().parse(scannedBarcode.getValue());
        JSONObject jsonObject = (JSONObject) obj;

        String deviceName = (String) jsonObject.get("deviceName");
        String deviceFID = (String) jsonObject.get("FID");
        String deviceID = (String) jsonObject.get("deviceID");
        str.append("Product name: ")
                .append(deviceFID)
                .append(System.getProperty("line.separator"))
                .append("Model: ")
                .append(deviceName)
                .append(System.getProperty("line.separator"))
                .append("Serial number: ")
                .append(deviceID)
                .append(System.getProperty("line.separator"));

        if (serialPort.isOpened() && deviceEnable) {
            serialPort.writeBytes(SendBytes.GET_DEVICE_INFO);
        }
        Thread.sleep(1000);
        str.append(scannedBarcode.getValue());
        this.setScannedBarcode(str.toString());
        logger.info(str.toString());
    }

    private class PortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    if (serialPort.isOpened() && deviceEnable) {
                        clearInput();
                        String tempStr = "";
                        StringBuilder str = new StringBuilder();
                        while (true) {
                            try {
                                tempStr = serialPort.readString(event.getEventValue(), 100);
                            } catch (SerialPortTimeoutException e) {
                                break;
                            }
                            if (tempStr.charAt(0) == 13) break;
                            str.append(tempStr);
                            tempStr = "";
                        }
                        logger.debug("Data: " + str);
                        setScannedBarcode(str.toString());
                        receivedData = str.toString().getBytes(StandardCharsets.UTF_8);
                        if (callBack != null)
                            callBack.fireDataEvent(new DataEvent(this, 0));
                    }
                } catch (SerialPortException e) {
                    if (callBack != null)
                        callBack.fireErrorEvent(new ErrorEvent(this, JposConst.JPOS_E_EXTENDED, 0, 0, 0));
                    throw new RuntimeException(e);
                } catch (JposException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}