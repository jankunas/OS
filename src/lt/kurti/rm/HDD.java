package lt.kurti.rm;

import static lt.kurti.constants.Constants.HDD_NAME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class HDD {

	private static final int BLOCKS = 68;
	private static final int WORDS_PER_BLOCK = 16;
	private static final String EMPTY_SECTOR = "                ";
	public static ArrayList<Integer> usedSectors = new ArrayList<>();

	private static RandomAccessFile file;

	public HDD() throws FileNotFoundException {
		file = new RandomAccessFile(HDD_NAME, "rw");
		try {
			for (int i = 0; i < BLOCKS; ++i) {
				file.seek(i * WORDS_PER_BLOCK * 2);
				file.writeChars(EMPTY_SECTOR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("HDD init.");
	}

	public static void write(char[] data, int block) {
		if (block < 0 || block > BLOCKS) {
			throw new IllegalArgumentException("Incorrect block");
		}
		try {
			file.seek(block * WORDS_PER_BLOCK * 2);
			file.writeChars(new String(data));
			usedSectors.add(block);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static char[] read(int block) {
		if (block < 0 || block > BLOCKS) {
			throw new IllegalArgumentException("Incorrect block");
		}
		try {
			file.seek(block * WORDS_PER_BLOCK * 2);
			char[] data = new char[WORDS_PER_BLOCK];
			for (int i = 0; i < WORDS_PER_BLOCK; ++i) {
				data[i] = file.readChar();
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isEmpty(int sector) {
		if (sector < 0 || sector > BLOCKS) {
			throw new IllegalArgumentException("Incorrect sector");
		}
		return new String(read(sector)).equals(EMPTY_SECTOR);
	}

	public static void clear(int sector) {
		if (sector < 0 || sector > BLOCKS) {
			throw new IllegalArgumentException("Incorrect sector");
		}
		try {
			file.seek(sector * WORDS_PER_BLOCK * 2);
			file.writeChars(EMPTY_SECTOR);
			usedSectors.remove(sector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
