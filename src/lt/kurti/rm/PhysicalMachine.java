package lt.kurti.rm;

import static lt.kurti.constants.Constants.SHUTDOWN_COMMAND;

import java.io.FileNotFoundException;
import java.io.IOException;

import lt.kurti.cli.CLI;
import lt.kurti.vm.VirtualMachine;

public class PhysicalMachine {

	private static PhysicalMachine instance = null;

	//List of Registers
	public static int R1;
	public static int R2;
	public static int PTR;

	public static short IC;

	public static byte CH1;
	public static byte CH2;
	public static byte CH3;

	public static byte PI;
	public static byte SI;
	public static byte TI;
	public static byte IOI;

	public static byte MODE;
	//First bit - ZF, 2nd - SF, 3rd - OF
	public static byte C;

	public static Memory memory;
	public static SupervisorMemory supervisorMemory;
	public static HDD hdd;

	static {
		try {
			hdd = new HDD();
			supervisorMemory = new SupervisorMemory();
			memory = new Memory();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private PhysicalMachine() {
		System.out.println("RM initialized");
	}

	public static PhysicalMachine getInstance() {
		if (instance == null) {
			instance = new PhysicalMachine();
		}
		return instance;
	}

	public void run(final CLI cli) {
		final VirtualMachine virtualMachine = new VirtualMachine();
		updateMode(cli.getUserInputForMode());

		while (true) {
			final String userInput = cli.getUserInput();
			if (SHUTDOWN_COMMAND.equals(userInput)) {
				break;
			} else if (userInput != null) {
				readFromExternalMemory(userInput);
				PD("0");
				moveMemory("0");
				virtualMachine.processCommands();
				break;
			}
		}
	}

	private void updateMode(final int mode) {
		if (mode == 0) {
			setMODE((byte) 0);
		} else if (mode == 1) {
			setMODE((byte) 1);
		} else {
			System.out.println("EXIT. BAD INPUT");
			System.exit(-1);
		}
	}

	public static void setZF() {
		C |= (1 << 6);
	}

	public static void clearZF() {
		C &= ~(1 << 6);
	}

	public static int getZF() {
		return (C >> 6) & 1;
	}

	public static void setSF() {
		C |= (1 << 5);
	}

	public static void clearSF() {
		C &= ~(1 << 5);
	}

	public static int getSF() {
		return (C >> 5) & 1;
	}

	public static void setOF() {
		C |= (1 << 4);
	}

	public static void clearOF() {
		C &= ~(1 << 4);
	}

	public static int getOF() {
		return (C >> 4) & 1;
	}

	public static int getR1() {
		return R1;
	}

	public static void setR1(int r1) {
		R1 = r1;
	}

	public static int getR2() {
		return R2;
	}

	public static void setR2(int r2) {
		R2 = r2;
	}

	public static int getPTR() {
		return PTR;
	}

	public static void setPTR(int ptr) {
		PTR = ptr;
	}

	public static short getIC() {
		return IC;
	}

	public static void setIC(short counter) {
		IC = counter;
	}

	public static byte getCH1() {
		return CH1;
	}

	public static void setCH1(byte state) {
		channelHelper(state, "CH1:0. FlashMemory channel freed",
				"CH1:1. FlashMemory channel busy - transferring data");
		if (state == 0 || state == 1)
			CH1 = state;
	}

	public static byte getCH2() {
		return CH2;
	}

	public static void setCH2(byte state) {
		channelHelper(state, "CH2:0. Printer channel freed",
				"CH2:1. Printer channel busy - transferring data");
		if (state == 0 || state == 1)
			CH2 = state;
	}

	public static byte getCH3() {
		return CH3;
	}

	public static void setCH3(byte state) {
		channelHelper(state, "CH3:0. Supervisor-HDD channel freed",
				"CH3:1. Supervisor-HDD channel busy - transferring data");
		if (state == 0 || state == 1)
			CH3 = state;
	}

	private static void channelHelper(byte state, String freedInterrupt, String busyInterrupt) {
		switch (state) {
			case 0:
				System.out.println(freedInterrupt);
				break;
			case 1:
				System.out.println(busyInterrupt);
				break;
			default:
				break;
		}
	}


	public static byte getPI() {
		return PI;
	}

	public static void setPI(byte state) {
		switch (state) {
			case 1:
				System.out.println("PI:1. OUT OF MEMORY RANGE Interrupt");
				break;
			case 2:
				System.out.println("PI:2. UNKNOWN OPERATION CODE Interrupt");
				break;
			case 3:
				System.out.println("PI:3. DIVISION BY ZERO Interrupt");
				break;
			case 4:
				System.out.println("PI:4. ARITHMETIC OPERATION ERROR Interrupt");
				break;
			default:
				break;
		}
		if (state == 1 || state == 2 || state == 3 || state == 4)
			PI = state;
	}

	public static byte getSI() {
		return SI;
	}

	public static void setSI(byte state) {
		switch (state) {
			case 1:
				System.out.println("SI:1. READ Command Interupt");
				break;
			case 2:
				System.out.println("SI:2. WRITE Command Interupt");
				break;
			case 3:
				System.out.println("SI:3. HALT Command Interupt");
				break;
			default:
				break;
		}
		if (state == 1 || state == 2 || state == 3)
			SI = state;
	}

	public static byte getTI() {
		return TI;
	}

	public static void setTI(byte state) {
		TI = state;
	}

	public static byte getIOI() {
		return IOI;
	}

	public static void setIOI(byte state) {
		switch (state) {
			case 1:
				System.out.println("IOI:1. CH1 Input/Output interrupt");
				break;
			case 2:
				System.out.println("IOI:2. CH2 Input/Output interrupt");
				break;
			case 3:
				System.out.println("IOI:3. CH3 Input/Output interrupt");
				break;
			default:
				break;
		}
		if (state == 1 || state == 2 || state == 3)
			IOI = state;
	}

	public static byte getMODE() {
		return MODE;
	}

	public void setMODE(byte mode) {
		switch (mode) {
			case 0:
				System.out.println("Entering user mode");
				break;
			case 1:
				System.out.println("Entering supervisor mode");
				break;
			default:
				break;
		}
		if (mode == 0 || mode == 1)
			MODE = mode;
	}

	public static Memory getMemory() {
		return memory;
	}

	//reads from external memory to HDD
	public void readFromExternalMemory(final String externalFileName) {
		setCH1((byte) 1);
		ExternalMemory.readToHDD(externalFileName);
		setCH1((byte) 0);
		if (getMODE() == 0) {
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeToPrinter(Object o) {
		setCH2((byte) 1);
		Printer.print(o);
		setCH2((byte) 0);
	}

	//HALT - programos sustojimo taško komanda, t.y. programos valdymo pabaiga.
	public static void HALT() throws Exception {
		setSI((byte) 3);
		//throw new Exception("PROGRAMOS PABAIGA");
		setSI((byte) 0);
	}

	//PDx - SI tampa 1 ir valdymas perduodamas OS, duomenų kopijavimui iš kietojo disko į supervizorinės atminties vietą x.
	public static void PD(String address) {
		setSI((byte) 1);
		setCH3((byte) 1);

		int block = Integer.parseInt(address);
		for (int i = 0; i < HDD.usedSectors.size(); ++i) {
			supervisorMemory.writeBlock(HDD.read(HDD.usedSectors.get(i)), block);
			block++;
		}
		setCH3((byte) 0);
		setSI((byte) 0);
		System.out.println("Data was loaded from HDD to supervisor memory");
		if (getMODE() == 0) {
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Perkelia duomenis is supervizorines atminties i pagrindine
	public static void moveMemory(String address) {
		System.out.println("move memory from supervisor to main memory");
		boolean dataSeg = false;
		boolean codeSeg = false;

		int codeOffset = 64;
		int currCodePos = 0;

		int commandOffset = 0;
		int currCommandPos = 0;

		for (Integer bID : supervisorMemory.usedBlocks) {
			char[] block = supervisorMemory.getBlock(bID);
			//Splitting every 4 'bytes'
			String[] blockString = new String(block).split("(?<=\\G....)");
			for (String s : blockString) {
				if (s.equals("DATA") && !dataSeg) {
					dataSeg = true;
				}
				if (s.equals("CODE")) {
					codeSeg = true;
				}
				if (!codeSeg && !s.equals("DATA")) {
					memory.writeBlockOffset(s.toCharArray(), codeOffset, currCodePos);
					currCodePos += 4;
					codeOffset += 4;
					if (currCodePos == 16) {
						currCodePos = 0;
					}
				}
				if (codeSeg && !s.equals("CODE")) {
					memory.writeBlockOffset(s.toCharArray(), commandOffset, currCommandPos);
					currCommandPos += 4;
					commandOffset += 4;
					if (currCommandPos == 16) {
						currCommandPos = 0;
					}
				}
			}
		}
	}

	//GDx - SI tampa 2 ir valdymas perduodamas OS, duomenų kopijavimui į kietąjį diską iš supervizorinės atminties vietos x.
	public static void GD(String address) {
		setSI((byte) 2);
		setCH1((byte) 1);
		//paimt is atminties address
		//ir irasyt i hdd
		setCH1((byte) 0);
		setSI((byte) 0);
	}

	//    @Override
	public static String getInfo() {
		return "+------------------+" + '\n' +
				"|       RM         |" + '\n' +
				"+------------------+" + '\n' +
				"R1: " + R1 + '\n' +
				"R2: " + R2 + '\n' +
				"CH1: " + getCH1() + '\n' +
				"CH2: " + getCH2() + '\n' +
				"CH3: " + getCH3() + '\n' +
				"PI: " + getPI() + '\n' +
				"SI: " + getSI() + '\n' +
				"TI: " + getTI() + '\n' +
				"IOI: " + getIOI() + '\n' +
				"IC: " + getIC() + '\n' +
				"MODE: " + getMODE() + '\n' +
				"ZF: " + getZF() + '\n' +
				"SF: " + getSF() + '\n' +
				"OF: " + getOF() + '\n' +
				"+------------------+";

	}



}
