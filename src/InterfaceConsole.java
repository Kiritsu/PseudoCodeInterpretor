import org.fusesource.jansi.AnsiConsole;

import java.util.ArrayList;

/**
 * Interface de type console. Permet de gérer l'affichage du code et des différentes traces dans la console.
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public class InterfaceConsole {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BG_RED = "\u001B[41m";
    private static final String ANSI_BG = "\u001B[43m";

    /**
     * Instance de notre lecteur. Nous permet de récuppérer les lignes de code nécessaires.
     */
    private Lecteur lecteur;

    /**
     * Instance de notre interpréteur.
     *
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
     * @param lecteur Instance du lecteur de code.
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

        String tirets = new String(new char[150]).replace('\0', '-');

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
        }

        //Affichage de l'en-tête
        str.append("+" + tirets + "+\n");
        str.append(String.format("|      | Code %92s | Trace des variables %21s |\n", " ", " "));
        str.append("+" + tirets + "+\n");

        //Affichage du code
        for (int i = debut; i < fin; i++) {
            if (i == numLigneTraitee) {
                str.append(String.format(ANSI_BG_RED + "|  %02d  | %-97s |" + ANSI_RESET + " %-41s |\n",
                        i + 1, lignes[i].replace("\t", "    "), getTraceVariable(i)));
            } else {
                str.append(String.format("|  %02d  | %-97s | %-41s |" + "\n", i + 1,
                        lignes[i].replace("\t", "    "), getTraceVariable(i)));
            }
        }

        str.append("+" + tirets + "+\n");
        str.append("\n\n");

        str.append("+" + tirets + "+\n");
        str.append(String.format("| Console %140s |\n", " "));
        str.append("+" + tirets + "+\n");
        str.append(String.format("%-80s\n", getTraceExecution()));
        str.append("+" + tirets + "+\n");

        if (windowsUser) {
            AnsiConsole.out.println(str);
        } else {
            System.out.println(str);
        }
    }

    /**
     * Retourne la trace des variables en fonction de la ligne traitée. Cette méthode permet de correctement
     * formater l'affichage dans notre console.
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
     */
    public String getTraceExecution() {
        StringBuilder str = new StringBuilder();

        ArrayList<String> traceExecution = interpreteur.getTraceExecution();

        String x;
        if (traceExecution.size() >= 3) {
            x = traceExecution.get(traceExecution.size() - 3);
            str.append(String.format("| %-149s|\n", x));
        }

        if (traceExecution.size() >= 2) {
            x = traceExecution.get(traceExecution.size() - 2);
            str.append(String.format("| %-149s|\n", x));
        }

        if (traceExecution.size() >= 1) {
            x = traceExecution.get(traceExecution.size() - 1);
            str.append(String.format("| %-149s|\n", x));
        }

        return str.toString();
    }
}