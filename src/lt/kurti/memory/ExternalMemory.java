package lt.kurti.memory;

import static lt.kurti.constants.Constants.END_OF_FILE_KW;
import static lt.kurti.constants.Constants.EXTERNAL_MEMORY_FILE_NAME;
import static lt.kurti.constants.Constants.START_OF_FILE_KW;
import static lt.kurti.rm.PhysicalMachine.supervisorMemory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ExternalMemory {

	private static boolean isReadSuccessful = false;

	public static void readToSupervisorMemory(final String programName, final int offX1, final int offX2) {
		try (FileReader fr = new FileReader(EXTERNAL_MEMORY_FILE_NAME);
			 BufferedReader br = new BufferedReader(fr)) {
			String line;

			while ((line = br.readLine()) != null) {
				if (isProgramNamePresent(line, programName)) {
					if (readProgramToSupervisorMemory(br, offX1, offX2)) {
						isReadSuccessful = true;
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!isReadSuccessful) {
			System.out.println("Reading from External Memory to Supervisor Memory failed.");
			System.exit(-1);
		}
		System.out.println("Reading from External Memory to Supervisor Memory finished.");
	}

	private static boolean readProgramToSupervisorMemory(final BufferedReader br, int offX1, int offX2) {
		while (true) {
			try {
				String line = br.readLine();
				if (line == null) {
					return false;
				} else if (END_OF_FILE_KW.equals(line)) {
					break;
				}
				supervisorMemory.writeBlock(line.toCharArray(), offX1, offX2);
				offX2++;
				if (offX2 % 16 == 0) {
					offX2++;
				}
				offX1++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return offX2 != 0;
	}

	private static boolean isProgramNamePresent(final String line, final String programName) {
		if (line.contains(START_OF_FILE_KW)) {
			final String nameInExternalMemory = line.substring(START_OF_FILE_KW.length() + 1);
			return nameInExternalMemory.equals(programName);
		}
		return false;
	}
}
