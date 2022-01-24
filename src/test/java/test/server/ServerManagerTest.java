package test.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerManagerTest {


    static boolean first() {
        System.out.println("This is executing first?");
        return false;
    }

    static boolean sec() {
        System.out.println("This maybe not be executing");
        return true;
    }

    public static void main(String[] args) {
        Logger a = LoggerFactory.getLogger("ServerManagerTest");
        a.info("Hola");
        a.error("This is an error");
        a.debug("This is a debug message");
        a.warn("This is a warning message");
        if (first() && sec())
            System.out.println("finnish bad :O");
        else
            System.out.println("finnish good :)");
    }
}
