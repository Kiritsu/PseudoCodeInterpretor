import bsh.Interpreter;

/**
 * Utilisation simple de l'API de scripting de javax.script. Cette classe nous permet d'effectuer des calculs,
 * de vérifier des conditions, tout en utilisant des variables et non forcément des valeurs.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.1 2018-12-20
 */
public final class Scripting {
    /**
     * Interpreteur de code Java.
     */
    private static Interpreter interpreter;

    /**
     * Notre interpreteur.
     */
    private static Interpreteur interpreteur;

    /**
     * Constructeur static afin d'instancier notre interpreteur au début du runtime.
     */
    static {
        interpreter = new Interpreter();
        creerFonctions();
    }

    /**
     * Constructeur privé pour empêcher toute instanciation de la classe.
     */
    private Scripting() {

    }

    /**
     * Réinitialise l'interpreteur BeanShell.
     */
    public static void reset() {
        interpreter = new Interpreter();
        creerFonctions();
    }

    /**
     * Méthode ajoutant toutes les différentes fonctions utiles.
     */
    public static void creerFonctions() {
        try {
            interpreter.eval("public static int hasard(int i) { return (int)(Math.random() * i); }");
            interpreter.eval("public static boolean estEntier(String entier) { try { Integer.parseInt(entier); return true; } catch (Exception e) { return false; } }");
            interpreter.eval("public static char car(String entier) { int val = Integer.valueOf(entier); return (char) val; }");
            interpreter.eval("public static int ord(String car) { return (int) car.charAt(0); }");
            interpreter.eval("public static String enChaine(String entier) { return entier; }");
            interpreter.eval("public static int enEntier(String chaine) { return Integer.valueOf(chaine); }");
            interpreter.eval("public static double enReel(String chaine) { return Double.valueOf(chaine); }");
            interpreter.eval("public static int plafond(String reel) { double val = Double.valueOf(reel); return ((int) val) + 1; }");
            interpreter.eval("public static int plancher(String reel) { double val = Double.valueOf(reel); return (int) val; }");
            interpreter.eval("public static int arrondi(String reelBase) { double reel = Double.valueOf(reelBase); if (reel - ((int) reel) > 0.5) { return plafond(reelBase); } return plancher(reelBase); }");
            interpreter.eval("public static String aujourdhui() { SimpleDateFormat formater = new SimpleDateFormat(\"dd/MM/yyyy\"); return formater.format(new Date()); }");
            interpreter.eval("public static String jour(String date) { return date.split(\"/\")[0]; }");
            interpreter.eval("public static String mois(String date) { return date.split(\"/\")[1]; }");
            interpreter.eval("public static String annee(String date) { return date.split(\"/\")[2]; }");
            interpreter.eval("public static boolean estReel(String reel) { try { Double.parseDouble(reel.replace(\",\", \".\")); return true; } catch (Exception e) { return false; } }");
            interpreter.eval("public static String ecrire(Object message) { return message.toString(); }");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change l'instance de notre interprêteur dans cette classe.
     *
     * @param i Instance de l'Interpreteur.
     */
    public static void setInterpreteur(Interpreteur i) {
        interpreteur = i;
    }

    /**
     * Modifie et retourne la valeur d'une variable.
     *
     * @param nom    Nom de la variable à modifier.
     * @param valeur Nouvelle valeur de cette variable.
     */
    public static Object modifieVariable(String nom, String valeur) {
        try {
            if (valeur.length() == 0) {
                valeur = "\"\"";
            }

            interpreter.eval(nom + "=" + valeur);
            return (Object) interpreter.eval(nom);
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
            return (boolean) interpreter.eval(condition);
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
            return interpreter.eval(calcul);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tente d'exécuter une méthode en fonction de la ligne envoyée. Ne fonctionne pas pour les méthodes imbriquées.
     *
     * @param ligne Ligne à examiner pour l'exécuter.
     * @return Valeur de la méthode.
     */
    public static Object execute(String ligne) {
        ligne = nettoie(ligne);

        try {
            return interpreter.eval(ligne);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Nettoie un message de ses caractères spéciaux et accents.
     *
     * @param message Message à nettoyer.
     * @return Le message nettoyé.
     */
    private static String nettoie(String message) {
        return message.replace("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e")
                .replace("à", "a").replace("â", "a").replace("ä", "a")
                .replace("ì", "i").replace("î", "i").replace("ï", "i")
                .replace("ò", "o").replace("ô", "o").replace("ö", "o")
                .replace("'", "");
    }
}
