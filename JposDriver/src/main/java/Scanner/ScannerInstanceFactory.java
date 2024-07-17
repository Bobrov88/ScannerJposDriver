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
        Prop lCommPortNumberProp = paramJposEntry.getProp("portName");
        Prop physicalDeviceDescriptionProp = paramJposEntry.getProp("productDescription");
        Prop physicalDeviceNameProp = paramJposEntry.getProp("productName");
        int comport = Integer.parseInt(lCommPortNumberProp.getValueAsString());
        String physicalDeviceDescription = physicalDeviceDescriptionProp.getValueAsString();
        String physicalDeviceName = physicalDeviceNameProp.getValueAsString();

        ScannerService localJposServiceInstance = null;
        try {
            localJposServiceInstance = new ScannerService();
            localJposServiceInstance.setComPortNumber(comport);
            localJposServiceInstance.setPhysicalDeviceDescription(physicalDeviceDescription);
            localJposServiceInstance.setPhysicalDeviceName(physicalDeviceName);
        } catch (Exception localException) {
            logger.fatal(localException.toString());
            throw new JposException(jpos.JposConst.JPOS_E_NOSERVICE,
                    "Could not create the service instance!", localException);
        }
        return localJposServiceInstance;
    }
}
