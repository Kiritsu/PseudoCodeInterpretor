/**
 * Classe représentant une variable. Une variable est composée d'un nom, d'un type et d'une valeur.
 * Une variable peut être constante.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Variable {
    /**
     * Nom de la variable.
     */
    private String nom;

    /**
     * Type de la variable.
     */
    private String type;

    /**
     * Valeur de la variable.
     */
    private String valeur;

    /**
     * Indique si la variable est une constante.
     */
    private boolean constante;

    /**
     * Indique si la variable est tracée.
     */
    private boolean tracee;

    /**
     * Empêche la variable d'être manipulée.
     */
    private boolean bloquee;

    /**
     * Initialise une variable non constante.
     *
     * @param nom  Nom de la variable.
     * @param type Type de la variable.
     */
    public Variable(String nom, String type) {
        this.nom = nom;
        this.type = type;
        this.valeur = null;
        this.constante = false;
        this.bloquee = false;
        setValeurDefaut();
    }

    /**
     * Initialise une variable constante.
     *
     * @param nom    Nom de la constante.
     * @param type   Type de la constante.
     * @param valeur Valeur de la constante.
     */
    public Variable(String nom, String type, String valeur) {
        this.nom = nom;
        this.type = type;
        this.valeur = valeur;
        this.constante = true;
        this.tracee = false;
        this.bloquee = false;
        modifieVariableDansScripting();
    }

    /**
     * Initialise une variable constante par copie. Utilisation exclusive pour la trace des variables.
     *
     * @param copie Variable à copier.
     */
    public Variable(Variable copie) {
        this.nom = copie.nom;
        this.type = copie.type;
        this.valeur = copie.valeur;
        this.constante = copie.constante;
        this.tracee = copie.tracee;
        this.bloquee = true;
    }

    /**
     * Remet la valeur de cette variable par défaut.
     */
    public void setValeurDefaut() {
        switch (type) {
            case "chaîne":
            case "chaine":
                this.valeur = "\"\"";
                break;
            case "entier":
                this.valeur = "0";
                break;
            case "reel":
            case "réel":
                this.valeur = "0.0";
                break;
            case "booleen":
            case "booléen":
                this.valeur = "false";
                break;
            case "caractere":
                this.valeur = "\0";
                break;
        }

        modifieVariableDansScripting();
    }

    /**
     * Modifie la valeur de la variable. Ne fonctionne uniquement si la variable n'est pas une constante.
     *
     * @param valeur Nouvelle valeur de la variable.
     */
    public void setValeur(String valeur) {
        if (bloquee) {
            return;
        }

        if (!constante) {
            this.valeur = valeur;
            modifieVariableDansScripting();
        }
    }

    /**
     * Met à jour la valeur de la variable dans notre classe Scripting.
     *
     * @return Retourne un booléen pour bloquer la méthode jusqu'à sa terminaison.
     */
    private boolean modifieVariableDansScripting() {
        if (bloquee) {
            return false;
        }

        if (type == "booleen") {
            valeur = valeur.replace("vrai", "true").replace("faux", "false");
        } else if (type == "reel") {
            valeur = valeur.replace(",", ".");
        }

        this.valeur = Scripting.modifieVariable(nom, valeur).toString();

        return true;
    }

    /**
     * Indique si la variable courante doit être tracée. Ne fonctionne uniquement si la variable n'est pas une constante.
     */
    public void setTracee(boolean tracee) {
        if (bloquee) {
            return;
        }

        if (!constante) {
            this.tracee = tracee;
        }
    }

    /**
     * Indique si la variable est tracée.
     *
     * @return
     */
    public boolean estTracee() {
        return this.tracee;
    }

    /**
     * Retourne la valeur de la variable.
     */
    public String getValeur() {
        return this.valeur;
    }

    /**
     * Retourne le type de la variable.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Retourne le nom de la variable.
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Indique si la variable est constante.
     */
    public boolean estConstante() {
        return this.constante;
    }

    @Override
    public String toString() {
        return getNom() + " (" + getType() + ") : " + getValeur();
    }
}
