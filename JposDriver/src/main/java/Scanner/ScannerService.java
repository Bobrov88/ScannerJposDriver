package Scanner;

import Bytes.SendBytes;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jpos.JposConst;
import jpos.JposException;
import jpos.events.DataEvent;
import jpos.services.EventCallbacks;
import jpos.services.ScannerService114;
import org.apache.log4j.Logger;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import Logger.MyLogger;

import java.util.Arrays;

public class ScannerService implements ScannerService114 {
    private final Logger logger = MyLogger.createLoggerInstance(ScannerService.class.getName());
    private int comPort = 1;
    private int state = JposConst.JPOS_S_CLOSED;
    private int timeout = -1;
    private EventCallbacks callBack = null;
    private DataEvent dataEvent = null;
    private byte[] receivedData;
    private final StringProperty scannedBarcode = new SimpleStringProperty("");
    private boolean deviceEnable = false; // TODO static
    private boolean claimed = false; // TODO static
    private SerialPort serialPort; // TODO static

    public void setComPortNumber(int comPort) throws JposException {
        logger.info("Setting comport to " + comPort);
        this.comPort = comPort;
        if (!(comPort > 0 && comPort < 255)) {
            logger.fatal("Port number < 1 or > 255. Connection refused!");
            throw new JposException(JposConst.JPOS_E_FAILURE, "Invalid comm port number");
        }
    }
    private void setScannedBarcode(byte[] bytes) {
        scannedBarcode.set(Arrays.toString(bytes));
    }
    public StringProperty scannedBarcodeProperty() {
        return scannedBarcode;
    }
    public int getComPortNumber() {
        return this.comPort;
    }

    @Override
    public void clearInputProperties() throws JposException {

    }

    @Override
    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    @Override
    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    @Override
    public void compareFirmwareVersion(String s, int[] ints) throws JposException {

    }

    @Override
    public void updateFirmware(String s) throws JposException {

    }

    @Override
    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    @Override
    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    @Override
    public void resetStatistics(String s) throws JposException {

    }

    @Override
    public void retrieveStatistics(String[] strings) throws JposException {

    }

    @Override
    public void updateStatistics(String s) throws JposException {

    }

    @Override
    public void deleteInstance() throws JposException {

    }

    @Override
    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    @Override
    public int getPowerNotify() throws JposException {
        return 0;
    }

    @Override
    public void setPowerNotify(int i) throws JposException {

    }

    @Override
    public int getPowerState() throws JposException {
        return 0;
    }

    @Override
    public boolean getAutoDisable() throws JposException {
        return false;
    }

    @Override
    public void setAutoDisable(boolean b) throws JposException {

    }

    @Override
    public int getDataCount() throws JposException {
        return 0;
    }

    @Override
    public boolean getDataEventEnabled() throws JposException {
        return false;
    }

    @Override
    public void setDataEventEnabled(boolean b) throws JposException {

    }

    @Override
    public boolean getDecodeData() throws JposException {
        return false;
    }

    @Override
    public void setDecodeData(boolean b) throws JposException {

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
    }

    @Override
    public String getCheckHealthText() throws JposException {
        return null;
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
        return null;
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        return 0;
    }

    @Override
    public boolean getFreezeEvents() throws JposException {
        return false;
    }

    @Override
    public void setFreezeEvents(boolean b) throws JposException {

    }

    @Override
    public String getPhysicalDeviceDescription() throws JposException {
        return null;
    }

    @Override
    public String getPhysicalDeviceName() throws JposException {
        return null;
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

    }

    @Override
    public void directIO(int i, int[] ints, Object o) throws JposException {

    }

    @Override
    public void open(String logicalName, EventCallbacks eventCallbacks) throws JposException {
        logger.debug("Opening " + logicalName + ": COM" + String.valueOf(comPort));
        serialPort = new SerialPort("COM" + String.valueOf(comPort));
        try {
            serialPort.openPort();
            if (serialPort.isOpened())
                logger.debug("Serial port opened");
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            // TODO get from jpos.xml
            serialPort.addEventListener(new PortReader());
        } catch (SerialPortException e) {
            logger.fatal(e.getMessage());
            throw new RuntimeException(e);
        }
        state = JposConst.JPOS_S_IDLE;
        this.callBack = eventCallbacks;
        this.deviceEnable = true;
    }

    @Override
    public void release() throws JposException {
        logger.debug("Release device");
        if (!this.claimed || !this.deviceEnable)
            return;
        this.claimed = false;
        this.state = JposConst.JPOS_S_IDLE;
    }

    private class PortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                try {
                    byte[] tmp = serialPort.readBytes(event.getEventValue());
                    if (tmp != null)
                        receivedData = tmp;
                    logger.debug("Data: " + Arrays.toString(receivedData));
                    setScannedBarcode(receivedData);
                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}