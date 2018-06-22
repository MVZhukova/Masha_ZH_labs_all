package masha;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.Queue;
//��������� ������. �������� �� ���� ������� �� ������� � ��������������� ���������� �� ����������.
public class Parser {
	private static Token currentToken; // Token - ���, � ����� ��� 
	private static Queue<Token> tokens;
	public static int counter=0;
	
	public static void parse(String in) {
		tokens = Lexems.makeTokenList(in);//�������� ������� �� �������
		lang();
		System.out.println("!!!WELLDONE!!!");
		
	}
	

	static void match() { //����������� ������ �� �������, �� �� �������
		currentToken = tokens.element(); // ���������� ������ ������������� �������� 1��� ������ �� ������� 
		while(currentToken.s.equals("Space")){ //��������: ���� ������������ �������
			tokens.poll(); //������� 
			currentToken = tokens.element();
		}
	}

	static void remove() { //������� ������� ������������ �����
		tokens.poll(); //������� ������� ��������
		counter++; // ���� ����� (������ �������)
	}

	static void lang() {
		try { // ������� ���������
			expr();
		} catch (NoSuchElementException e) { //������� ������; ���� ��������� ���, �� �� ��� �����
			
		}
		catch(EmptyStackException e1){ // ���� ����-�� �� ������� ; (����� �� ������������)
			throw new IllegalArgumentException("Something is missing o_o"); //���������� ����� ������ � �����
		}
	}

	static void expr() {
				try {
					assign(); //������������
				} catch (IllegalArgumentException e) { //������ �� ���������� �������
					try{
						while_c();
					} catch (IllegalArgumentException e1) { //������ �� ���������� �������
						try{
							fu();
						} catch (NoSuchElementException e2) { //��� ������� ����� ������ while/assign - � ��� ��������� 
							
					}
				}
			}
				expr(); //��� ���, �� ��� ��� ���� �� ��������
			

	}

	private static void fu() {
			fu_one();
	}


	private static void fu_one() {
		FU_ONE();
		VAR();
		
	}


	


	private static void while_c() {
		WHILE_KW();
		try{ 
			BR_O();
			value();
			COMP();
			value();
			BR_C();
			DO_KW();
			try {
				expr();
			} catch (NoSuchElementException e) { //������ �� ���������� �������
			
		}
			catch (IllegalArgumentException e1){ //���� ����������� ���������
				try{
					END_KW();
					
				} catch (IllegalArgumentException e2){ //���� ��� ���, �� ������ ������
					throw e1; //������ ������
				}
			}
		
		} catch (NoSuchElementException e1){ // �� ������� �����-���� �������� �� �������� try (������ ���� ���)
			throw new EmptyStackException(); //������ ���� ������ �_�
		}
	}

	private static void assign() {
		VAR();
		try{
			ASSIGN_OP();
			value();
			try {
				op_value();
			} catch (NoSuchElementException e) { //���� �� ����� �������������� ��������, �� ��
			
			}
			catch (IllegalArgumentException e3){ 
				
			}
		}
		catch(NoSuchElementException e1){ // �� ������� �����-���� �������� �� �������� try (������ ���� ���)
			throw new EmptyStackException(); //������ ���� ������ �_�
		}
		
	}

	static void op_value() {
		OP();
			try{
				value();
			}
		catch(NoSuchElementException e){ //����-�� �� �������, �� ������ ������
			throw new EmptyStackException();
		}
		catch(IllegalArgumentException e1){ //���� �� ������������ ����� (���� ����������, ���� ����� �����!), ������ ������
			throw new EmptyStackException();
		}
		op_value();
	}
	
	private static void value() {

		try {
			DIGIT();
		}
		catch (IllegalArgumentException e) { // ���� ����-�� �� �������, ���� ������
			try{
				VAR();
			}
			catch (IllegalArgumentException e1){
				br_expr();
			}
		}
	}


	private static void br_expr() {
		BR_O();
		try{
			value();
			try {
				op_value();
			}	catch(IllegalArgumentException e1){ //���� ����-�� �� �������, ���� ������, ���� ")" 
				try{
					BR_C();
				}
				catch(IllegalArgumentException e2){ //���� ��� ����������� ������, �� �_�
					throw e1; 
				}
			}
			}
		catch(NoSuchElementException e){ ////���� �� ������� ����-�� �� ����������, �� ������ ������
			throw new EmptyStackException(); 
		}
		
		
	}
	
	//���������

	private static void OP() {
		
		match();
		if (!(currentToken.s.equals("OP"))) { //���� �� ����� ��� ��, �� ������
			throw new IllegalArgumentException("Operation expected, got " + currentToken.s+" at "+counter); // ��������� ���� + ������� ������

		}

		remove(); //�������� ������������� ������

	}

	static void VAR() {

		match();
		if (!(currentToken.s.equals("VAR"))) {
			throw new IllegalArgumentException("Var expected, got " + currentToken.s+" at "+counter);
		}
		remove();

	}

	private static void ASSIGN_OP() {

		match();
		if (!(currentToken.s.equals("ASSIGN_OP"))) {
			throw new IllegalArgumentException("Assign_op expected, got " + currentToken.s+" at "+counter);
		}
		remove();

	}

	private static void DIGIT() {

		match();
		if (!(currentToken.s.equals("DIGIT"))) {
			throw new IllegalArgumentException("Digit expected, got " + currentToken.s+" at "+counter);
		}

		remove();

	}
	private static void WHILE_KW(){
		match();
		if (!(currentToken.s.equals("WHILE_KW"))) {
			throw new IllegalArgumentException("While expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void BR_O(){
		match();
		if (!(currentToken.s.equals("BR_O"))) {
			throw new IllegalArgumentException("Open bracket expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void COMP(){
		match();
		if (!(currentToken.s.equals("COMP"))) {
			throw new IllegalArgumentException("Comparation expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void BR_C(){
		match();
		if (!(currentToken.s.equals("BR_C"))) {
			throw new IllegalArgumentException("Close bracket expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void DO_KW(){
		match();
		if (!(currentToken.s.equals("DO_KW"))) {
			throw new IllegalArgumentException("Do expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void END_KW(){
		match();
		if (!(currentToken.s.equals("END_KW"))) {
			throw new IllegalArgumentException("End expected, got " + currentToken.s+" at "+counter);
		}

		remove();
	}
	private static void FU_ONE() {
		match();
		if (!(currentToken.s.equals("FU_ONE"))) {
			throw new IllegalArgumentException("Function expected, got " + currentToken.s+" at "+counter);
		}

		remove();
		
	}

	
}