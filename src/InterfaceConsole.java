import org.fusesource.jansi.AnsiConsole;

import java.util.ArrayList;

/**
 * Interface de type console. Permet de gérer l'affichage du code et des différentes traces dans la console.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class InterfaceConsole {
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static final String ANSI_BG_RED = "\u001B[41m";

    /**
     * Instance de notre lecteur. Nous permet de récuppérer les lignes de code nécessaires.
     */
    private Lecteur lecteur;

    /**
     * Instance de notre interpréteur.
     * <p>
     * Nous permet d'avoir une liaison avec les choix de l'utilisateur en entrée clavier. Nous pourrions les gérer
     * ici, mais celà nous permet de pouvoir avoir une interface graphique utilisant ses mêmes données sans pour
     * autant créer une dépendance entre les deux interfaces.
     */
    private Interpreteur interpreteur;

    /**
     * Lignes de code.
     */
    private String[] lignes;

    /**
     * Indique si l'utilisateur courant utilise le système d'exploitation windows. Cette vérification est dûe au fait
     * que la colorisation des consoles sous windows et sur linux est complètement différente. Nous utilisons donc
     * une API nommée jansi.AnsiConsole, qui corrige ce problème.
     */
    private boolean windowsUser;

    /**
     * Création de notre interface de type console et détermination du système d'exploitation utilisé.
     *
     * @param lecteur      Instance du lecteur de code.
     * @param interpreteur Instance de l'interpréteur.
     */
    public InterfaceConsole(Lecteur lecteur, Interpreteur interpreteur) {
        this.lecteur = lecteur;
        this.interpreteur = interpreteur;

        this.lecteur.lire();
        this.lignes = this.lecteur.getLignes();

        windowsUser = System.getProperty("os.name").contains("Windows");

        AnsiConsole.systemInstall();
    }

    /**
     * Méthode générant l'entièreté de l'affichage sur la console.
     */
    public void actualiserConsole() {
        StringBuilder str = new StringBuilder();

        str.append("\033[H\033[2J");

        String tirets = new String(new char[138]).replace('\0', '-');

        int numLigneTraitee = interpreteur.getNumLigneTraitee();
        int debut = numLigneTraitee - 15;
        int fin = numLigneTraitee + 15;

        if (debut < 0) {
            debut = 0;
            fin = 30;
        }

        if (fin > lignes.length) {
            fin = lignes.length;
            debut = fin - 30 < 0 ? 0 : debut;
        }

        if (numLigneTraitee > lignes.length - 15) {
            fin = lignes.length;
            debut = fin - 30;

            if (debut < 0) {
                debut = 0;
                fin = 30;
            }
        }

        //Affichage de l'en-tête
        str.append("+" + tirets + "+\n");
        str.append(String.format("|      | Code %80s | Trace des variables %21s |\n", " ", " "));
        str.append("+" + tirets + "+\n");

        //Affichage du code
        for (int i = debut; i < fin; i++) {
            String ligne;
            if (i < lignes.length) {
                ligne = lignes[i];
            } else {
                ligne = " ";
            }

            ligne = ligne.replace("\t", "        ");

            if (i == numLigneTraitee) {
                str.append(String.format(ANSI_BG_RED + "|  %02d  | %-85s |" + ANSI_RESET + " %-41s |\n", i + 1, ligne, getTraceVariable(i)));
            } else {
                ligne = colorie(String.format("%-85s", ligne));
                str.append(String.format("|  %02d  | %-85s | %-41s |" + "\n", i + 1, ligne, getTraceVariable(i)));
            }
        }

        str.append("+" + tirets + "+\n");
        str.append("\n\n");

        str.append("+" + tirets + "+\n");
        str.append(String.format("| Console %128s |\n", " "));
        str.append("+" + tirets + "+\n");
        str.append(String.format("| " + ANSI_YELLOW + " %-134s " + ANSI_RESET + " |\n", getTraceExecution(0)));
        str.append(String.format("| " + ANSI_YELLOW + " %-134s " + ANSI_RESET + " |\n", getTraceExecution(1)));
        str.append(String.format("| " + ANSI_YELLOW + " %-134s " + ANSI_RESET + " |\n", getTraceExecution(2)));
        str.append("+" + tirets + "+\n");

        if (windowsUser) {
            AnsiConsole.out.println(str);
        } else {
            System.out.println(str);
        }
    }

    /**
     * Colorie la ligne donnée en paramètre en fonction de ses mots clef.
     *
     * @param ligne Ligne à colorier.
     * @return La ligne coloriée.
     */
    public String colorie(String ligne) {
        String commentaire = "";
        if (ligne.matches(".*//.*")) {
            commentaire = ligne.substring(ligne.indexOf("//"));
            ligne = ligne.substring(0, ligne.indexOf("//"));
        }

        commentaire = ANSI_GREEN + commentaire + ANSI_RESET;

        String copie = ligne;

        try {
            copie = ligne.replaceAll("(\".*\")", ANSI_BLUE + "$1" + ANSI_RESET);
        } catch (Exception e) {

        }

        try {
            ligne = ligne.replaceAll("([é\\w]+[\\s]*)\\(", ANSI_YELLOW + "$1" + ANSI_RESET + "(");
        } catch (Exception e) {

        }

        ligne = reformer(copie, ligne);

        return ligne + commentaire;
    }

    /**
     * Reforme correctement les couleurs de la chaîne afin de gérer les fonctions imbriquées
     * en ignorant celles se trouvant à l'intérieur de guillemets.
     *
     * @param copie Copie de notre chaîne, la couleur pour les guillemets y est appliquée.
     * @param base  Base de notre chaîne, la couleur pour les fonctions y est appliquée.
     * @return Un mélange de la base et de la copie, avec les couleur comme il le faut.
     */
    public String reformer(String copie, String base) {
        String ret = "";

        String[] copies = copie.split("\"");
        String[] bases = base.split("\"");

        for (int i = 0; i < copies.length; i++) {
            if (i % 2 != 0) {
                ret += ANSI_BLUE + "\"" + copies[i] + "\"" + ANSI_RESET;
            } else {
                ret += bases[i];
            }
        }

        return ret;
    }

    /**
     * Retourne la trace des variables en fonction de la ligne traitée. Cette méthode permet de correctement
     * formater l'affichage dans notre console.
     *
     * @param i Ligne en train d'être traitée.
     * @return Le formattage de cette variable tracée.
     */
    public String getTraceVariable(int i) {
        if (i == 0) {
            return "    NOM     |    TYPE    |     VALEUR    ";
        } else {
            Variable v = interpreteur.getVariableTracee(i);
            if (v == null) {
                return String.format("%-11s | %-10s | %-13s ", " ", " ", " ");
            }

            return String.format("%-11s | %-10s | %-13s ", v.getNom(), v.getType(), v.getValeur());
        }
    }

    /**
     * Retourne les trois dernières valeurs formattées de la trace d'exécution.
     *
     * @param i 'i'ème dernière trace d'exécution.
     */
    public String getTraceExecution(int i) {
        ArrayList<String> traceExecution = interpreteur.getTraceExecution();

        if (traceExecution.size() <= i) {
            return "";
        }

        if (traceExecution.size() >= 3) {
            return traceExecution.get(traceExecution.size() - (3 - i));
        } else {
            return traceExecution.get(i);
        }
    }
}