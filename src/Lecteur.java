import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Lecteur personnalisé de fichier.
 *
 * @author Allan Mercou, Adrien Guey, Gauthier Salas, Remi Schneider
 * @version 1.0 2018-12-20
 */
public final class Lecteur {
    /**
     * Chemin vers le fichier à lire.
     */
    private String chemin;

    /**
     * Lignes du fichier précédemment lu.
     */
    private ArrayList<String> lignes;

    /**
     * Contenu du fichier précédemment lu.
     */
    private String contenu;

    /**
     * Indique si le fichier a déjà été lu.
     */
    private boolean lu;

    /**
     * Création d'un Lecteur à partir du fichier spécifié.
     *
     * @param chemin Chemin vers le fichier.
     */
    public Lecteur(String chemin) {
        this.chemin = chemin;
        this.lu = false;
        this.lignes = new ArrayList<>();
    }

    /**
     * Retourne les lignes du fichier sous la forme d'un tableau de String.
     */
    public String[] getLignes() {
        String[] lignes = new String[this.lignes.size()];
        return this.lignes.toArray(lignes);
    }

    /**
     * Lis le fichier afin de récuppérer l'entièreté de son contenu.
     */
    public void lire() {
        if (lu) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(chemin));

            String ligne;
            while ((ligne = br.readLine()) != null) {
                lignes.add(ligne);
                contenu += ligne + "\n";
            }

            br.close();
            lu = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
