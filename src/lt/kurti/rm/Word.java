package lt.kurti.rm;

public class Word {

	public char[] word = new char[4];

	public Word(String data){
		word = data.toCharArray();
	}
	public Word(char[] data){
		System.arraycopy(data, 0, word, 0, data.length);
	}

	public String toString(){
		return new String(word);
	}
}
