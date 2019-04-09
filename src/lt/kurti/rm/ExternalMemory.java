package lt.kurti.rm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class ExternalMemory {

	public static void readToHDD(String sourceFile) {
		try(FileReader fr = new FileReader(sourceFile);
			BufferedReader br = new BufferedReader(fr)){

			String line = "";
			int c;
			int block = 0;

			while((c = br.read()) != -1){
				if(line.length() == 16){
					HDD.write(line.toCharArray(), block);
					block++;
					line = "";
				}
				//ignoruojam siuksles: LF(new line feed) ir CR(carriage return)
				if(c != 10 && c != 13){
					line+= (char)c;
				}
			}
			HDD.write(line.toCharArray(), block);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Reading from External Memory to HDD finished.");
	}
}
