package edu.wpi.teamR.archive;

import java.io.*;
import java.util.List;

public class CSVWriter {

    public void writeCSV(String filename, List<? extends Archivable> data) throws IOException {
        OutputStream out = new FileOutputStream(filename);
        writeCSV(out, data);
        out.close();
    }

    public void writeCSV(OutputStream out, List<? extends Archivable> data) throws IOException {
        if (data.size() == 0)
            return;
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));
        writer.write(data.get(0).getCSVColumns());
        writer.write("\n");
        for (Archivable d : data) {
            writer.write(d.toCSVEntry());
            writer.write("\n");
        }
        writer.flush();
    }
}
