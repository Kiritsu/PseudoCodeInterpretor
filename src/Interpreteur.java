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
            interprete(numLigneTraitee, false);

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

        int x;
        for (x = 0; x < i; x++) {
            numLigneTraitee = x;
            if (!interprete(x, true)) {
                break;
            }
        }

        numLigneTraitee = x;
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
    public boolean interprete(int i, boolean reset) {
        String ligne = lecteur.getLignes()[i].replace("\t", "");
        console.actualiserConsole();

        //assignation de variable.
        if (ligne.matches(" *(\\w+) *<-- .*")) {
            String[] separation = ligne.split("<--");

            Variable v = getVariableParNom(separation[0].trim());
            if (v == null) {
                return false;
            }

            v.setValeur(separation[1]);

            if (v.estTracee()) {
                variablesTracees.add(new Variable(v));
            }
        } else if (ligne.matches(" *[é\\w]+\\(.*\\)")) {
            try {
                String nomFonction = ligne.split("\\(")[0];
                switch (nomFonction) {
                    case "ecrire":
                    case "écrire":
                        String resultat = Scripting.execute(ligne.replace(",", "+")).toString();
                        traceExecution.add("ecrire() => " + resultat);
                        break;
                    case "lire":
                        Variable var = getVariableParNom(ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")")));
                        if (var == null) {
                            throw new Exception("Variable introuvable.");
                        }

                        System.out.println("Entrez une valeur pour la variable " + var.getNom() + " de type " + var.getType() + " : ");
                        String entree = scanner.nextLine();
                        
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
        } else if (ligne.contains("si ") && ligne.endsWith(" alors")) { //condition si alors
            if (reset) {
                return false;
            }

            String condition = ligne.substring(ligne.indexOf("si ") + 3, ligne.lastIndexOf(" alors")).replace(" ou ", "||").replace(" et ", "&&");

            if (Scripting.evalue(condition)) {
                String contenu;
                do {
                    numLigneTraitee++;
                    interprete(numLigneTraitee, false);

                    console.actualiserConsole();
                    scanner.nextLine();

                    contenu = lecteur.getLignes()[numLigneTraitee].replace("\t", "");
                } while (!contenu.equals("sinon") && !contenu.equals("fsi"));

                if (contenu.equals("sinon")) {
                    do {
                        numLigneTraitee++;

                        console.actualiserConsole();

                        contenu = lecteur.getLignes()[numLigneTraitee].replace("\t", "");
                    } while(!contenu.equals("fsi"));
                }

                console.actualiserConsole();
            } else {
                String contenu;
                do {
                    numLigneTraitee++;
                    contenu = lecteur.getLignes()[numLigneTraitee].replace("\t", "");
                    console.actualiserConsole();
                } while (!contenu.equals("sinon"));
            }
        } else if (ligne.contains("tant que ") && ligne.endsWith(" faire")) {
            String condition = ligne.substring(ligne.indexOf("tant que ") + 9, ligne.lastIndexOf(" faire")).replace(" ou ", "||").replace(" et ", "&&");
            int baseLigne = numLigneTraitee;
            while (Scripting.evalue(condition)) {
                do {
                    numLigneTraitee++;
                    interprete(numLigneTraitee, false);
                    console.actualiserConsole();
                    scanner.nextLine();
                } while (!lecteur.getLignes()[numLigneTraitee].contains("ftq"));

                numLigneTraitee = baseLigne;
            }

            do {
                numLigneTraitee++;
            } while (!lecteur.getLignes()[numLigneTraitee].contains("ftq"));
        }

        console.actualiserConsole();
        return true;
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
