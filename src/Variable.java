public class Variable {
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
     * Initialise une variable non constante.
     * @param nom Nom de la variable.
     * @param type Type de la variable.
     */
    public Variable(String nom, String type) {
        this.nom = nom;
        this.type = type;
        this.valeur = null;
        this.constante = false;
    }

    /**
     * Initialise une variable constante.
     * @param nom Nom de la constante.
     * @param type Type de la constante.
     * @param valeur Valeur de la constante.
     */
    public Variable(String nom, String type, String valeur) {
        this.nom = nom;
        this.type = type;
        this.valeur = valeur;
        this.constante = true;
    }

    /**
     * Modifie la valeur de la variable. Ne fonctionne uniquement si la variable n'est pas une constante.
     * @param valeur Nouvelle valeur de la variable.
     */
    public void setValeur(String valeur) {
        if (!constante) {
            this.valeur = valeur;
        }
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
}
