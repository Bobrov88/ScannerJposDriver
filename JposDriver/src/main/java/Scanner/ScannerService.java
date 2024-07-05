package Scanner;

import Bytes.SendBytes;
import Thread.ScannerSerialThread;
import jpos.JposConst;
import jpos.JposException;
import jpos.services.EventCallbacks;
import jpos.services.ScannerService114;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScannerService implements ScannerService114 {
    private static final Logger logger = LogManager.getLogger(ScannerService.class.getName());
    private int commPortNumber;
    private int state = JposConst.JPOS_S_CLOSED;
    private EventCallbacks callBack;
    private ScannerSerialThread internalThread = null;

    private boolean claimed = false;

    public void setCommPortNumber(int pCommPortNumber) throws JposException {
        // save the port number
        logger.info("Setting com port to " + pCommPortNumber);
        this.commPortNumber = pCommPortNumber;
        if (!(pCommPortNumber > 0 && pCommPortNumber < 255)) {
            logger.fatal("Port number < 1 or > 255. Connection refused!");
            throw new JposException(JposConst.JPOS_E_FAILURE, "Invalid comm port number");
        }
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
        return new byte[0];
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
        return false;
    }

    @Override
    public boolean getDeviceEnabled() throws JposException {
        return false;
    }

    @Override
    public void setDeviceEnabled(boolean b) throws JposException {

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
        return 0;
    }

    @Override
    public void claim(int timeout) throws JposException {
        try {
            // Create the internal thread
            this.internalThread = new ScannerSerialThread("COM" + this.commPortNumber);
            System.out.println(1);
            // Wait the thread is not busy
            waitThreadNotBusy();
            System.out.println(2);
            // Command the physical device to cancel insert operation
            logger.debug("Claiming... That's sending 'ReadDeviceInfo.' command");
            this.internalThread.sendSimpleOrderMessage(SendBytes.GET_DEVICE_INFO);
            System.out.println(3);
            waitThreadNotBusy();
           // System.out.println(4);
            // Command the physical device to eject check if their are one in
            //byte[] data2 = { IngenicoFunction.INGENICO_EJECT_CHECK };
            //this.internalThread.sendSimpleOrderMessage(data2);
            //System.out.println(5);
            // Wait that the communication thread is not busy
            //waitThreadNotBusy();
            //System.out.println(6);
            System.out.println(4);
            this.claimed = true;
        } catch (Exception e) {
            logger.fatal("Claim: " + e.getMessage());
            throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Error in device preparation", e);
        }
    }

    @Override
    public void close() throws JposException {

    }

    @Override
    public void checkHealth(int i) throws JposException {

    }

    @Override
    public void directIO(int i, int[] ints, Object o) throws JposException {

    }

    @Override
    public void open(String logicalName, EventCallbacks eventCallbacks) throws JposException {
        logger.debug("Opening with logical name: " + logicalName);
        this.state = JposConst.JPOS_S_IDLE;
        this.callBack = eventCallbacks;
    }

    @Override
    public void release() throws JposException {
        logger.debug("Release");
        if (this.claimed == false) {
            return;
        }
        this.internalThread.abort();
        this.claimed = false;
        this.state = JposConst.JPOS_S_IDLE;
    }

    private boolean waitThreadNotBusy() throws JposException {
        logger.debug("WaitThreadNotBusy");
        try {
            internalThread.getNotBusyWaiter().waitNotBusy();
        } catch (InterruptedException e) {
            logger.fatal("WaitThreadNotBusy: "  + e.getMessage());
            throw new JposException(JposConst.JPOS_E_FAILURE, "The waiting service has been interrupted");
        }
        return internalThread.getNotBusyWaiter().isNotified();
    }
}
