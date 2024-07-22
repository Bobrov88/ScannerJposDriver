package Scanner;

import Logger.MyLogger;
import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.config.JposEntry.Prop;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;
import org.apache.log4j.Logger;

public final class ScannerInstanceFactory implements JposServiceInstanceFactory {
    private Logger logger = MyLogger.createLoggerInstance(ScannerInstanceFactory.class.getName());

    public ScannerInstanceFactory() {
        logger.info("ScannerInstanceFactory constructor called");
    }

    public JposServiceInstance createInstance(String paramString, JposEntry paramJposEntry) throws JposException {
        logger.debug("Creating instance of scanner");

        if (!(paramJposEntry.hasPropertyWithName("serviceClass"))) {
            logger.fatal("The JposEntry does not contain the 'serviceClass' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'serviceClass' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("portName"))) {
            logger.fatal("The JposEntry does not contain the 'portName' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'portName' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("baudRate"))) {
            logger.fatal("The JposEntry does not contain the 'baudRate' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'baudRate' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("dataBits"))) {
            logger.fatal("The JposEntry does not contain the 'dataBits' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'dataBits' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("stopBits"))) {
            logger.fatal("The JposEntry does not contain the 'stopBits' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'stopBits' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("parity"))) {
            logger.fatal("The JposEntry does not contain the 'parity' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'parity' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("productDescription"))) {
            logger.fatal("The JposEntry does not contain the 'productDescription' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'productDescription' property");
        }
        if (!(paramJposEntry.hasPropertyWithName("productName"))) {
            logger.fatal("The JposEntry does not contain the 'productName' property");
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'productName' property");
        }

        Prop comPortNumberProp = paramJposEntry.getProp("portName");
        Prop baudRateProp = paramJposEntry.getProp("baudRate");
        Prop dataBitsProp = paramJposEntry.getProp("dataBits");
        Prop stopBitsProp = paramJposEntry.getProp("stopBits");
        Prop parityProp = paramJposEntry.getProp("parity");
        Prop physicalDeviceDescriptionProp = paramJposEntry.getProp("productDescription");
        Prop physicalDeviceNameProp = paramJposEntry.getProp("productName");

        try {
            int comport = Integer.parseInt(comPortNumberProp.getValueAsString());
            String physicalDeviceDescription = physicalDeviceDescriptionProp.getValueAsString();
            String physicalDeviceName = physicalDeviceNameProp.getValueAsString();
            int baudRate = Integer.parseInt(baudRateProp.getValueAsString());
            int dataBits = Integer.parseInt(dataBitsProp.getValueAsString());
            int stopBits = Integer.parseInt(stopBitsProp.getValueAsString());
            String parity = parityProp.getValueAsString();

            ScannerService localJposServiceInstance = null;
            localJposServiceInstance = new ScannerService();
            localJposServiceInstance.setComPortNumber(comport);
            localJposServiceInstance.setBaudRate(baudRate);
            localJposServiceInstance.setDataBits(dataBits);
            localJposServiceInstance.setStopBits(stopBits);
            localJposServiceInstance.setParity(parity);
            localJposServiceInstance.setPhysicalDeviceDescription(physicalDeviceDescription);
            localJposServiceInstance.setPhysicalDeviceName(physicalDeviceName);
            return localJposServiceInstance;
        } catch (Exception localException) {
            logger.fatal(localException.toString());
            throw new JposException(jpos.JposConst.JPOS_E_NOSERVICE,
                    localException.getMessage() + ". Please, check your jpos.xml param values", localException);
        }
    }
}
