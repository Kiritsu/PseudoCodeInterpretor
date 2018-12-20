import java.lang.reflect.Method;
import java.rmi.UnexpectedException;

/**
 * Classe nous permettant d'appeler les fonctions pré-faites et sous-programmes ajoutés durant le runtime.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Fonction {
    /**
     * Empêche la classe d'être instanciée.
     */
    private Fonction() {

    }

    /**
     * Tente d'exécuter une méthode en fonction de la ligne envoyée. Ne fonctionne pas pour les méthodes imbriquées.
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
