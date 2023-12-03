package telran.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import telran.test.annotation.Test;

public class TestRunner implements Runnable{
	private Object testObj;
	
	public TestRunner(Object testObj) {
		super();
		this.testObj = testObj;
	}

	@Override
	public void run() {
		
			Class<?> clazz = testObj.getClass();
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method: methods) {
				if(method.isAnnotationPresent(Test.class)) {
					method.setAccessible(true);
					try {
						method.invoke(testObj);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						System.out.println("error   " + e.getMessage() );
					}
				}
			}
		
		
	}
	

}
