package lt.kurti.rm;

import java.util.Arrays;

public class Word {

	public char[] word = new char[4];

	public Word(final String data) {
		this.word = data.toCharArray();
	}

	public Word(final char[] data) {
		System.arraycopy(data, 0, word, 0, 4);
	}

	@Override
	public String toString() {
		return new String(word);
	}
}
