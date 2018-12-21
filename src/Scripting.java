import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Utilisation simple de l'API de scripting de javax.script. Cette classe nous permet d'effectuer des calculs,
 * de vérifier des conditions, tout en utilisant des variables et non forcément des valeurs.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Scripting {
    /**
     * Manager permettant de créer le moteur de scripting pour le langage JavaScript.
     */
    private static ScriptEngineManager factory;

    /**
     * Moteur de scripting pour le langage JavaScript.
     */
    private static ScriptEngine engine;

    /**
     * Constructeur static afin d'instancier notre manager et notre moteur.
     */
    static {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByExtension("js");
    }

    /**
     * Empêche la classe d'être instanciée.
     */
    private Scripting() {

    }

    public static void reset() {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByExtension("js");
    }

    /**
     * Modifie et retourne la valeur d'une variable.
     *
     * @param nom Nom de la variable à modifier.
     * @param valeur Nouvelle valeur de cette variable.
     */
    public static Object modifieVariable(String nom, String valeur) {
        try {
            if (valeur.length() == 0) {
                valeur = "\"\"";
            }

            engine.eval(nom + "=" + valeur);
            return (Object) engine.eval(nom);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Évalue la condition donnée et retourne un booléen en fonction de son résultat.
     *
     * @param condition Condition à évaluer.
     */
    public static boolean evalue(String condition) {
        try {
            return (boolean) engine.eval(condition);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Effectue un calcul et retourne son résultat. Si celui utilise des variables et qu'elles sont modifiées,
     * le changement s'appliquera comme si la méthode Scripting.modifieVariable avait été appelée.
     *
     * @param calcul Calcul à effectuer.
     */
    public static Object calcule(String calcul) {
        try {
            return engine.eval(calcul);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
