package lt.kurti.vm;

import static lt.kurti.rm.PhysicalMachine.R1;
import static lt.kurti.rm.PhysicalMachine.R2;
import static lt.kurti.rm.PhysicalMachine.AX;
import static lt.kurti.rm.PhysicalMachine.setSI;
import static lt.kurti.rm.PhysicalMachine.test;

import lt.kurti.rm.Memory;
import lt.kurti.rm.PhysicalMachine;
import lt.kurti.rm.Printer;

public class VirtualMachine {

	private byte SF, IOI;
	private short IC;
	private byte C;

	private Memory memory;

	public VirtualMachine() {
		this.memory = PhysicalMachine.getMemory();
		System.out.println("VM init.");
	}

	public void processCommands() {

		int offX1 = 0, offX2 = 0;
		for (int cmdBlock = 0; cmdBlock < memory.usedCODEBlocks; ++cmdBlock) {
			String word = new String(memory.getWord(offX1, offX2).word);
			if (word.contains("_")) {
				word = word.replace("_", "");
			}
			try {
				if (PhysicalMachine.getMODE() == 0) {
					System.out.println(PhysicalMachine.getInfo());
					System.in.read();
				}
				resolveCommand(word);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			offX1++;
			offX2++;
			if(offX2 == 16){
				offX2 = 0;
			}
		}
	}


	public void resolveCommand(String line) throws Exception {
		System.out.println("Resolve command: " + line);
		if (line.equals("HALT")) {
			setSI((byte) 3);
			test();
		} else if (line.substring(0, 3).equals("ADD")) {
			ADD();
		} else if (line.substring(0, 3).equals("SUB")) {
			SUB();
		} else if (line.substring(0, 3).equals("MUL")) {
			MUL();
		} else if (line.substring(0, 3).equals("DIV")) {
			DIV();
		} else if (line.substring(0, 3).equals("CMP")) {
			CMP();
		} else if (line.substring(0, 2).equals("LW")) {
			String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			LW(x1, x2/4);
		}
		else if (line.substring(0, 2).equals("LD")){
			LD(Integer.parseInt(line.substring(3, 4)));
		}

		else if (line.substring(0, 2).equals("LE"))
		{
			String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			LE(x1,x2/4);
		}
		else if (line.substring(0, 2).equals("PM")) {
			PM(Integer.parseInt(line.substring(2, 4), 16) + 64);
		}
        else if (line.substring(0, 2).equals("LS")) {
            LS(Integer.parseInt(line.substring(3, 4)));
        }
        else if (line.substring(0, 2).equals("LX")) {
            String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			LX(x1, x2/4);
        }
         else if (line.substring(0, 2).equals("LY")) {
            String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			LY(x1, x2/4);
        }
        else if (line.substring(0, 2).equals("LL")) {
            LL(Integer.parseInt(line.substring(3, 4)));
        }
		else if (line.substring(0, 2).equals("LR")) {
			LR(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JM")) {
			JM(line.substring(3, 4));
		} else if (line.substring(0, 2).equals("JE")) {
			JE(line.substring(3, 4));
		} else if (line.substring(0, 2).equals("JG")) {
			JG(line.substring(3, 4));
		} else if (line.substring(0, 2).equals("JL")) {
			JL(line.substring(3, 4));
		} else if (line.substring(0, 2).equals("JN")) {
			JN(line.substring(3, 4));
		} else if (line.substring(0, 2).equals("IC")) {
			IC(line.substring(3, 4));
		}
        /*else if (line.substring(0, 2).equals("PD")) {
            PD(line.subsring(3, 4));
        }
         else if (line.substring(0, 2).equals("GD")) {
            GD(line.substring(3, 4));
        }
        */
		else {
			// 2 - neatpažintas operacijos kodas
			PhysicalMachine.setPI((byte) 2);
			throw new Exception("PAKEIST I TINKAMA. NEATPAZINTA KOMANDA");
		}
	}

	// Sudeda R1 ir R2, įrašoma į R1. Jeigu rezultatas netelpa, OF = 1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void ADD() {
		if (PhysicalMachine.R1 + PhysicalMachine.R2 > Integer.MAX_VALUE) {
			setOF();
			return;
		} else {
			PhysicalMachine.R1 += PhysicalMachine.R2;
		}
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Iš R1 atimama R2, įrašoma į R1. Jeigu rezultatas netelpa, OF = 1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void SUB() {
		if (PhysicalMachine.R1 - PhysicalMachine.R2 < Integer.MIN_VALUE) {
			setOF();
			return;
		} else {
			PhysicalMachine.R1 -= PhysicalMachine.R2;
		}
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Sudaugina R1 ir R2, įrašoma į R1.Jeigu rezultatas netelpa, OF = 1.Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void MUL() {
		if (PhysicalMachine.R1 * PhysicalMachine.R2 > Integer.MAX_VALUE) {
			setOF();
			return;
		} else {
			PhysicalMachine.R1 *= PhysicalMachine.R2;
		}
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Padalina R1 iš R2, įrašoma į R1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void DIV() {
		PhysicalMachine.R1 /= PhysicalMachine.R2;
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	public void AND() {
		PhysicalMachine.R1 = PhysicalMachine.R1 & PhysicalMachine.R2;
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	public void OR() {
		PhysicalMachine.R1 = PhysicalMachine.R1 | PhysicalMachine.R2;
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	public void XOR() {
		PhysicalMachine.R1 = PhysicalMachine.R1 ^ PhysicalMachine.R2;
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	public void NOT() {
		PhysicalMachine.R1 = ~PhysicalMachine.R1;
		if (((PhysicalMachine.R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	//Ši komanda palygina registre R1 ir R2 ęsančias reikšmes. Jeigu reikšmės lygios, ZF = 1, priešingu atveju ZF = 0.
	public void CMP() {
		if (PhysicalMachine.R1 == PhysicalMachine.R2) {
			setZF();
		} else if(PhysicalMachine.R1 > PhysicalMachine.R2){
			clearZF();
			clearSF();
		}else if(PhysicalMachine.R1 < PhysicalMachine.R2){
			clearZF();
			setSF();
		}else{
			clearZF();
		}
		++IC;
	}

	//LWx1x2 - į registrą R1 užkrauna žodį nurodytu adresu 16 * x1 + x2.
	public void LW(int x1, int x2) {
        /*int block = address / 16;
        int offset = (address - 64) % 16; */
		char[] word = new char[4];
		// int j = 0;
		word = memory.getWord(x1, x2).word;
		PhysicalMachine.R1 = Integer.parseInt(new String(word));
		++IC;
	}

	//LEx1x2 - į registrą R2 užkrauna skaičių, adresu 16 * x1 + x2.
	public void LE(int x1, int x2) {
        /*int block = address / 16;
        int offset = (address - 64) % 16; */
		char[] word = new char[4];
		// int j = 0;
		word = memory.getWord(x1, x2).word;
		PhysicalMachine.R2 = Integer.parseInt(new String(word));
		++IC;
	}
	//LDx - į R1 įrašo reikšmę x
	public void LD(int value){
		PhysicalMahine.R1 = value;
		++IC;
	}

	//LSx1x2 - į atmintį adresu 16 * x1 + x2 rašo žodį ar skaičių.
	//Duomenu ivedimui is failo naudosim
	public void LS(int address) {
		int block = address / 16;
		int offset = (address - 64) % 16;
		PhysicalMachine.setSI((byte) 1);
		test();
		
		char [] data = new char [4];
		data = PhysicalMachine.sharedMemory.getWord(block, offset).word;
		memory.writeBlock(data,block,offset);
		++IC;
	}


	//LXx1x2 - į R1 užkrauna bendrosios atminties srities adreso 16*x1 + x2 reikšmę.
	public void LX(int x1, int x2) {
		char[] word = new char[4];
		word = PhysicalMachine.sharedMemory.getWord(x1, x2).word;
		PhysicalMachine.R1 = Integer.parseInt(new String(word));
		++IC;
	}

	//LYx1x2 - į R2 užkrauna bendrosios atminties srities adreso 16*x1 + x2 reikšmę.
	public void LY(int x1, int x2) {
		char[] word = new char[4];
		word = PhysicalMachine.sharedMemory.getWord(x1, x2).word;
		PhysicalMachine.R2 = Integer.parseInt(new String(word));
		++IC;
	}

	//LLx1x2 - į bendrosios atminties sritį adresu 16 * x1 + x2 rašo žodį ar skaičių.
	public void LL(int address) {
		int block = address / 16;
		int offset = (address - 64) % 16;
		PhysicalMachine.setSI((byte) 1);
		test();
		
		char [] data = new char [4];
		data = PhysicalMachine.sharedMemory.getWord(block, offset).word;
		PhysicalMachine.sharedMemory.writeBlock(data,block,offset);
		++IC;
	}


	//LRXX- išveda į printerį XX registrą (R1 arba R2)
	public void LR(String register) {
		PhysicalMachine.setSI((byte) 2);
		if (register.equals("R1")) {
			PhysicalMachine.sharedMemory.writeBlock(String.valueOf(PhysicalMachine.getR1()).toCharArray(), 0, 0);
		}
		if (register.equals("R2")) {
			PhysicalMachine.sharedMemory.writeBlock(String.valueOf(PhysicalMachine.getR2()).toCharArray(), 0, 0);
		}
		test();
		++IC;
	}

	//Isveda i ekrana atminties 4 baitus
	public void PM(int address) {

		int block = address / 16;
		int offset = (address - 64) % 16;
		PhysicalMachine.setSI((byte) 2);
		PhysicalMachine.sharedMemory.writeBlock(String.valueOf(memory.getWord(block, offset)).toCharArray(), 0, 0);
		test();
		++IC;
		// char[] word = new char[4];
		// int j = 0;
		// for (int i = offset; i < offset + 4; ++i) {
		// 	//word[j] = memory.getBlock(block)[i];
		// 	j++;
		// }
		
	}

	//JMx1x2 - besąlyginio valdymo perdavimo komanda. Ji reiškia, kad valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16 * x1 + x2
	public void JM(String address) {
		/*int block = address / 16;
		int offset = (address - 64) % 16;
		offX1 = block;
		offX2 = offset;*/
		IC(address);
		//Word goTo = new Word(address);
		//IC = Integer.parseInt(new String(goTo));
	}

	//JEx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 1
	public void JE(String address) {
		if (getZF() == 1) {
			//processCommands() tik paduot parametra, nuo kurios vietos vykdyt koda
			IC(address);
		}
		else
			++IC;
	}

	//JGx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 0 IR SF = OF
	public void JG(String address) {
		if (getZF() == 0 && getSF() == getOF()) {
			IC(address);
		}
		else
			++IC;
	}

	//JLx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu SF != OF
	public void JL(String address) {
		if (getSF() != getOF()) {
			Integer.parseInt(address, 16);
			IC(address);
		}
			++IC;
	}

	public void JN(String address) {
		if (getZF() == 0) {
			IC(address);
		}
		else
			++IC;
	}
	/// /IC - komandos skaitliukas. IC = 16 * x1 + x2;
	//Kam jis ir kuo skiriasi nuo JM? Kam ja pridejau isvis? :D
	public void IC(String address) {
		IC = Short.parseShort(address, 16);
	}
/*
	public void PD(String address) {
		PhysicalMachine.setSI((byte)1);
		PhysicalMachine.supervisorMemory.writeBlock
	}
*/
	public void setZF() {
		C |= (1 << 6);
	}

	public void clearZF() {
		C &= ~(1 << 6);
	}

	public int getZF() {
		return (C >> 6) & 1;
	}

	public void setSF() {
		C |= (1 << 5);
	}

	public void clearSF() {
		C &= ~(1 << 5);
	}

	public int getSF() {
		return (C >> 5) & 1;
	}

	public void setOF() {
		C |= (1 << 4);
	}

	public void clearOF() {
		C &= ~(1 << 4);
	}

	public int getOF() {
		return (C >> 4) & 1;
	}

	public short getIC() {
		return IC;
	}

	public void setIC(short IC) {
		this.IC = IC;
	}

	@Override
	public String toString() {
		return  "+------------------+" + '\n' +
				"|       VM         |" + '\n' +
				"+------------------+" + '\n' +
				"| IC: " + getIC() + '\n' +
				"| ZF: " + getZF() + '\n' +
				"| SF: " + getSF() + '\n' +
				"| OF: " + getOF() + '\n' +
				"+-----------------+";
	}

}
