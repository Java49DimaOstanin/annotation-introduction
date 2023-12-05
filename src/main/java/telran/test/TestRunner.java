package telran.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.IntStream;

import telran.test.annotation.BeforeEach;
import telran.test.annotation.Test;

public class TestRunner implements Runnable {
	private Object testObj;
	
	public TestRunner(Object testObj) {
		super();
		this.testObj = testObj;
	}

	@Override
	public void run() {
			Class<?> clazz = testObj.getClass();
			Method[] methods = clazz.getDeclaredMethods();
			Method[] beforeEachMethods = getBeforeEachMethods(methods);
			runTestMethods(methods, beforeEachMethods);

	}

	private Method[] getBeforeEachMethods(Method[] methods) {
		
		return Arrays.stream(methods).filter(m -> m.isAnnotationPresent(BeforeEach.class)).toArray(Method[]::new);
	}

	private void runTestMethods(Method[] methods, Method[] beforeEachMethods) {
		for(Method method: methods) {
			if(method.isAnnotationPresent(Test.class)) {
					runOneTestMethod(method, beforeEachMethods);
			}
		}
	}

	private void runMethods(Method[] methods) {
		for(Method method: methods) {
				method.setAccessible(true);
				try {
					method.invoke(testObj);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
		}
		
	}
	private void runOneTestMethod(Method method, Method[] beforeEachMethods) {
		method.setAccessible(true);
		runMethods(beforeEachMethods);
		Test testAnnotation = method.getAnnotation(Test.class);
		int nRuns = testAnnotation.nRuns();
		Instant start = Instant.now();
		IntStream.range(0, nRuns).forEach(i -> {
			try {
				method.invoke(testObj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		});
		System.out.printf("test: %s; running time: %d\n", method.getName(),
				ChronoUnit.MILLIS.between(start, Instant.now()));
	}

}