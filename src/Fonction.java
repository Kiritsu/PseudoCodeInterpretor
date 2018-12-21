import java.lang.reflect.Method;
import java.rmi.UnexpectedException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe nous permettant d'appeler les fonctions pré-faites et sous-programmes ajoutés durant le runtime.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Fonction {
    private static final char concateneur = '©';

    /**
     * Empêche la classe d'être instanciée.
     */
    private Fonction() {

    }

    /**
     * Tente d'exécuter une méthode en fonction de la ligne envoyée. Ne fonctionne pas pour les méthodes imbriquées.
     *
     * @param ligne Ligne à examiner pour l'exécuter.
     * @return Valeur de la méthode.
     * @throws UnexpectedException Lorsque la méthode n'a pas pu être trouvée.
     */
    public static Object execute(String ligne) throws UnexpectedException {
        ligne = ligne.substring(0, ligne.indexOf("//"));

        String nomMethode = ligne.substring(0, ligne.indexOf("("));
        String parametres = ligne.substring(ligne.indexOf("(") + 1, ligne.lastIndexOf(")"));

        nomMethode = nettoie(nomMethode);

        try {
            if (parametres.length() != 0) {
                Method methode = Fonction.class.getMethod(nomMethode, String.class);
                return methode.invoke(Fonction.class, parametres);
            } else {
                Method methode = Fonction.class.getMethod(nomMethode);
                return methode.invoke(Fonction.class);
            }
        } catch (Exception e) {
            throw new UnexpectedException("La méthode '" + nomMethode + "' n'est pas implémentée.");
        }
    }

    /**
     * Tente d'exécuter une méthode en fonction de la ligne envoyée.
     *
     * @param ligne Ligne à examiner pour l'exécuter.
     * @return Valeur de la méthode.
     * @throws UnexpectedException Lorsque la ou les méthodes n'ont pas pu être trouvées.
     */
    public static Object executeRecursivement(String ligne) throws UnexpectedException {
        String[] methodes = ligne.split("\\)[ ]*\\" + concateneur + "[ ]*\\(");
        if (methodes.length > 1) {
            return executeRecursivement(methodes[0]).toString() + executeRecursivement(ligne.substring(0, methodes[0].length()));
        } else {
            return execute(methodes[0]); //todo: méthode imbriquée.
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

    /**
     * Retourne intelligemment en fonction de la concaténation
     *
     * @param message
     * @return
     */
    public static String ecrire(String message) {
        String[] params = message.split("\\" + concateneur);
        String str = "";
        for (String s : params) {
            s = s.substring(0, s.indexOf("\"")).replace(" ", "") + s.substring(s.indexOf("\"") + 1);
            s = s.substring(0, s.lastIndexOf("\"")) + s.substring(s.lastIndexOf("\"") + 1).replace(" ", "");
            str += s;
        }

        return str;
    }

    /**
     * Retourne le caractère correspondant à l'entier passé en paramètre.
     *
     * @param entier Entier à convertir.
     */
    public static char car(String entier) {
        int val = Integer.valueOf(entier);
        return (char) val;
    }

    /**
     * Retourne l'entier correspondant au caractère passé en paramètre.
     *
     * @param car Caractère à convertir.
     */
    public static int ord(String car) {
        return (int) car.charAt(0);
    }

    /**
     * Transforme n'importe quelle chose passée en paramètre, en string.
     *
     * @param entier Chose à convertir en string.
     */
    public static String enChaine(String entier) {
        return entier;
    }

    /**
     * Convertie la chaîne en entier.
     *
     * @param chaine Chaîne à convertir en entier.
     */
    public static int enEntier(String chaine) {
        return Integer.valueOf(chaine);
    }

    /**
     * Convertie la chaîne en réel.
     *
     * @param chaine Chaîne à convertir en réel.
     */
    public static double enReel(String chaine) {
        return Double.valueOf(chaine);
    }

    /**
     * Arrondie à l'entier supérieur le réel passé en paramètre.
     *
     * @param reel Réel à arrondir.
     */
    public static int plafond(String reel) {
        double val = Double.valueOf(reel);
        return ((int) val) + 1;
    }

    /**
     * Arrondie à l'entier inférieur le réel passé en paramètre.
     *
     * @param reel Réel à arrondir.
     */
    public static int plancher(String reel) {
        double val = Double.valueOf(reel);
        return (int) val;
    }

    /**
     * Arrondie à l'entier supérieur ou inférieur le réel passé en paramètre en fonction de sa valeur.
     *
     * @param reelBase Réel à arrondir.
     */
    public static int arrondi(String reelBase) {
        double reel = Double.valueOf(reelBase);

        if (reel - ((int) reel) > 0.5) {
            return plafond(reelBase);
        }

        return plancher(reelBase);
    }

    /**
     * Retourne la date d'aujourd'hui au format dd/MM/yyyy.
     */
    public static String aujourdhui() {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        return formater.format(new Date());
    }

    /**
     * Retourne le jour correspondant à la date au format dd/MM/yyyy passée en paramètre.
     *
     * @param date Date à analyser..
     */
    public static String jour(String date) {
        return date.split("\\/")[0];
    }

    /**
     * Retourne le mois correspondant à la date au format dd/MM/yyyy passée en paramètre.
     *
     * @param date Date à analyser..
     */
    public static String mois(String date) {
        return date.split("\\/")[1];
    }

    /**
     * Retourne l'année correspondante à la date au format dd/MM/yyyy passée en paramètre.
     *
     * @param date Date à analyser..
     */
    public static String annee(String date) {
        return date.split("\\/")[2];
    }

    /**
     * Vérifie que la valeur passée en paramètre est un réel.
     *
     * @param reel Valeur à vérifier.
     */
    public static boolean estReel(String reel) {
        try {
            Double.parseDouble(reel.replace(",", "."));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie que la valeur passée en paramètre est un entier.
     *
     * @param entier Valeur à vérifier.
     */
    public static boolean estEntier(String entier) {
        try {
            Integer.parseInt(entier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retourne un nombre au hasard compris entre 0 et 'entier'
     *
     * @param entier Valeur maximum (exclue)
     */
    public static int hasard(String entier) {
        return (int) (Math.random() * Integer.valueOf(entier));
    }
}
