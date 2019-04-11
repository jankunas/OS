package lt.kurti.vm;

import lt.kurti.rm.Memory;
import lt.kurti.rm.Printer;
import lt.kurti.rm.PhysicalMachine;

public class VirtualMachine {

	private byte C;
	private short IC;

	private Memory memory;

	public VirtualMachine() {
		this.memory = PhysicalMachine.getMemory();
		System.out.println("VM init.");
	}

	public void processCommands() {
		for (int cmdBlock = 0; cmdBlock < 4; ++cmdBlock) {
			char[] block = memory.getBlock(cmdBlock);
			//Splitting every 4 'bytes'
			String[] blockString = new String(block).split("(?<=\\G....)");
			for (String s : blockString) {
				try {
					if (s.equals("HALT")) {
						return;
					}
					//nebutina turbut
					if (s.contains("_")) {
						s = s.replace("_", "");
					}
					if (PhysicalMachine.getMODE() == 0) {
						System.out.println(PhysicalMachine.getInfo());
						System.out.println("VM " + this.toString());
						System.in.read();
					}
					resolveCommand(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		} else if (line.substring(0, 3).equals("MUL")) {
			MUL();
		} else if (line.substring(0, 3).equals("DIV")) {
			DIV();
		} else if (line.substring(0, 3).equals("CMP")) {
			CMP();
		} else if (line.substring(0, 2).equals("LW")) {
			LW(Integer.parseInt(line.substring(2, 4)) + 64);
		} else if (line.substring(0, 2).equals("LE")) {
			LE(Integer.parseInt(line.substring(2, 4), 16) + 64);
		} else if (line.substring(0, 2).equals("PM")) {
			PM(Integer.parseInt(line.substring(2, 4), 16) + 64);
		}
        /*else if (line.substring(0, 2).equals("LS")) {
            LS(line.substring(3, 4));
        }
        else if (line.substring(0, 2).equals("LX")) {
            LX(line.substring(3, 4));
        }
         else if (line.substring(0, 2).equals("LY")) {
            LY(line.substring(3, 4));*
        }
        else if (line.substring(0, 2).equals("LL")) {
            LL(line.substring(3, 4));
        }
        */
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
		} else if (line.substring(0, 2).equals("IC")) {
			IC(line.substring(3, 4));
		}
        /*else if (line.substring(0, 2).equals("PD")) {
            PD();
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

	//Ši komanda palygina registre R1 ir R2 ęsančias reikšmes. Jeigu reikšmės lygios, ZF = 1, priešingu atveju ZF = 0.
	public void CMP() {
		if (PhysicalMachine.R1 == PhysicalMachine.R2) {
			setZF();
		} else {
			clearZF();
		}
		++IC;
	}

	//LWx1x2 - į registrą R1 užkrauna žodį nurodytu adresu 16 * x1 + x2.
	public void LW(int address) {
		int block = address / 16;
		int offset = (address - 64) % 16;

		char[] word = new char[4];
		int j = 0;
		for (int i = offset; i < offset + 4; ++i) {
			word[j] = memory.getBlock(block)[i];
			j++;
		}
		PhysicalMachine.R1 = Integer.parseInt(new String(word));
		++IC;
	}

	//LEx1x2 - į registrą R2 užkrauna skaičių, adresu 16 * x1 + x2.
	public void LE(int address) {
		int block = address / 16;
		int offset = (address - 64) % 16;

		char[] word = new char[4];
		int j = 0;
		for (int i = offset; i < offset + 4; ++i) {
			word[j] = memory.getBlock(block)[i];
			j++;
		}
		PhysicalMachine.R2 = Short.parseShort(new String(word));
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
		if (register.equals("R1")) {
			Printer.print(PhysicalMachine.getR1());
		}
		if (register.equals("R2")) {
			Printer.print(PhysicalMachine.getR1());
		}
		++IC;
	}

	//Isveda i ekrana atminties 4 baitus
	public void PM(int address) {

		int block = address / 16;
		int offset = (address - 64) % 16;

		char[] word = new char[4];
		int j = 0;
		for (int i = offset; i < offset + 4; ++i) {
			word[j] = memory.getBlock(block)[i];
			j++;
		}
		Printer.print(new String(word));
		++IC;
	}


	//LDx1x2 - nuskaito registrą R2
	//public void LD(String address) {
	//    ++IC;
	//}

	//JMx1x2 - besąlyginio valdymo perdavimo komanda. Ji reiškia, kad valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16 * x1 + x2
	public void JM(String address) {
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