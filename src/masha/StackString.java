package masha;

import java.util.ArrayList; 
import java.util.Queue;
import java.util.Stack;
  
public class StackString {
	ArrayList<Variable> table = new ArrayList<Variable>(); //создание таблицы переменных 
	private Queue<Token> tokens; // очередь из токенов создается
	private static Stack<Token> stack = new Stack<Token>(); // объявляю стек ПОЛИЗа (ПОЛИЗ в виде стека)
	private static Stack<Token> deadlock = new Stack<Token>(); // объявляю тупики
	private static Token currentToken;
	private static ArrayList<Token> poliz = new ArrayList<Token>(); // ПОЛИЗ в виде массива
	StackString(Queue<Token> tokens){ //конструктор для принятия очереди токенов
		this.tokens = tokens;
	}
	public Stack<Token> execute(){ // вызывает метод для создания ПОЛИЗа
		addIn(); //формирование ПОЛИЗа на стеке  
		stack = reverse(stack); // стек = перевернутый стек
		makePoliz(); //ПОЛИЗ в виде массива
		return stack;
		
	}
	public ArrayList<Variable> getTable(){ //возвращает таблицу с переменными для/из других классов
		return table;
	}
	void addIn(){ //формирование ПОЛИЗа на стеке
		while(!tokens.isEmpty()){ //пока список с токенами не пустой, берем новый токен
			currentToken = tokens.poll(); // взяли элемент и удалили, и скопировали в текущий токен
			if(currentToken.s.equals("DIGIT")){ //если цифра - в стек
				stack.push(currentToken);
			} else if(currentToken.s.equals("VAR")){ 
				boolean found =false; // для поиска 
				int a = findInTable(currentToken.value); //поиск в таблице перемнных по имени (а=1, ищет по а), 
														//а возращает индекс нахождения "а" в табл.перемен.
				if(a!=-1) found=true; //-1 - нет в таблице переменных
				if(found){ // если нашлась, то кладем в стек токен
					stack.push(currentToken);
				} else {
					table.add(new Variable(currentToken.value, null, "Integer"));//если нет такой переменной, создает и в стек и тд
					stack.push(currentToken);
				}
			} else 
				if(checkDeadlock(currentToken))	{ //не перем и не цифра, в тупик сразу (вызывает пров-ку приоритетов)
					deadlock.push(currentToken); // checkdeadlock возвращает true (кладем в тупик) и false (ничего не делаем) 
				
			}
			
			
		}
	while(!deadlock.isEmpty()){ // выталкиваем все из тупика и после очищаем
		stack.push(deadlock.pop());
	}
	}
	
