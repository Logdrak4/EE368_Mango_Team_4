package com.serotonin.mango.vo.report;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.serotonin.mango.view.export.CsvWriter;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.mango.Common;

public class ReportCsvStreamer implements ReportDataStreamHandler {
    private final PrintWriter out;
    public static ResourceBundle universal_bundle =Common.getBundle();;
    // Working fields
    private TextRenderer textRenderer;
    private final String[] data = new String[5];
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
    private final CsvWriter csvWriter = new CsvWriter();
    private String header = ""; // Initialize the header variable

    public ReportCsvStreamer(PrintWriter out, ResourceBundle bundle, Boolean writeHeader) {
        this.out = out;
       // universal_bundle = bundle;
        if(writeHeader){
            // Write the headers.
            data[0] = I18NUtils.getMessage(bundle, "reports.pointName");
            data[1] = I18NUtils.getMessage(bundle, "common.time");
            data[2] = I18NUtils.getMessage(bundle, "common.value");
            data[3] = I18NUtils.getMessage(bundle, "reports.rendered");
            data[4] = I18NUtils.getMessage(bundle, "common.annotation");

            out.write(csvWriter.encodeRow(data));
        }
        else{
            this.header += I18NUtils.getMessage(bundle, "reports.pointName") + "," +
                    I18NUtils.getMessage(bundle, "common.time") + "," +
                    I18NUtils.getMessage(bundle, "common.value") + "," +
                    I18NUtils.getMessage(bundle, "reports.rendered") + "," +
                    I18NUtils.getMessage(bundle, "common.annotation");
        }
    }

    public void startPoint(ReportPointInfo pointInfo) {
        data[0] = pointInfo.getExtendedName();
        textRenderer = pointInfo.getTextRenderer();
    }

    public void pointData(ReportDataValue rdv) {
        data[1] = dtf.print(new DateTime(rdv.getTime()));

        if (rdv.getValue() == null)
            data[2] = data[3] = null;
        else {
            data[2] = rdv.getValue().toString();
            data[3] = textRenderer.getText(rdv.getValue(), TextRenderer.HINT_FULL);
        }

        data[4] = rdv.getAnnotation();

        out.write(csvWriter.encodeRow(data));
    }

    // New method to handle the ArrayList of ReportDataValue objects
    public void writeReportDataValues(List<ReportDataValue> rdvs) {
        for (ReportDataValue rdv : rdvs) {
                // Write the headers.
//                data[0] = I18NUtils.getMessage(universal_bundle, "reports.pointName");
//                data[1] = I18NUtils.getMessage(universal_bundle, "common.time");
//                data[2] = I18NUtils.getMessage(universal_bundle, "common.value");
//                data[3] = I18NUtils.getMessage(universal_bundle, "reports.rendered");
//                data[4] = I18NUtils.getMessage(universal_bundle, "common.annotation");
//                out.write(csvWriter.encodeRow(data));
                pointData(rdv);

        }
    }

    // Modified done method to accept an ArrayList of ReportDataValue objects
    public void done(List<ReportDataValue> rdvs) {
        writeReportDataValues(rdvs);
        out.flush();
        out.close();
    }
}
