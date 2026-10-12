package org.rebecalang.transparentactormodelchecker.abstractrebeca.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public final class ObjectSizeCalculator {

    private ObjectSizeCalculator() {
    }

    public static long sizeOf(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			String s = baos.toString();
			return s.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;

    }
}
