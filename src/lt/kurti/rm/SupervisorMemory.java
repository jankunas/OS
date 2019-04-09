package lt.kurti.rm;

public class SupervisorMemory extends Memory {

	private final int BLOCK_COUNT = 16;

	public SupervisorMemory(){
		this.memory = new char[BLOCK_COUNT][BLOCK_SIZE];
	}
}
