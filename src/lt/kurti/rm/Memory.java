package lt.kurti.rm;

import java.util.Arrays;

public class Memory {

	private final int BLOCK_COUNT = 48;
	protected final int BLOCK_SIZE = 16;

    /* --------------------------------------
        0 1  2  3  4  5  6 7 8 9 A B C D E F|
        -----------Programos komandos--------
       0            0-16                    |
       1            16-32                   |
       2            32-48                   |
       3            48-64                   |
       -----------Programos duomenys---------
       4            64-80                   |
       5            80-96                   |
       6            96-112                  |
       7            112-128                 |
       8            128-144                 |
       9            144-160                 |
       A            160-176                 |
       ----------Puslapiavimo lentele?------|
       B            176-192                 |
       C            192-208                 |
       D            208-224                 |
       E            224-240                 |
       F            240-256                 |
       --------------------------------------

     */

	protected Word[][] memory = new Word[BLOCK_COUNT][BLOCK_SIZE];
	public int usedCODEBlocks = 0;
	public int usedDATABlocks = 0;
	protected int offset = 0;

	public Memory() {
	}

	public Word getWord(int x1, int x2) {
		return memory[x1 / 16][x2];
	}

	public void writeBlock(char[] data, int x1, int x2) {
		memory[x1 / 16][x2] = new Word(data);
		offset++;
	}

//	public void writeBlockOffset(char[] data, int offset1, int offset2) {
//		int blockID = offset1 / 16;
//		for (int i = 0; i < data.length; ++i) {
//			memory[blockID][offset2] = data[i];
//			offset2++;
//		}
//	}

	public void display() {
		for (int x = 0; x < BLOCK_COUNT; ++x) {
			for (int y = 0; y < BLOCK_SIZE; ++y) {
				System.out.print(memory[x][y] + "|");
			}
			System.out.println();
		}
	}

	@Override
	public String toString() {
		return "Memory{" +
				"BLOCK_COUNT=" + BLOCK_COUNT +
				", BLOCK_SIZE=" + BLOCK_SIZE +
				", memory=" + Arrays.toString(memory);
	}
}
