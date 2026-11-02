package Modele;

import Modele.Sommet;
import Modele.Graphe;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test unitaire pour Graphe.
 *
 * @author eliott
 */
public class GrapheTest {

    private Graphe instance;

    public GrapheTest() {
    }

    @Before
    public void setUp() {
        instance = new Graphe();
    }

    @Test
    public void testGetListe_sommets() {
        instance.ajouterSommet("S1", "M");
        assertEquals(1, instance.getListe_sommets().size());
        assertTrue(instance.getListe_sommets().containsKey("S1"));
    }

    @Test
    public void testAlea() {
        int nb = 10;
        Graph result = instance.Alea(nb);
        assertEquals(nb, instance.getListe_sommets().size());
        assertEquals(nb, result.getNodeCount());
    }

    @Test
    public void testInfo_Sommet() {
        instance.ajouterSommet("S1", "M");
        String result = instance.info_Sommet("S1");
        assertTrue(result.contains("Infos sur le Sommet : S1"));
        assertTrue(result.contains("Type de Sommet : M"));

        String notFound = instance.info_Sommet("S99");
        assertEquals("Sommet non trouve", notFound);
    }

    @Test
    public void testInfo_Arete() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterArete(15, 8, 20, "S1", "S2");

        String result = instance.info_Arete("S1", "S2");

        assertNotEquals("Arete non trouve", result);
        assertTrue("Devrait contenir la distance", result.contains("15"));
        assertTrue("Devrait contenir le risque/fiabilité", result.contains("8"));
        assertTrue("Devrait mentionner S1", result.contains("S1"));
        assertTrue("Devrait mentionner S2", result.contains("S2"));

        String notFound = instance.info_Arete("S1", "S99");
        assertEquals("Devrait signaler l'absence de l'arête", "Arete non trouve", notFound);
    }

    @Test
    public void testAjouterSommet() {
        instance.ajouterSommet("S1", "N");
        assertNotNull(instance.getSommet("S1"));
        assertEquals("N", instance.getSommet("S1").getType());
    }

    @Test
    public void testGetSommet() {
        instance.ajouterSommet("S1", "M");
        Sommet result = instance.getSommet("S1");
        assertNotNull(result);
        assertEquals("S1", result.getNumero());

        assertNull(instance.getSommet("S_INCONNU"));
    }

    @Test
    public void testAjouterArete() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterArete(50, 5, 45, "S1", "S2");

        assertNotNull(instance.getArete("S1", "S2"));
        assertEquals(50, instance.getArete("S1", "S2").getDistance());
    }

    @Test
    public void testGenererGraphe() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterArete(10, 5, 20, "S1", "S2");

        Graph result = instance.genererGraphe();
        assertEquals(2, result.getNodeCount());
        assertEquals(1, result.getEdgeCount());
    }

    @Test
    public void testCptM() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "M");
        instance.ajouterSommet("S3", "O");
        assertEquals(2, instance.cptM());
    }

    @Test
    public void testCptO() {
        instance.ajouterSommet("S1", "O");
        instance.ajouterSommet("S2", "M");
        assertEquals(1, instance.cptO());
    }

    @Test
    public void testCptN() {
        instance.ajouterSommet("S1", "N");
        instance.ajouterSommet("S2", "N");
        instance.ajouterSommet("S3", "N");
        assertEquals(3, instance.cptN());
    }

    @Test
    public void testArete_fia() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterArete(10, 3, 20, "S1", "S2"); // Fiabilité de 3

        Graph gr = instance.genererGraphe();
        instance.arete_fia(5, gr); // Seuil de 5

        // L'arête (fiabilité 3) est sous le seuil de 5, elle doit être colorée en rouge.
        org.graphstream.graph.Edge edge = gr.getEdge("S1 S2");
        assertNotNull(edge);
        assertTrue(edge.getAttribute("ui.style").toString().contains("#ff0000"));
    }

    @Test
    public void testTrouver_art() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterArete(10, 5, 20, "S1", "S2");

        // Test sens normal
        assertNotNull(instance.trouver_art("S1", "S2"));
        // Test sens inverse
        assertNotNull(instance.trouver_art("S2", "S1"));
        // Test inexistant
        assertNull(instance.trouver_art("S1", "S3"));
    }

    @Test
    public void testRouteIni() {
        instance.ajouterSommet("S1", "M");
        instance.ajouterSommet("S2", "O");
        instance.ajouterSommet("S3", "N");

        instance.ajouterArete(10, 2, 20, "S1", "S2"); // Fiabilité 2 (critique)
        instance.ajouterArete(15, 8, 30, "S2", "S3"); // Fiabilité 8 (ok)

        int result = instance.routeIni(5, instance);
        assertEquals(1, result); // Une seule arête a une fiabilité < 5
    }
}
