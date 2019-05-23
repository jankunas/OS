package lt.kurti.rm;

public class SharedMemory extends Memory {

	public final int BLOCK_COUNT = 16;

	public SharedMemory() {
		this.memory = new Word[BLOCK_COUNT][BLOCK_SIZE];
	}
}
