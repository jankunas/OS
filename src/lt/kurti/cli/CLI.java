package lt.kurti.cli;

import static lt.kurti.constants.Constants.SHUTDOWN_COMMAND;
import static lt.kurti.constants.Constants.START_COMMAND;

import java.util.Scanner;

import lt.kurti.rm.PhysicalMachine;


public class CLI {

	final Scanner keyboard;

	public CLI() {
		this.keyboard = new Scanner(System.in);
	}

	public int getUserInputForMode() {
		System.out.println("Enter MODE:");
		System.out.println("0. User mode");
		System.out.println("1. Supervisor mode");
		PhysicalMachine.setCH1((byte) 1);
		final int userInput = Integer.parseInt(keyboard.nextLine());
		PhysicalMachine.setCH1((byte) 0);
		return userInput;
	}

	public String getUserInput() {
		System.out.println("==================================");
		System.out.println("Enter external memory's file name:");
		System.out.println("START <FILE_NAME>");
		System.out.println("OR shutdown the OS:");
		System.out.println("SHUTDOWN");
		PhysicalMachine.setCH1((byte) 1);
		final String userInput = keyboard.nextLine();
		PhysicalMachine.setCH1((byte) 0);

		if (userInput.contains(START_COMMAND)) {
			return userInput.substring(START_COMMAND.length() + 1);
		} else if (userInput.equals(SHUTDOWN_COMMAND)) {
			System.out.println("Shutting down OS");
			return userInput;
		}
		System.out.println("Invalid input.");
		return null;
	}

	public String readFromInput(){
		return keyboard.nextLine();
	}
}
