package masha;
public class Variable {
	String name;
	String type;
	Object value;
	Variable(String n, Object value, String type){
		name=n;
		this.type = type;
		this.value = value;	
	}
}
