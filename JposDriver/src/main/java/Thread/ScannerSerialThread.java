package Thread;

import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;

public class ScannerSerialThread implements Runnable{

    private RXTXPort serialPort;
    private InputStream inputStream;
    private Thread readThread;
    private int timeOut;
    private byte[] datasToSend = new byte[0];
    private byte[] dataReceived = new byte[0];
    private boolean busy;
    private final WaitNotBusyHelper notBusyWaiter = new WaitNotBusyHelper();

    private final WaitDataHelper dataWaiter = new WaitDataHelper();
    private ScannerSerialThread() {};

    public ScannerSerialThread(String commName) throws PortInUseException, UnsupportedCommOperationException {
        this();
        serialPort = new RXTXPort(commName);
        inputStream = serialPort.getInputStream();

        // activate the DATA_AVAILABLE notifier
        serialPort.notifyOnDataAvailable(true);

        // these are default values for Scanner check readers.
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_7, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);

        this.busy = true;

        // start the read thread
        readThread = new Thread(this);
        readThread.start();
    }

    private void sendMessage(RXTXPort serialPort, byte[] datas) throws IOException {
            serialPort.getOutputStream().write(datas);
    }
    private void receiveChar(RXTXPort serialPort) throws IOException {
        int dataSize = serialPort.getInputStream().available();
        int i = 0;
        while (i < dataSize) {
            try {
                dataReceived[i] = (byte)serialPort.getInputStream().read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        initwritetoport();
        try {
            sendMessage(serialPort, datasToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WaitNotBusyHelper getNotBusyWaiter() {
        return this.notBusyWaiter;
    }
    public WaitDataHelper getDataWaiter() {
        return this.dataWaiter;
    }

    public void sendSimpleOrderMessage(byte[] datas) {
        this.busy = true;
        if (datas != null) {
            this.datasToSend = datas;
        }
    }

    private void initwritetoport() {
        try {
            serialPort.notifyOnOutputEmpty(true);
        } catch (Exception e) {
            System.out.println("Error setting event notification");
            System.out.println(e.toString());
            System.exit(-1);
        }
    }

    public void abort() {
        if (this.readThread != null) {
            this.readThread.interrupt();
            this.serialPort.close();
        }
    }
}
