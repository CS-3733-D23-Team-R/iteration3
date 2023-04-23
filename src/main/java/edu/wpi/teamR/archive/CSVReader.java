package edu.wpi.teamR.archive;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CSVReader<T extends CSVReadable> {
    private final BufferedReader reader;
    private final Class<T> _class;

    /**
     * Creates CSVReader
     * @param path Path to CSV file to read
     * @param _class Class that you are reading (e.g. Node.class)
     * @throws IOException If file is not found
     */
    public CSVReader(String path, Class<T> _class) throws IOException {
        this._class = _class;
        reader = new BufferedReader(new FileReader(path));
        reader.readLine();
    }

    public ArrayList<T> parseCSV() throws CSVParameterException, IOException {
        ArrayList<T> data = new ArrayList<>();
        Constructor<T> c = null;
        try {
            c = _class.getDeclaredConstructor(String[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        c.setAccessible(true);
        String line;
        String[] args;
        try {
            while ((line = reader.readLine()) != null) {
                args = line.split(",");
                data.add(c.newInstance((Object) args));
            }
            c.setAccessible(false);
        } catch(IndexOutOfBoundsException e) {
            c.setAccessible(false);
            throw new CSVParameterException();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e ) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
