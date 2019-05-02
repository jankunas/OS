package lt.kurti.vm;

import static lt.kurti.rm.PhysicalMachine.R1;
import static lt.kurti.rm.PhysicalMachine.R2;

import lt.kurti.rm.Memory;
import lt.kurti.rm.PhysicalMachine;
import lt.kurti.rm.Printer;

public class VirtualMachine {

	private byte SF, IOI;
	private short IC;

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
			if(word.equals("HALT")){
				System.out.println("HALT found. HALTING...");
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
			PhysicalMachine.HALT();
		} else if (line.substring(0, 3).equals("ADD")) {
			ADD();
		} else if (line.substring(0, 3).equals("SUB")) {
			SUB();
		} else if (line.substring(0, 2).equals("AD")) {
			AD(Integer.parseInt(line.substring(2, 4)));
		} else if (line.substring(0, 2).equals("SB")) {
			//SB(Integer.parseInt(line.substring(2, 4)));
		} else if (line.substring(0, 4).equals("COMP")) {
			CMP();
		} else if (line.substring(0, 2).equals("LX")) {
//			LX(Integer.parseInt(line.substring(2,4)));
		} else if (line.substring(0, 2).equals("LW")) {
			String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			LW(x1, x2/4);
		} else if (line.substring(0, 2).equals("LR")) {
			LR(line.substring(2, 3));
		} else if (line.substring(0, 2).equals("PM")) {
			String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			SX(x1, x2);
		} else if (line.substring(0, 2).equals("LR")) {
			LR(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JP")) {
			JP(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JE")) {
			JE(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JG")) {
			JG(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JL")) {
			JL(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JN")) {
			//JN(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JC")) {
			//JC(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JO")) {
			//JO(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("LP")) {
			//LP(Integer.parseInt(line.substring(2, 4)));
		} else if (line.substring(0, 3).equals("AND")) {
			//AND();
		} else if (line.substring(0, 2).equals("OR")) {
			//OR();
		} else if (line.substring(0, 2).equals("NR")) {
			//NOT(line.substring(2,3));
		} else if (line.substring(0, 3).equals("XOR")) {
			//XOR();
		} else if (line.substring(0, 1).equals("I")) {
			INTER(Integer.parseInt(line.substring(1, 4)));
		} else {
			// 2 - neatpažintas operacijos kodas
			PhysicalMachine.setPI((byte) 2);
			throw new Exception("PAKEIST I TINKAMA. NEATPAZINTA KOMANDA");
		}
	}

	// Sudeda R1 ir R2, įrašoma į R1. Jeigu rezultatas netelpa, OF = 1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void ADD() {
		if (R1 + R2 > Integer.MAX_VALUE) {
			setOF();
			return;
		} else {
			R1 += R2;
		}
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Iš R1 atimama R2, įrašoma į R1. Jeigu rezultatas netelpa, OF = 1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void SUB() {
		if (R1 - R2 < Integer.MIN_VALUE) {
			setOF();
			return;
		} else {
			R1 -= R2;
		}
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Sudaugina R1 ir R2, įrašoma į R1.Jeigu rezultatas netelpa, OF = 1.Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void AD(int xy) {
		if (R1 + R2 > Integer.MAX_VALUE) {
			setOF();
			return;
		} else {
			R1 += R2;
		}
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Padalina R1 iš R2, įrašoma į R1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void SB() {
		R1 /= R2;
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	//Ši komanda palygina registre R1 ir R2 ęsančias reikšmes. Jeigu reikšmės lygios, ZF = 1, priešingu atveju ZF = 0.
	public void CMP() {
		if (R1 == R2) {
			setZF();
		} else {
			clearZF();
		}
		++IC;
	}

	//LWx1x2 - į registrą R1 užkrauna žodį nurodytu adresu 16 * x1 + x2.
	public void LW(int x1, int x2) {
		char[] word = memory.getWord(x1, x2).word;
		R1 = Integer.parseInt(new String(word));
		++IC;
	}

	//LEx1x2 - į registrą R2 užkrauna skaičių, adresu 16 * x1 + x2.
	public void LE(int x1, int x2) {
		char[] word = memory.getWord(x1, x2).word;
		R2 = Integer.parseInt(new String(word));
		++IC;
	}

	//LSx1x2 - į atmintį adresu 16 * x1 + x2 rašo žodį ar skaičių.
	//Duomenu ivedimui is failo naudosim
	public void LS(String address) {
		++IC;
	}

   /*
    * Bendroji atminties sritis lygtais jau MOS dalis
    *
    //LXx1x2 - į R1 užkrauna bendrosios atminties srities adreso 16*x1 + x2 reikšmę.
    public void LX(String address) {
        ++IC;
    }

    //LYx1x2 - į R2 užkrauna bendrosios atminties srities adreso 16*x1 + x2 reikšmę.
    public void LY(String address) {
        ++IC;
    }

    //LLx1x2 - į bendrosios atminties sritį adresu 16 * x1 + x2 rašo žodį ar skaičių.
    public void LL(String address) {
        ++IC;
    }
    */

	//LRXX- išveda į printerį XX registrą (R1 arba R2)
	public void LR(String register) {
		R2 = R1;
		++IC;
	}

	//Isveda i ekrana atminties 4 baitus
	public void SX(final int x1, final int x2) {
		char[] word = memory.getWord(x1, x2).word;
		Printer.print(new String(word));
		++IC;
	}


	//LDx1x2 - nuskaito registrą R2
	//public void LD(String address) {
	//    ++IC;
	//}

	//JMx1x2 - besąlyginio valdymo perdavimo komanda. Ji reiškia, kad valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16 * x1
	// + x2
	public void JP(String address) {
		++IC;
	}

	//JEx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 1
	public void JE(String address) {
		if (getZF() == 1) {
			//processCommands() tik paduot parametra, nuo kurios vietos vykdyt koda
		}
		++IC;
	}

	//JGx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 0 IR SF = OF
	public void JG(String address) {
		if (getZF() == 0 && getSF() == getOF()) {

		}
		++IC;
	}

	//JLx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu SF != OF
	public void JL(String address) {
		if (getSF() != getOF()) {
			Integer.parseInt(address, 16);
		}
		++IC;
	}

	/// /IC - komandos skaitliukas. IC = 16 * x1 + x2;
	//Kam jis ir kuo skiriasi nuo JM? Kam ja pridejau isvis? :D
	public void IC(String address) {
		IC = Short.parseShort(address, 16);
	}

	public void INTER(int param) {
		int z = param / 100;
		int y = param % 10;
		param /= 10;
		int x = param % 10;

		if (z == 0) {
			char[] strInBytes = (PhysicalMachine.readFromInput(1)).toCharArray();
//			memory.writeBlock(strInBytes, memory.getBlock(x * 16)[y]);
		} else if (z == 1) {
			char[] strInBytes = (PhysicalMachine.readFromInput(y)).toCharArray();
			for (int i = 0; i < y; ++i) {
//				memory.writeBlock(strInBytes, memory.getBlock(x * 16)[i]);
			}
		} else if (z == 2) {
//			PhysicalMachine.writeToPrinter(memory.getBlock(16 * x)[y]);
		} else if (z == 3) {
			for (int i = 0; i < y; ++i) {
//				PhysicalMachine.writeToPrinter(memory.getBlock(16 * x)[i]);
			}
		} /*else if(z=='x'){
			byte [] strInBytes = (PhysicalMachine.readFromInput(1)).getBytes();
			R1 = strInBytes;
		}*/ else {
			PhysicalMachine.writeToPrinter("Wrong command");
		}

	}


	public void setZF() {
		SF |= (1 << 6);
	}

	public void clearZF() {
		SF &= ~(1 << 6);
	}

	public int getZF() {
		return (SF >> 6) & 1;
	}

	public void setSF() {
		SF |= (1 << 5);
	}

	public void clearSF() {
		SF &= ~(1 << 5);
	}

	public int getSF() {
		return (SF >> 5) & 1;
	}

	public void setOF() {
		SF |= (1 << 4);
	}

	public void clearOF() {
		SF &= ~(1 << 4);
	}

	public int getOF() {
		return (SF >> 4) & 1;
	}

	public short getIC() {
		return IC;
	}

	public void setIC(short IC) {
		this.IC = IC;
	}

	@Override
	public String toString() {
		return "+------------------+" + '\n' +
				"|       VM         |" + '\n' +
				"+------------------+" + '\n' +
				"| IC: " + getIC() + '\n' +
				"| ZF: " + getZF() + '\n' +
				"| SF: " + getSF() + '\n' +
				"| OF: " + getOF() + '\n' +
				"+-----------------+";
	}

}
