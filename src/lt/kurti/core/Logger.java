package lt.kurti.core;


import static lt.kurti.constants.Constants.LOG_FILE_DOES_NOT_EXIST;
import static lt.kurti.constants.Constants.LOG_FILE_NAME;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static File logFile = new File(LOG_FILE_NAME);

	static {
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(String message) {
		if (!logFile.exists()) {
			throw new IllegalArgumentException(LOG_FILE_DOES_NOT_EXIST);
		} else {
			System.out.println(message);
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
				bw.write(message + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
