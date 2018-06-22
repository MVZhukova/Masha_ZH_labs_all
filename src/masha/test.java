package masha;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class test {

	public static void main(String[] args) {		
		
		//Parser+Lexer test
		Scanner in = new Scanner(System.in);
		System.out.print("Write your text ->> ");
		String str = "";
		if (in.hasNextLine()) {
			str = in.nextLine();
		}
		//Lexems.showList(Lexems.makeTokenList(str));
		//Parser.parse(str);
		
		//Stack test
		StackString st = new StackString(Lexems.makeTokenList(str));
		st.execute();
		st.countSt();
		//st.showTable();
		//st.showPoliz();  ss
	}

}
