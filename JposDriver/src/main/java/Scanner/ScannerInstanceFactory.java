package Scanner;

import Logger.MyLogger;
import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.config.JposEntry.Prop;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;
import org.apache.logging.log4j.Logger;
import jpos.Scanner;

public final class ScannerInstanceFactory implements JposServiceInstanceFactory {

    private Logger logger = MyLogger.createLoggerInstance(ScannerInstanceFactory.class.getName());

    public ScannerInstanceFactory() {
        logger.info("ScannerInstanceFactory constructor called");
    }

    public JposServiceInstance createInstance(String paramString, JposEntry paramJposEntry) throws JposException {

        logger.debug("Creating instance of scanner");
//         JposServiceInstance localJposServiceInstance = null;
//        try {
//            Class<?> localClass = Class.forName("java.scanner.ScannerService");
//            localJposServiceInstance = (JposServiceInstance) localClass.getDeclaredConstructor().newInstance();
//            //localJposServiceInstance = (JposServiceInstance)localClass.newInstance();
//            return localJposServiceInstance;
//        } catch (ClassNotFoundException ex) {
//            logger.fatal("ScannerService does not exist!");
//            throw new JposException(104, "ScannerService does not exist!", ex);
//        } catch (InstantiationException ex) {
//            logger.fatal("ScannerService could not be instantiated!");
//            throw new JposException(104, "ScannerService could not be instantiated!", ex);
//        } catch (IllegalAccessException ex) {
//            logger.fatal("ScannerService creation failed!");
//            throw new JposException(104, "ScannerService creation failed!", ex);
//        } catch (NoSuchMethodException ex) {
//            logger.fatal("NoSuchMethodException thrown");
//            throw new JposException(104, "NoSuchMethodException thrown", ex);
//        } catch (InvocationTargetException ex) {
//            logger.fatal("ScannerService constructor threw an exception!");
//            throw new JposException(104, "ScannerService constructor threw an exception!", ex);
//        }

        if (!(paramJposEntry.hasPropertyWithName("serviceClass")))
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'serviceClass' property");
        if (!(paramJposEntry.hasPropertyWithName("portName")))
            throw new JposException(JposConst.JPOS_E_NOSERVICE,
                    "The JposEntry does not contain the 'commPortNumber' property");
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
        logger.debug("Connection to port: OK");

        return localJposServiceInstance;
    }
}
