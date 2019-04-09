package lt.kurti;

import lt.kurti.cli.CLI;
import lt.kurti.rm.PhysicalMachine;

public class Main {

	public static void main(String[] args) {
		final PhysicalMachine physicalMachine = PhysicalMachine.getInstance();
		final CLI cli = new CLI();
		physicalMachine.run(cli);
	}

}
