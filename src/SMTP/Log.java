package SMTP;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * The Log class provides logging functionality by creating a file and writing log messages to it.
 */
public class Log {
    public Logger logger;
    FileHandler fh;

    /**
     * Constructor for the Log class that takes a filename as a parameter.
     * @param filename The name of the file to log to.
     * @throws IOException If an I/O error occurs while creating the file.
     */
    public Log(String filename) throws IOException {
        File f = new File(filename);

        if(f.exists()){
            f.createNewFile();
        }

        fh = new FileHandler(filename,true);
        logger = Logger.getLogger("test");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
