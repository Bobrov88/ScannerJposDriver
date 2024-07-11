package Logger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MyLogger {
    static public Logger createLoggerInstance(String className) {
        Logger logger = Logger.getLogger(className);
        PropertyConfigurator.configure("logging.properties");
        return logger;
    }
}
