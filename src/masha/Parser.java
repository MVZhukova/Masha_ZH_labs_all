package masha;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.Queue;
//Проверяем лексер. Получаем на вход очередь из токенов и последовательно проходимся по грамматике.
public class Parser {
	private static Token currentToken; // Token - тип, а потом имя 
	private static Queue<Token> tokens;
	public static int counter=0;
	
	public static void parse(String in) {
		tokens = Lexems.makeTokenList(in);//получаем очередь из токенов
		lang();
		System.out.println("!!!WELLDONE!!!");
		
	}
	

	static void match() { //вытаскивает токены из очереди, но не удаляет
		currentToken = tokens.element(); // выбранному токену присваивается значение 1ого токена из очереди 
		while(currentToken.s.equals("Space")){ //Описание: цикл пропускающий пробелы
			tokens.poll(); //удаляем 
			currentToken = tokens.element();
		}
	}

	static void remove() { //удаляет крайний обработанный токен
		tokens.poll(); //возврат первого элемента
		counter++; // след токен (увелич счетчик)
	}

	static void lang() {
		try { // пробует выражения
			expr();
		} catch (NoSuchElementException e) { //ловится ошибка; если выражений нет, то ок или иначе
			
		}
		catch(EmptyStackException e1){ // если чего-то не хватает ; (ВЫЗОВ ИЗ последующего)
			throw new IllegalArgumentException("Something is missing o_o"); //выкидывает новую ошибку и пишет
		}
	}

	static void expr() {
				try {
					assign(); //присваивание
				} catch (IllegalArgumentException e) { //ошибка не совпадение шаблону
					try{
						while_c();
					} catch (IllegalArgumentException e1) { //ошибка не совпадение шаблону
						try{
							fu();
						} catch (NoSuchElementException e2) { //нет токенов после успеха while/assign - и это нормально 
							
					}
				}
			}
				expr(); //еще раз, до тех пор пока не ошибемся
			

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
			} catch (NoSuchElementException e) { //ошибка не совпадение шаблону
			
		}
			catch (IllegalArgumentException e1){ //если закончились выражения
				try{
					END_KW();
					
				} catch (IllegalArgumentException e2){ //если энд нет, то выброс ошибки
					throw e1; //выброс ошибки
				}
			}
		
		} catch (NoSuchElementException e1){ // не хватает какой-либо операции из главного try (должны быть все)
			throw new EmptyStackException(); //выброс этой ошибки О_О
		}
	}

	private static void assign() {
		VAR();
		try{
			ASSIGN_OP();
			value();
			try {
				op_value();
			} catch (NoSuchElementException e) { //если не нашел арифметическую операцию, то ок
			
			}
			catch (IllegalArgumentException e3){ 
				
			}
		}
		catch(NoSuchElementException e1){ // не хватает какой-либо операции из главного try (должны быть все)
			throw new EmptyStackException(); //выброс этой ошибки О_О
		}
		
	}

	static void op_value() {
		OP();
			try{
				value();
			}
		catch(NoSuchElementException e){ //чего-то не хватает, то выброс ошибки
			throw new EmptyStackException();
		}
		catch(IllegalArgumentException e1){ //если не соответсвует валью (либо переменная, либо число можно!), выброс ошибки
			throw new EmptyStackException();
		}
		op_value();
	}
	
	private static void value() {

		try {
			DIGIT();
		}
		catch (IllegalArgumentException e) { // если чего-то не хватает, едем дальше
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
			}	catch(IllegalArgumentException e1){ //если чего-то не хватает, едем дальше, ждем ")" 
				try{
					BR_C();
				}
				catch(IllegalArgumentException e2){ //если нет закрывающей скобки, то О_О
					throw e1; 
				}
			}
			}
		catch(NoSuchElementException e){ ////если не хватает чего-то из грамматики, то выброс ошибки
			throw new EmptyStackException(); 
		}
		
		
	}
	
	//ТЕРМИНАЛЫ

	private static void OP() {
		
		match();
		if (!(currentToken.s.equals("OP"))) { //если не такая как ОР, то ошибка
			throw new IllegalArgumentException("Operation expected, got " + currentToken.s+" at "+counter); // выбранный токе + позиция токена

		}

		remove(); //удаление обработанного токена

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