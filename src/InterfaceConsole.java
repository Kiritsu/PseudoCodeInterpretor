import org.fusesource.jansi.AnsiConsole;

public class InterfaceConsole {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BG_RED = "\u001B[41m";
    private static final String ANSI_BG = "\u001B[43m";

    private Lecteur lecteur;

    private Interpreteur interpreteur;

    private String[] lignes;

    private boolean windowsUser;

    public InterfaceConsole(Lecteur lecteur, Interpreteur interpreteur) {
        this.lecteur = lecteur;
        this.interpreteur = interpreteur;

        this.lecteur.lire();
        this.lignes = this.lecteur.getLignes();

        windowsUser = System.getProperty("os.name").contains("Windows");

        AnsiConsole.systemInstall();
    }

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
                str.append(String.format(ANSI_BG_RED + "|  %02d  | %-97s |" + ANSI_RESET + " %-41s |\n", i + 1, lignes[i].replace("\t", "    "), getTraceVariable(i)));
            } else {
                str.append(String.format("|  %02d  | %-97s | %-41s |" + "\n", i + 1, lignes[i].replace("\t", "    "), getTraceVariable(i)));
            }
        }

        str.append("+" + tirets + "+\n");
        str.append("\n\n");

        str.append("+" + tirets + "+\n");
        str.append(String.format("| Console %140s |\n", " "));
        str.append("+" + tirets + "+\n");
        str.append(String.format("%-80s\n", interpreteur.getTraceExecution()));
        str.append("+" + tirets + "+\n");

        if (windowsUser) {
            AnsiConsole.out.println(str);
        } else {
            System.out.println(str);
        }
    }

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
}