	private boolean checkDeadlock(Token last) { // + СОЗДАНИЕ МЕТОК и одновременно проверка приоритета для тупика
		                                       //checkdeadlock возвращает true (кладем в тупик) и false (ничего не делаем) 
		boolean prior = true;
		if(last.s.equals("BR_C")){ //закрывающая скобка
			if(deadlock.isEmpty()) return true; // если в тупике ничего нет - странно
			while(!deadlock.peek().s.equals("BR_O")){ //пока в тупике нет окрывающей скобки
				stack.push(deadlock.pop()); //выталкиваем из тупика все до "("
			}
			deadlock.pop(); //удаляем скобку
			return false;
		} else if(last.s.equals("DO_KW")){ //создание метки do = true/false в зависимости от условия
			Token l = new Token("LABEL_DO", "0");
			stack.push(l);
			return true;
		} else if(last.s.equals("END_KW")){ // создание метки еnd = адрес начала цикла 
			Token l = new Token("LABEL_END", "0");
			if(deadlock.isEmpty()) return true;
			while(!deadlock.peek().s.equals("DO_KW")){ // пока в тупике нет do 
				stack.push(deadlock.pop()); //выталкиваем все до do
			}
			deadlock.pop(); //удаляем do
			stack.push(l); //кладем метку end
			return false;
		} else if((last.s.equals("OP"))||(last.s.equals("COMP"))||(last.s.equals("ASSIGN_OP"))||(last.s.equals("Space"))) {
			while(prior) {	// пока соблюден приоритет	
				if(deadlock.isEmpty()) {
					break;
				}
				Token prev = deadlock.peek(); // возращаем значение крайнего токена, но не удаляем
				if(checkPriority(last)<checkPriority(prev)){ //last - то что, собираюсь вставить, prev - вершина (крайний токен)
					stack.push(deadlock.pop()); //кладем в стек и выталкиваем из тупика
				} else prior=false;
			}
		} else if((last.s.equals("WHILE_KW"))||(last.s.equals("FU_ONE"))) { //самый высок приор - сразу в стек
			stack.push(last); //записываем в стек
			return false;
		}
		return true;	
		
	}
	// Описание: проверяет приоритеты операций и возвращает их
	private int checkPriority(Token last){ //last - то что, собираюсь вставить
		int priority=0;
		switch(last.s){
		case("OP"):{
				if((last.value.equals("*"))||(last.value.equals("/"))){
					priority=4;
				} else {
					priority=3;
				}
			break;
			}
		case("ASSIGN_OP"):{
			priority=2;
			break;
		}
		case("COMP"):{
			priority=1;
			break;
		}
		case("FU_ONE"):{
			priority=0;
			break;
		}
		
		case("Space"):{
			priority=0;
			break;
		}
	
		}
		return priority;
	}
	public void print(Stack<Token> s) { //печать стека
		Stack<Token> show = s;
		 String type="";
		 String value="";
		 do{
			 if(show.isEmpty()){
				 break;
			 }
			 type = show.peek().s; //тип крайнего токена записываем в type
			 value = show.pop().value; // удалить и вернуть (скопировать) значение крайнего токена 
			 if (type=="Space") continue; 
			 System.out.println("Type= "+type+"  "+"Value= "+value);
		 } while (true); // бесконечный цикл
	}
	 static Stack<Token> reverse(Stack<Token> s) { //переворачивает для счета
		 Stack<Token> st= new Stack<Token>();	
		 while(!s.isEmpty()){
			 st.push(s.pop()); //берем из 2ого (s) стека значения и кладем его в первый стек (st)
		 }
		 return st;	//1ый стек	 
	        
	    }
	 public void showTable(){ //просмотреть таблицу переменных
		 for(int i=0;i<table.size();i++){ // ит_пер (индекс) < размера таблицы
			 System.out.println(table.get(i).type+" "+table.get(i).name+" "+table.get(i).value); //по индексу ищем имя и значение
		 }
	 }
	 
	 
	 ////////////////////////2 ЧАСТЬ. СЧИТАЕМ НА СТЕКЕ///////////////////////////////////////////////
	 public int countSt(){ // считаем на стеке 
		 Stack<Token> box = new Stack<Token>(); //создаем новый стек, гле считаем (box - стек в столбик)
		 while(!stack.isEmpty()){ //пока не пустой
			 box.push(stack.pop()); //записываем из ПОЛиза эл-т в box и удаляем эл-т из полиза
			 
			 //Запихнула в коробку элемент и смотрю: что это ?
			 
			 //assign
			 if(box.peek().s.equals("ASSIGN_OP")){
				 box.pop(); // удаляем операцию 
				 int f=0; //для поиска
				 int number = getDigit(box.peek().value); // Перевод в числа значения того, чему равна переменная 
				 box.pop(); //удаление эл-та
				 f=findInTable(box.peek().value); // присваивание f индекса из таблицы переменных
				 String val = box.pop().value; // присваиваем имя переменной  
					 table.set(f, new Variable(val, number, "Integer")); //в табл.переменных изменяет значения по индексу f
					 
			
					 
					 //Operation
			 } else if(box.peek().s.equals("OP")){
				 String vall = box.pop().value; // присваиваем имя операции 
				 int s1 = getDigit(box.pop().value); // Перевод в числа значения того, чему равна переменная 
				 int s2 = getDigit(box.pop().value); // Перевод в числа значения того, чему равна переменная 
				 switch(vall){
					 case("+"):{
						 box.push(new Token("DIGIT", String.valueOf(s1+s2))); // String.valueOf- перевод результата счета в строку
						 break;
					 }
					 case("-"):{
						 box.push(new Token("DIGIT", String.valueOf(s2-s1)));
						 break;
					 }
					 case("*"):{
						 box.push(new Token("DIGIT", String.valueOf(s1*s2)));
						 break;
					 }
					 case("/"):{
						 box.push(new Token("DIGIT", String.valueOf(s2/s1)));
						 break;
					 }
				 }
				 
				 
				 
				 //COMP (во время сравнения присваивает метки истина/ложь) (тоже самое, что и вверху)
			 } else if(box.peek().s.equals("COMP")){
				 String vall = box.pop().value; // присваиваем имя операции 
				 int s1 = getDigit(box.pop().value);
				 int s2 = getDigit(box.pop().value);
				 stack.pop();
				 switch(vall){
					 case(">"):{
						 stack.push(new Token("LABEL_DO", Boolean.toString(s2>s1)));
						 break;
					 }
					 case("<"):{
						 stack.push(new Token("LABEL_DO", Boolean.toString(s2<s1)));
						 break;
					 }
					 case("=="):{
						 stack.push(new Token("LABEL_DO", Boolean.toString(s1==s2)));
						 break;
					 }
					 case("!="):{
						 stack.push(new Token("LABEL_DO", Boolean.toString(s1!=s2)));
						 break;
					 }
				 }
				 
				 
				 
				 //do
			 } else if(box.peek().s.equals("LABEL_DO")){ // если истина, едем дальше
				 if(!Boolean.valueOf(box.pop().value)){ //если do - ложь, выталкиваем до энда
					 while(!stack.peek().s.equals("LABEL_END")){
						 stack.pop(); //выталкивает до энда
					 }
					 stack.pop(); 
					 poliz.set(getEndIndex(),new Token("Space", " ")); // в полизе по индексу 1ого end 
					                                                   //записываем ничего, чтобы удалить end 
				 }
				
				 
				 
				 
				 //end				 			 
			 } else if(box.peek().s.equals("LABEL_END")){				 
				 int it = getEndIndex(); //находим индекс энд
				do{
					 stack.push(poliz.get(it)); //в стек кладем из массива эл-т по индексу
					 it--; //декреиент индекса для возврата к while
				 } while(!poliz.get(it).s.equals("WHILE_KW")); // цикл пока не найдем while
				 
				 
				 
				 //1 func				 
			 } else if(box.peek().s.equals("FU_ONE")){
				 box.push(stack.pop());//берем из 2ого (stack) стека значения и кладем его в первый стек (box)
				 Token s1 = box.pop(); // удаляем и копируем в s1(то, что мы печатаем)
				 String vall = box.pop().value; //vall - само ключевое слово print

				 switch(vall){
				 case("print "):{
					 System.out.println(s1.value+"="+table.get(findInTable(s1.value)).value); //выводим имя пермененной и значение
					 break;
				 }
			 }
				 
			 } 
		 }

		return 0; 
	 }
	 private int findInTable(String s){ //пробежка по именам, если сопадает - возвращает индекс, если нет - -1
		 for(int i=0; i<table.size();i++){
				if(table.get(i).name.equals(s)){ //пробежка по именам, если сопадает - возвращает индекс, если нет - -1
					return i;
				}
			 }
		 return -1;
	 }
	 private int getDigit(String s){ //перевод из строки в цифру 
		 int number=0; //число, которое переведем из строки в число
		 try{
			 number = Integer.parseInt(s); // преобразует строку в число (пробуем!)
		 } catch(NumberFormatException e1){ //если не получаемтся преобразовать  в число, то пробуем использвать 
			 								//это как имя перменной в табл.перемн
			 number = (Integer) table.get(findInTable(s)).value; //ищем по имени переменной и берем ее значение и преобразуем 
			 													//ее в числовой тип (..) - явное преобразование
		 }
		 return number;
	 }
	 
	 
	 
