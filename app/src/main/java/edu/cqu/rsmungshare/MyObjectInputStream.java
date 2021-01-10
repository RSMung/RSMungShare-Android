package edu.cqu.rsmungshare;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class MyObjectInputStream extends ObjectInputStream {
    private static final String CLIENT_PACKAGE = "bean";
    private static final String SERVER_PACKAGE = "edu.cqu.rsmungshare.bean";

    public MyObjectInputStream(InputStream arg0) throws IOException {
        super(arg0);
    }

    @Override
    public Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            if (name.startsWith(CLIENT_PACKAGE)) {
                name = name.replace(CLIENT_PACKAGE, SERVER_PACKAGE);
                return Class.forName(name);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return super.resolveClass(desc);
    }
}
