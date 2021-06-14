package io.github.nicolasdesnoust.marslander.core;

import java.io.InputStream;
import java.util.function.Function;

public class TestCaseParser {
	private TestCaseParser() {
	}

	public static <T> T parseFileContent(String fileName, Function<InputStream, T> doneCallback) {
		ClassLoader classLoader = TestCaseParser.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		if (inputStream == null) {
			throw new IllegalArgumentException("File " + fileName + " not found!");
		} else {
			return doneCallback.apply(inputStream);
		}
	}
}