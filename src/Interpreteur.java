import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe principale. Elle contient les variables à tracer, les traces d'exécution
 * ainsi que la gestion de l'entrée clavier.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Interpreteur {
    /**
     * Instance de notre lecteur. Nous permet de récuppérer les lignes de code nécessaires.
     */
    private Lecteur lecteur;

    /**
     * Instance de notre interface de type console.
     */
    private InterfaceConsole console;

    /**
     * Scanner nous permettant de gérer l'entrée clavier.
     */
    private Scanner scanner;

    /**
     * Indique la ligne en cours de traitement/évaluation.
     */
    private int numLigneTraitee;

    /**
     * Représente la liste des variables tracées.
     */
    private ArrayList<Variable> variablesTracees;

    /**
     * Représente la liste des traces d'exécutions sur la console.
     */
    private ArrayList<String> traceExecution;

    /**
     * Créé les différentes instances nécessaires au bon fonctionnement du programme.
     *
     * @param chemin Chemin vers le fichier à interprêter.
     */
    public Interpreteur(String chemin) {
        lecteur = new Lecteur(chemin);
        console = new InterfaceConsole(lecteur, this);
        scanner = new Scanner(System.in);

        variablesTracees = new ArrayList<>();
        traceExecution = new ArrayList<>();

        numLigneTraitee = 0;
    }

    /**
     * Démarre la gestion de l'entrée clavier.
     */
    public void demarrer() {
        while (true) {
            if (numLigneTraitee >= lecteur.getLignes().length) {
                break;
            }

            console.actualiserConsole();
            interprete();

            String valeur = scanner.nextLine();
            if (valeur.equals("")) {
                numLigneTraitee++;
            } else if (valeur.equals("b")) {
                numLigneTraitee--;

                if (numLigneTraitee < 0) {
                    numLigneTraitee = 0;
                }
            } else if (valeur.startsWith("l")) {
                numLigneTraitee = Integer.valueOf(valeur.substring(1)) - 1;
            }

            else if (valeur.equals("q")) {
                break;
            }
        }
    }

    /**
     * Interprête les lignes de code allant de la première jusqu'à celle étant actuellement traitée.
     */
    public void interprete() {
        String ligne = lecteur.getLignes()[numLigneTraitee];

    }

    /**
     * Retourne le numéro de ligne en cours de traitement.
     */
    public int getNumLigneTraitee() {
        return numLigneTraitee;
    }

    /**
     * Retourne la 'i'ème variable tracée.
     *
     * @param i Index représentant la variable tracée à retourner.
     * @return
     */
    public Variable getVariableTracee(int i) {
        i--;

        if (variablesTracees.size() <= i) {
            return null;
        }

        return variablesTracees.get(i);
    }

    /**
     * Retourne notre trace d'exécution.
     */
    public ArrayList<String> getTraceExecution() {
        return this.traceExecution;
    }

    /**
     * Initialise le programme.
     *
     * @param args Non utilisé.
     */
    public static void main(String[] args) {
        new Interpreteur("algo.txt").demarrer();
    }
}
