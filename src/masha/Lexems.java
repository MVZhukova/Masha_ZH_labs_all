package masha; 
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.*;
public class Lexems { // ������� ����� �������
	                  //��������: �������� ������������ � ����� ������ � ��� �������� (���������� ���������)
enum LexemType { // ������������ 
	             //����������� ��
	//���:                                ������: 
	WHILE_KW("WHILE_KW", Pattern.compile("^while $")), // pattern.compile ���������� ���������� ����������� ��������� � ������
	DO_KW("DO_KW", Pattern.compile("^do $")),//
	END_KW("END_KW", Pattern.compile("^end.$")),//
	VAR( "VAR", Pattern.compile("^[a-z]+[a-z0-9]*$")),//
	DIGIT( "DIGIT", Pattern.compile("^0|[1-9][0-9]*$")),//
	OP( "OP", Pattern.compile("^[+]|[-]|[\\*]|[/]$")),	//<<<<<<<<
	COMP("COMP", Pattern.compile("^(>)|(<)|(==)|(!=)$")),//<<<<<<<<<<
	ASSIGN_OP( "ASSIGN_OP", Pattern.compile("^=$")),//<<<<<<<<<<<
	BR_O("BR_O", Pattern.compile("^\\($")),//
	BR_C("BR_C", Pattern.compile("^\\)$")),//
	SPACE("Space", Pattern.compile("^ *$")),//
	FU_ONE("FU_ONE", Pattern.compile("^print $"));
	//���������� ��� � ������ � ������������
	String type; //��� �� �����
	Pattern pattern; // ������ �� �����; patern - ��� ������� ����������� ���������
	LexemType(String t, Pattern p) { //����������� ��� ����������� ������� ��������
		type = t; //���������� type ����������� �������� t
		pattern = p;

	}

	}
    //��������: ������� ������� � ��������
	public static Queue<Token> makeTokenList(String in){ // �� ������ - ������� �� �������, () - �� ����� ������ 
		
		Queue<Token> tok = new LinkedList<Token>(); // �������� �������
		String temp_st = new String(); 
		String type; 
        //��������: ����, � ������� �� ���������� ������� �� ������� ������
		while(in.length()>0) {

			temp_st=temp_st+(in.charAt(0)); // � ��������� ������ ��������� �� ������ ������� (0 - ����� ����� � ������)

			type = parse(temp_st); // ���������� ����������� ������������ �� ������� ������� �� ��������� 
			in = removeCharAt(in,0); // ������� �� ������ �� 0-�� �����
			if((type=="Space")&&(in.length()>0)&&(type!=parse(temp_st+(in.charAt(0))))) { //��������� �������� ���� � ����������
				temp_st=""; //�������� ��������� ������ (����������� �� ������)
				tok.offer(new Token(type,temp_st)); //� ������� � �������� �������� ����� �����, ������� �� ���� ��������� ��� � ���� ��.������
			}
			else if((in.length()>0)&&(isPriority(parse(temp_st+(in.charAt(0)))))){ //�������� ��� ��.������ � ���������, �������� �� ���� ��� ��.������  
				tok.offer(new Token(parse(temp_st+(in.charAt(0))),temp_st+(in.charAt(0)))); //� ������� � �������� �������� ����� �����, ������� �� 
				                                                                            //���� ��������� ��� ��.��� � ���� ��.������
				temp_st="";
				in = removeCharAt(in,0); // ������� �� ������ �� 0-�� �����
			}
			else if((in.length()>0)&&(type!=parse(temp_st+(in.charAt(0))))){ //���� ��� �� ������ � �� ��.�����
				tok.offer(new Token(type,temp_st)); 
				temp_st="";
			} else if(in.length()==0){ //�����
				type = parse(temp_st);
				tok.offer(new Token(type,temp_st));
			}

		}
		return tok;

	}

	public static String parse(String s) { //��� ������������� ������� - String, (...) - ��� ��������� �� ����  
		//��������: �������� �� �������

		String res = "WARNING"; // ���������� warning, ���� ���� ������������� �������

		for(LexemType lexem : LexemType.values()) { // (��� ������_���������� + �� ��� : ��������� (����� values ���������� 
			                                        // � ����� ������� ������������ Lexemtype � ������� �� ����������

			Pattern pat = lexem.pattern; //pat ������������� ��������, ������� lexem ������� �� pattern, �� ������
			Matcher m = pat.matcher(s); //m ����������� ��������, ������� pat ������ ��� ������� � ���������� ������

			m.find(); // ��������� matcher m � ������ ����������� �������

			if(m.matches()) { //���� m ����� �����������

				res = lexem.type; //���������� ������������� ��������� ��� (type) �� ������������ lexem

				break;

			}

		}
		return res; // ���� �� �����, �� warning

	}
	
	
	
	static boolean isPriority(String s1){
		switch(s1){ //(��, ��� ����������)
			case "WHILE_KW": //("��, ��� ����� ����������� ���������")
				return true;
			case "FU_ONE":
				return true;
			case "DO_KW":
				return true;
			case "END_KW":
				return true;
			case "COMP":
				return true;
			case "Space":
				return true;
			default: //���� �� � ��� �� ������, �� false
				return false;
		}
		
		
	}
	
	
	
	
	
	 public static String removeCharAt(String s, int pos) { //�������� ������� �� ������
	      return s.substring(0, pos) + s.substring(pos + 1); //������� ���� �������� ��� �������� ������� 
	      													// (������ �� 0 �� ������� �������) + (������ �� ������� + 1 � �� �����)
	 }
	 
	 
	 
	 
	 
	 public static void showList(Queue<Token> list){ // ����� ����� �� ��������� ������
		 Queue<Token> show = list;
		 String type=""; //�������������
		 String value="";
		 do{
			 if(show.isEmpty()){
				 break;
			 }
			 type = show.element().s; // ����������� ���� �������� ������
			 value = show.element().value; // ����������� ������� �������� ������
			 if (type=="Space") continue;
			 System.out.println("Type= "+type+"  "+"Value= "+value);
		 } while (show.poll() != null); // ������� ������ ������� �� ������� (���� �� �������, ���������� �������� ������)
	 }

}