package org.rebecalang.transparentactormodelchecker;

import java.util.HashMap;

class A implements Cloneable {
	String b;
	public A clone() {
		int a;
		a = 10;
		return this;
	}
}

public class TempTest {
	public static void main(String[] args) {
		A a1 = new A();
		a1.b = "salam";
		
		HashMap<Integer, A> x = new HashMap<Integer, A>();
		x.put(1, a1);
		x.clone();
	}
}
