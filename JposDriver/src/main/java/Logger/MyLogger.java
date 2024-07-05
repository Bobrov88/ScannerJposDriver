package Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
    private static Logger logger = null;
    static public Logger createLoggerInstance(String className) {
        logger = LogManager.getLogger(className);
        System.setProperty("log4j.configurationFile", "file:./logging.properties");
        return logger;
    }

}
