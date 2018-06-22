package masha; 
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.*;
public class Lexems { // Создаем класс Лексера
	                  //Описание: создадим перечисление с типом токена и его шаблоном (регулярное выражение)
enum LexemType { // перечисление 
	             //перечисляем их
	//Тип:                                Шаблон: 
	WHILE_KW("WHILE_KW", Pattern.compile("^while $")), // pattern.compile компиляция выбранного регулярного выражения в шаблон
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
	//определяем имя и шаблон в перечислении
	String type; //имя на входе
	Pattern pattern; // шаблон на входе; patern - для задания регулярного выражения
	LexemType(String t, Pattern p) { //Конструктор для определения входных значений
		type = t; //переменной type присваиваем значение t
		pattern = p;

	}

	}
    //Описание: создает очерель с токенами
	public static Queue<Token> makeTokenList(String in){ // на выходе - очередь из токенов, () - на входе строка 
		
		Queue<Token> tok = new LinkedList<Token>(); // создание очереди
		String temp_st = new String(); 
		String type; 
        //Описание: цикл, в котором мы отщипываем буковки от входной строки
		while(in.length()>0) {

			temp_st=temp_st+(in.charAt(0)); // к временной строке добавляем по одному символу (0 - место буквы в строке)

			type = parse(temp_st); // переменной присваиваем обработанные по шаблону символы из временной 
			in = removeCharAt(in,0); // удаляем из строки на 0-ом месте
			if((type=="Space")&&(in.length()>0)&&(type!=parse(temp_st+(in.charAt(0))))) { //сравнение текущего типа и следующего
				temp_st=""; //очищение временной строки (присваиваем ей ничего)
				tok.offer(new Token(type,temp_st)); //в очередь с токенами засунули новый токен, который на вход принимает тип и саму вр.строку
			}
			else if((in.length()>0)&&(isPriority(parse(temp_st+(in.charAt(0)))))){ //получает тип вр.строки и проверяет, является ли этот тип кл.словом  
				tok.offer(new Token(parse(temp_st+(in.charAt(0))),temp_st+(in.charAt(0)))); //в очередь с токенами засунули новый токен, который на 
				                                                                            //вход принимает тип вр.стр и саму вр.строку
				temp_st="";
				in = removeCharAt(in,0); // удаляем из строки на 0-ом месте
			}
			else if((in.length()>0)&&(type!=parse(temp_st+(in.charAt(0))))){ //если это не пробел и не кл.слово
				tok.offer(new Token(type,temp_st)); 
				temp_st="";
			} else if(in.length()==0){ //конец
				type = parse(temp_st);
				tok.offer(new Token(type,temp_st));
			}

		}
		return tok;

	}

	public static String parse(String s) { //тип возвращаемого объекта - String, (...) - что поступает на вход  
		//Описание: проверка по шаблону

		String res = "WARNING"; // возвращает warning, если есть несоответсвие шаблону

		for(LexemType lexem : LexemType.values()) { // (тип итерац_переменной + ее имя : коллекция (метод values возвращает 
			                                        // в форме массива перечисление Lexemtype в порядке их объявления

			Pattern pat = lexem.pattern; //pat присваивается значение, которое lexem приняла из pattern, те шаблон
			Matcher m = pat.matcher(s); //m присваиваем значение, которое pat примет для шаблона и конкретной строки

			m.find(); // призываем matcher m к поиску соответсвия шаблону

			if(m.matches()) { //если m нашел соответсвие

				res = lexem.type; //переменной присваивается строковый тип (type) из перечисления lexem

				break;

			}

		}
		return res; // если не нашел, то warning

	}
	
	
	
	static boolean isPriority(String s1){
		switch(s1){ //(то, что сравниваем)
			case "WHILE_KW": //("то, что будет результатом сравнения")
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
			default: //если ни с чем не совпал, то false
				return false;
		}
		
		
	}
	
	
	
	
	
	 public static String removeCharAt(String s, int pos) { //удаление символа из строки
	      return s.substring(0, pos) + s.substring(pos + 1); //склейка двух подстрок без текущего символа 
	      													// (подстр от 0 до текущей позиции) + (подстр от текущей + 1 и до конца)
	 }
	 
	 
	 
	 
	 
	 public static void showList(Queue<Token> list){ // Вывод всего из временной строки
		 Queue<Token> show = list;
		 String type=""; //инициализация
		 String value="";
		 do{
			 if(show.isEmpty()){
				 break;
			 }
			 type = show.element().s; // возвращение типа крайнего токена
			 value = show.element().value; // возвращение значени крайнего токена
			 if (type=="Space") continue;
			 System.out.println("Type= "+type+"  "+"Value= "+value);
		 } while (show.poll() != null); // удалить первый элемент из очереди (если не нулевая, продолжаем выводить токены)
	 }

}