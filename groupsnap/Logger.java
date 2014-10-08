package groupsnap;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {
	File logfile;
	String logname;
	public Logger(File logfile, String logname) {
		this.logfile = logfile;
		this.logname = logname;
	}

	public void log(String stringToLog) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfile.getName(), true)));
    		out.println(getCurrentTimeString() + "\t" + logname + "\t" + stringToLog);
    		out.close();
		} catch (IOException e) {}
	}

	public static String getCurrentTimeString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}