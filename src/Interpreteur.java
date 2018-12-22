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
     * Représente la liste des variables.
     */
    private ArrayList<Variable> variables;

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
        variables = new ArrayList<>();
        traceExecution = new ArrayList<>();

        numLigneTraitee = 0;

        Scripting.setInterpreteur(this);
    }

    /**
     * Démarre la gestion de l'entrée clavier.
     */
    public void demarrer() {
        creerVariables();
        demanderTracage();

        while (true) {
            if (numLigneTraitee >= lecteur.getLignes().length) {
                break;
            }

            console.actualiserConsole();
            interprete(numLigneTraitee);

            String valeur = scanner.nextLine();
            if (valeur.equals("")) {
                numLigneTraitee++;
            } else if (valeur.toLowerCase().equals("b")) {
                numLigneTraitee--;

                if (numLigneTraitee < 0) {
                    numLigneTraitee = 0;
                }

                reinitialiser(numLigneTraitee);
            } else if (valeur.toLowerCase().startsWith("l")) {
                numLigneTraitee = Integer.valueOf(valeur.substring(1)) - 1;
                reinitialiser(numLigneTraitee);
            } else if (valeur.toLowerCase().equals("q")) {
                break;
            }

            if (!valeur.equals("")) {
                traceExecution.add(valeur);
            }
        }
    }

    /**
     * Effectue une réinterprêtation du code de la première jusqu'à la ligne i.
     * @param i Ligne à laquelle nous devons nous arrêter.
     */
    public void reinitialiser(int i) {
        this.variablesTracees.clear();
        Scripting.reset();

        for (Variable v : variables) {
            v.setValeurDefaut();
        }

        for (int x = 0; x < i; x++) {
            interprete(x);
        }

        numLigneTraitee = i;
        console.actualiserConsole();
    }

    /**
     * Lis le code une première fois afin de lire les variables et constantes déclarées.
     *
     * @return Retourne un booleen pour bloquer la méthode.
     */
    public boolean creerVariables() {
        String lignes[] = lecteur.getLignes();

        for (String ligne : lignes) {
            ligne = ligne.replace("\t", "");

            if (ligne.equals("DEBUT")) {
                break;
            }

            if (ligne.matches(" *(\\w+) *: *(\\w+)")) {
                String[] vals = ligne.replace(" ", "").split(":");
                variables.add(new Variable(vals[0], vals[1]));
            } else if (ligne.replace("\t", "").matches(" *(\\w+) *<-- *(\\w+)")) {
                String[] vals = ligne.split("<--");
                variables.add(new Variable(vals[0].replace(" ", ""), determineType(vals[1].trim()), vals[1].trim()));
            }
        }

        return true;
    }

    /**
     * Détermine le type de la variable constante.
     * @param valeur Valeur donnée à une constante.
     * @return Type de la variable constante.
     */
    public String determineType(String valeur) {
        valeur = valeur.replace("\t", "");
        if (valeur.startsWith("\"") && valeur.endsWith("\"")) {
            return "chaine";
        } else if (valeur.startsWith("'") && valeur.endsWith("'")) {
            return "caractere";
        } else if (valeur.equals("vrai") || valeur.equals("vraie") || valeur.equals("faux") || valeur.equals("fausse")) {
            return "booleen";
        } else {
            try {
                Integer.parseInt(valeur.replace("\t", "").replace(" ", ""));
                return "entier";
            } catch (Exception e) {
                return "reel";
            }
        }
    }

    /**
     * Demande pour chaque variable si on souhaite qu'elle soit tracée.
     */
    public void demanderTracage() {
        for (Variable var : variables) {
            if (var.estConstante()) {
                continue;
            }

            System.out.flush();
            System.out.println("Souhaitez-vous tracer la variable : " + var.getNom() + " (" + var.getType() + ") ? [Y/n]");
            String ligne = scanner.nextLine();

            if (ligne.equals("") || ligne.toLowerCase().charAt(0) == 'y') {
                var.setTracee(true);
            }
        }
    }

    /**
     * Interprête la ligne à l'index donné.
     * @param i Ligne à interprêter.
     */
    public void interprete(int i) {
        String ligne = lecteur.getLignes()[i].replace("\t", "");

        //assignation de variable.
        if (ligne.matches(" *(\\w+) *<-- .*")) {
            String[] separation = ligne.split("<--");

            Variable v = getVariableParNom(separation[0].trim());
            if (v == null) {
                return;
            }

            ligne = ligne.replace("<--", "=");

            v.setValeur(separation[1]);

            if (v.estTracee()) {
                variablesTracees.add(new Variable(v));
                console.actualiserConsole();
            }
        }

        if (ligne.matches(" *[é\\w]+\\(.*\\)")) {
            try {
                String nomFonction = ligne.split("\\(")[0];
                switch (nomFonction) {
                    case "ecrire":
                    case "écrire":
                        String resultat = Scripting.execute(ligne.replace(",", "+")).toString();
                        traceExecution.add("ecrire() => " + resultat);
                        break;
                    case "lire":
                        System.out.println("Entrez une valeur : ");
                        String entree = scanner.nextLine();
                        Variable var = getVariableParNom(ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")")));
                        if (var == null) {
                            throw new Exception("Variable introuvable.");
                        }
                        var.setValeur(entree);
                        traceExecution.add("lire() => " + var.getValeur());

                        if (var.estTracee()) {
                            variablesTracees.add(var);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { //conditions, boucles, etc.

        }
    }

    /**
     * Retourne la variable en fonction du nom donné en paramètre.
     * @param nom Nom de la variable à chercher.
     */
    public Variable getVariableParNom(String nom) {
        for (Variable v : variables) {
            if (v.getNom().equals(nom)) {
                return v;
            }
        }

        return null;
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
