package org.acme.task;

import io.agroal.api.AgroalDataSource;
import org.acme.CSVExpoter;
import org.acme.global.Globals;
import org.jboss.logmanager.Level;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Task implements Runnable {
    AgroalDataSource dataSource;

    @Inject
    Globals globals;

    private static Logger LOGGER = Logger.getLogger(Task.class.getName());

    private int rowStart = 0;
    private int rowEnd = 5000;
    private String csvFileName;

    public Task(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public synchronized void run() {
        String beginDate = "2022-01-23";
        String endDate = "2022-01-24";
        try {
            CSVExpoter csvExpoter = new CSVExpoter();//id, orderType, rowStart, rowEnd, beginDate, endDate);
            csvFileName = csvExpoter.getFileName("_Export", beginDate, endDate);
            int rowcnt = 0;
            List<Map<String, Object>> listData = csvExpoter.getData(dataSource, rowStart, rowEnd, beginDate, endDate);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(csvFileName));

            int colcnt = (listData.size() > 0) ? listData.get(0).size() : 0;
            csvExpoter.writeHeaderLine(listData.get(0), fileWriter);

            int csvCnt = csvExpoter.writeCSV(listData, fileWriter, colcnt);
            rowcnt = csvCnt;

            int totalCount = csvExpoter.getTotalCount();
            LOGGER.info("total count:" + totalCount + " start:" + rowStart + " size:" + rowEnd);

            int step = rowEnd - rowStart;
            while (rowcnt < totalCount) {
                rowStart = rowEnd + 1;
                listData = csvExpoter.getData(dataSource, rowStart, rowEnd += step, beginDate, endDate);
                csvCnt = csvExpoter.writeCSV(listData, fileWriter, colcnt);
                rowcnt += csvCnt;
                LOGGER.info("total count:" + totalCount + " start:" + rowStart + " size:" + rowEnd + " rowcnt:" + rowcnt);
                globals.setFileStatus(csvFileName ,"ingoing");
            }

            fileWriter.close();
            globals.setFileStatus(csvFileName, "done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.DEBUG, "task complete " + rowStart + " - " + rowEnd);
    }
}