	 //////////////////////////////////////////////////////
	 private void makePoliz(){ //копия стека полиза в виде массива (перевод)
		 Stack<Token> cpStack= (Stack<Token>) stack.clone(); // явное преобразования копии стека, 
		 													//ибо stack.clone() возвращает object
		 int i=0,k=0; //i - итератор, k - метка while
		 while(!cpStack.isEmpty()){
			 if(cpStack.peek().s.equals("WHILE_KW")){ // нужно для того, чтобы сохранить адрес while
				 k=i;
			 } else if(cpStack.peek().s.equals("LABEL_END")){ //энд = метка на while
				 cpStack.push(new Token(cpStack.pop().s, String.valueOf(k))); //удаляем и кладем метку (ее имя) end, а в качестве значения 
				 															// присваиваем сохраненную метку на while 
				 poliz.add(cpStack.pop()); // сформированный токен записываем в ПОЛИЗ
				 i++;
				 continue;
			 }
			 i++;
			 poliz.add(cpStack.pop()); // сформированный токен записываем в ПОЛИЗ (нужно)
		 }
		
	 }
	 public void showPoliz(){ // просмотреть массив
		 for(int i=0;i<poliz.size();i++){
			 System.out.println(poliz.get(i).s+" "+poliz.get(i).value); // по индексу выдает тип и значение
		 }
	 }
	 private int getEndIndex(){ //поиск первого end, возвращает индекс
		 for(int i=0;i<poliz.size();i++){
			 if(poliz.get(i).s.equals("LABEL_END")) {
				 return i;
			 }
		 }
		return -1;
		 
	 }
}
