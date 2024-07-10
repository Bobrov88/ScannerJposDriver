package test;

import jpos.config.JposEntry;
import jpos.config.simple.SimpleEntryRegistry;
import jpos.config.simple.xml.SimpleXmlRegPopulator;

import java.util.*;

public class Utility {

    private static Object[][] data;

    private static void getDeviceListFromXML() {
        SimpleEntryRegistry reg = null;
        try {
            reg = new SimpleEntryRegistry(new SimpleXmlRegPopulator());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
        reg.load();
        data = new Object[reg.getSize()][4];
        Enumeration<?> entriesEnum = reg.getEntries();
        int count = 0;
        for (; entriesEnum.hasMoreElements(); ++count) {
            JposEntry entry = (JposEntry) entriesEnum.nextElement();
            Object[] row = new Object[]{entry.getProp("deviceCategory").getValueAsString(),
                    entry.getProp("logicalName").getValueAsString(),
                    entry.getProp("vendorName").getValueAsString(),
                    entry.getProp("productName").getValueAsString()};
            data[count] = row;
        }
    }

    public static List<String> extractLogicalName() {
        getDeviceListFromXML();
        List<String> deviceList = new ArrayList<>();
        for (Object[] logicalName : data) {
            deviceList.add(logicalName[1].toString());
        }
        Collections.sort(deviceList);
        return deviceList;
    }
}
