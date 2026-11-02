package Vue;

import Vue.ClicCentre;
import Modele.Sommet;
import Modele.Graphe;
import Modele.Graphe;
import Modele.Sommet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires basiques (Niveau BUT1) pour la classe ClicCentre.
 * @author eliott
 */
public class ClicCentreTest {
    
    private Graphe gr;
    private ClicCentre instance;

    public ClicCentreTest() {
    }
    
    @Before
    public void setUp() {
        // 1. Initialisation d'un graphe de test simple
        gr = new Graphe();

        // 2. Création de 3 sommets de test
        gr.ajouterSommet("C1", "M"); // Maternité
        gr.ajouterSommet("C2", "O"); // Bloc Opératoire
        gr.ajouterSommet("C3", "N"); // Centre Nutrition

        // 3. Création des arêtes (distance, fiabilité, durée, départ, arrivée)
        // Route C1 vers C2 : distance = 10, fiabilité = 5, durée = 15
        gr.ajouterArete(10, 5, 15, "C1", "C2"); 
        // Route C2 vers C3 : distance = 20, fiabilité = 8, durée = 30
        gr.ajouterArete(20, 8, 30, "C2", "C3");

        // ATTENTION : Instancier ClicCentre ici lance l'IHM. 
        // Assure-toi que ta JDialog n'est pas bloquante (modale) pendant les tests.
        // On passe 'null' pour la JFrame parent car on n'en a pas besoin ici.
        instance = new ClicCentre(null, "C2", gr); 
    }

    /**
     * Test de la méthode dist_centre.
     * En BUT1, comme cette méthode ne renvoie rien (void) et modifie une JTable privée,
     * on se contente de vérifier qu'elle s'exécute sans faire planter le programme (pas de NullPointerException).
     */
    @Test
    public void testDist_centre() {
        System.out.println("Test de dist_centre");
        try {
            instance.dist_centre("C2", gr);
            assertTrue("La méthode dist_centre s'est exécutée sans erreur", true);
        } catch (Exception e) {
            fail("La méthode a généré une exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Test de la méthode duree_centre.
     * Même logique que pour dist_centre.
     */
    @Test
    public void testDuree_centre() {
        System.out.println("Test de duree_centre");
        try {
            instance.duree_centre("C2", gr);
            assertTrue("La méthode duree_centre s'est exécutée sans erreur", true);
        } catch (Exception e) {
            fail("La méthode a généré une exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Test de duree_min_mat (Recherche de la maternité la plus proche).
     */
    @Test
    public void testDuree_min_mat() {
        System.out.println("Test de duree_min_mat");
        
        // On part du sommet C2 (Bloc Opé) et on cherche la Maternité ("M")
        Sommet centreActuel = gr.getSommet("C2");
        
        // Appel de la méthode
        ClicCentre.ResultatProche result = instance.duree_min_mat(centreActuel, gr);
        
        // Vérifications
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("La maternité la plus proche de C2 doit être C1", "C1", result.id);
        
        // On vérifie la durée brute. Dans notre setUp, la durée de C1 à C2 est de 15.
        // Le 3ème paramètre (0.01) est la marge d'erreur tolérée pour les doubles.
        assertEquals("La durée minimale attendue est 15.0", 15.0, result.valeur, 0.01);
    }

    /**
     * Test de duree_min_ope (Recherche du bloc opératoire le plus proche).
     */
    @Test
    public void testDuree_min_ope() {
        System.out.println("Test de duree_min_ope");
        
        // On part du sommet C1 (Maternité) et on cherche le Bloc Opé ("O")
        Sommet centreActuel = gr.getSommet("C1");
        
        ClicCentre.ResultatProche result = instance.duree_min_ope(centreActuel, gr);
        
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("Le bloc opératoire le plus proche de C1 doit être C2", "C2", result.id);
        assertEquals("La durée minimale attendue est 15.0", 15.0, result.valeur, 0.01);
    }

    /**
     * Test de duree_min_nut (Recherche du centre de nutrition le plus proche).
     */
    @Test
    public void testDuree_min_nut() {
        System.out.println("Test de duree_min_nut");
        
        Sommet centreActuel = gr.getSommet("C2");
        
        ClicCentre.ResultatProche result = instance.duree_min_nut(centreActuel, gr);
        
        assertNotNull("Le résultat ne doit pas être null", result);
        assertEquals("Le centre de nutrition le plus proche de C2 doit être C3", "C3", result.id);
        assertEquals("La durée minimale attendue est 30.0", 30.0, result.valeur, 0.01);
    }
}