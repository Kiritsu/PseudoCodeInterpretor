import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Scripting {
    private static ScriptEngineManager factory;
    private static ScriptEngine engine;

    static {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByExtension("js");
    }

    public static void modifieVariable(String nom, String valeur) {
        try {
            engine.eval(nom + "=" + valeur);
        } catch (Exception e) {
            return;
        }
    }

    public static boolean evalue(String condition) {
        try {
            return (boolean) engine.eval(condition);
        } catch (Exception e) {
            return false;
        }
    }

    public static Object calcule(String calcul) {
        try {
            return engine.eval(calcul);
        } catch (Exception e) {
            return null;
        }
    }
}
