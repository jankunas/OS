package lt.kurti.vm;

import static lt.kurti.rm.PhysicalMachine.R1;
import static lt.kurti.rm.PhysicalMachine.R2;
import static lt.kurti.rm.PhysicalMachine.AX;

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
			AD(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("SB")) {
			SB(line.substring(2, 4));
		} else if (line.substring(0, 4).equals("COMP")) {
			CMP();
		} else if (line.substring(0, 2).equals("LX")) {
				String memLoc = line.substring(2, 4);
				int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
				int x2 = Character.getNumericValue(memLoc.charAt(1));
				LX(x1, x2/4);
		} else if (line.substring(0, 2).equals("LW")) {
				LW(line.substring(2,3));
		} else if (line.substring(0, 2).equals("LR")) {
			LR(Integer.parseInt(line.substring(2, 3)));
		} else if (line.substring(0, 2).equals("SX")) {
			String memLoc = line.substring(2, 4);
			int x1 = Character.getNumericValue(memLoc.charAt(0)) + 64;
			int x2 = Character.getNumericValue(memLoc.charAt(1));
			SX(x1, x2);
		} else if (line.substring(0, 2).equals("JP")) {
			JP(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JE")) {
			JE(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JG")) {
			JG(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JL")) {
			JL(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JN")) {
			JN(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JC")) {
			JC(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("JO")) {
			JO(line.substring(2, 4));
		} else if (line.substring(0, 2).equals("LP")) {
			LP(line.substring(2, 4));
		} else if (line.substring(0, 3).equals("AND")) {
			AND();
		} else if (line.substring(0, 2).equals("OR")) {
			OR();
		} else if (line.substring(0, 2).equals("NR")) {
			NR(Integer.parseInt(line.substring(2,3)));
		} else if (line.substring(0, 3).equals("XOR")) {
			XOR();
		} else if (line.substring(0, 1).equals("I")) {
			INTER(line.substring(1, 4));
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
	public void AD(String val) {
		int xy = Integer.parseInt(val, 16);
		if (R1 + R2 > Integer.MAX_VALUE) {
			setOF();
			return;
		} else {
			memory.writeBlock(String.valueOf(R1+R2).toCharArray(),xy/16,xy%16);
		}
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	// Padalina R1 iš R2, įrašoma į R1. Jeigu reikšmės ženklo bitas yra 1, SF = 1.
	public void SB(String val) {
		int xy = Integer.parseInt(val, 16);
		if (R1 - R2 > Integer.MIN_VALUE) {
			setOF();
			return;
		} else {
			memory.writeBlock(String.valueOf(R1-R2).toCharArray(),xy/16,xy%16);
		}
		if (((R1 >> 6) & 1) == 1) {
			setSF();
		}
		++IC;
	}

	//Ši komanda palygina registre R1 ir R2 ęsančias reikšmes. Jeigu reikšmės lygios, ZF = 1, priešingu atveju ZF = 0.
	public void CMP() {
		if (R1 == R2) {
			setZF();
		}
		else if(R1>R2){
			clearZF();
			clearSF();
		}
		else if(R1<R2){
			clearZF();
			setSF();
		}
		else {
			clearZF();
		}
		++IC;
	}
	//LWx1x2 - į registrą R1 užkrauna žodį nurodytu adresu 16 * x1 + x2.
	public void LX(int x, int y){
		char[] word = memory.getWord(x, y).word;
		R1 = Integer.parseInt(new String(word));
		++IC;
	}

	public void LW(String val) {
		R1 = Integer.parseInt(val, 16);
		++IC;
	}

	//LRx - į registrą RX užkrauna R~X reikšmę
	public void LR(int x) {
		if(x==1){
			R1 = R2;
			++IC;
		}
		if(x==2){
			R2 = R1;
			++IC;
		}
		else
			PhysicalMachine.writeToPrinter("Wrong argument for command LR(x)");
	}

	//LSx1x2 - į atmintį adresu 16 * x1 + x2 rašo žodį ar skaičių.
	//Duomenu ivedimui is failo naudosim
	public void SX(int x, int y) {
		memory.writeBlock(String.valueOf(R1).toCharArray(),x,y);
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


	//Isveda i ekrana atminties 4 baitus
/*
	public void SX(final int x1, final int x2) {
		char[] word = memory.getWord(x1, x2).word;
		Printer.print(new String(word));
		++IC;
	}
*/

	//LDx1x2 - nuskaito registrą R2
	//public void LD(String address) {
	//    ++IC;
	//}

	//JMx1x2 - besąlyginio valdymo perdavimo komanda. Ji reiškia, kad valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16 * x1
	// + x2
	public void JP(String address) {
		IC(address);
	}

	//JEx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 1
	public void JE(String address) {
		if (getZF() == 1) {
			IC(address);
			//processCommands() tik paduot parametra, nuo kurios vietos vykdyt koda
		}
		else
			++IC;
	}

	//JGx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu ZF = 0 IR SF = OF
	public void JG(String address) {
		if (getZF() == 0 && getSF() == 0){//getOF()) {
			IC(address);
		}
		else
			++IC;
	}

	//JLx1x2 - valdymas turi būti perduotas kodo segmento žodžiui, nurodytam adresu 16* x1 + x2 jeigu SF != OF
	public void JL(String address) {
		if (getZF()==0 && getSF()==1){//getSF() != getOF()) {
			IC(address);
		}
		else
			++IC;
	}

	public void JN(String address) {
		if (getZF()==0){
			IC(address);
		}
		else
			++IC;
	}

	public void JC(String address) {
//		if (getCF()==1){
//			IC(address);
//		}
//		else
			++IC;
	}

	public void JO(String address) {
		if (getOF()==1){
			IC(address);
		}
		else
			++IC;
	}

	public void LP(String address) {
		if(R2>0){
			R2 = R2 - 1;
			IC(address);
		}
		else
			++IC;
	}

	public void AND() {
		R1 = R1 & R2;
		++IC;
	}

	public void OR() {
		R1 = R1 | R2;
		++IC;
	}

	public void NR(int x){
		if(x == 1)
			R1 = ~R1;
		if(x == 2)
			R2 = ~R2;
		++IC;
	}

	public void XOR(){
		R1 = R1^R2;
		++IC;
	}
	/// /IC - komandos skaitliukas. IC = 16 * x1 + x2;
	//Kam jis ir kuo skiriasi nuo JM? Kam ja pridejau isvis? :D
	public void IC(String address) {
		IC = Short.parseShort(address, 16);
	}

	public void INTER(String param) {
		String z = param.substring(0, 1);
		String x = param.substring(1, 2);
		String y = param.substring(2, 3);

		if (z.equals("0")) {
			AX = Integer.parseInt(x)*16+Integer.parseInt(y);
			PhysicalMachine.setSI((byte)1);
			PhysicalMachine.test();
			R1 = AX;
		}
		else if(z.equals("2")) {
			AX = R1;
			PhysicalMachine.setSI((byte)2);
			PhysicalMachine.test();
		}
		/*	//char[] strInBytes = (PhysicalMachine.readFromInput(1)).toCharArray();
			//memory.writeBlock(strInBytes, memory.getBlock(x * 16)[y]);
		} /*else if (z.equals("1")) {
			char[] strInBytes = (PhysicalMachine.readFromInput(y)).toCharArray();
			for (int i = 0; i < y; ++i) {
//				memory.writeBlock(strInBytes, memory.getBlock(x * 16)[i]);
}
} else if (z.equals("2")) {
//			PhysicalMachine.writeToPrinter(memory.getBlock(16 * x)[y]);
		} else if (z.equals("3")) {
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
