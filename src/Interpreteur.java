import java.util.ArrayList;
import java.util.Scanner;

public class Interpreteur {
    private Lecteur lecteur;
    private InterfaceConsole console;

    private Scanner scanner;

    private int numLigneTraitee;

    private ArrayList<Variable> variablesTracees;
    private ArrayList<String> traceExecution;

    public Interpreteur(String chemin) {
        lecteur = new Lecteur(chemin);
        console = new InterfaceConsole(lecteur, this);
        scanner = new Scanner(System.in);

        variablesTracees = new ArrayList<>();
        traceExecution = new ArrayList<>();

        numLigneTraitee = 0;
    }

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

    public void interprete() {
        String ligne = lecteur.getLignes()[numLigneTraitee];


    }

    public int getNumLigneTraitee() {
        return numLigneTraitee;
    }

    public Variable getVariableTracee(int i) {
        i--;

        if (variablesTracees.size() <= i) {
            return null;
        }

        return variablesTracees.get(i);
    }

    public String getTraceExecution() {
        StringBuilder str = new StringBuilder();

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

    public static void main(String[] args) {
        new Interpreteur("algo.txt").demarrer();
    }
}
