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

        if (!(paramJposEntry.hasPropertyWithName("serviceClass")))
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'serviceClass' property");
        if (!(paramJposEntry.hasPropertyWithName("portName")))
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'portName' property");
        int lCommPortNumber = -1;
        Prop lCommPortNumberProp = paramJposEntry.getProp("portName");

        lCommPortNumber = Integer.parseInt(lCommPortNumberProp
                .getValueAsString());

        logger.info("Port name: " + lCommPortNumber);

        ScannerService localJposServiceInstance = null;
        try {
            localJposServiceInstance = new ScannerService();
            localJposServiceInstance.setCommPortNumber(lCommPortNumber);
        } catch (Exception localException) {
            logger.fatal("Connection to port: " + lCommPortNumber + " failed");
            throw new JposException(jpos.JposConst.JPOS_E_NOSERVICE,
                    "Could not create the service instance!", localException);
        }
        logger.debug("Comport defined");

        return localJposServiceInstance;
    }
}
