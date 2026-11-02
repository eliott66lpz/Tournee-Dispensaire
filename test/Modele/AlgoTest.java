package Modele;

import Modele.Graphe;
import Modele.Arete;
import Modele.Algo;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eliott
 */

public class AlgoTest {

    private Graphe modeleGraphe;
    private Graph graphStream;

    @Before
    public void setUp() {
        modeleGraphe = new Graphe();
        graphStream = new SingleGraph("GrapheTest");

        modeleGraphe.ajouterSommet("S1", "M");
        Node s1 = graphStream.addNode("S1");
        s1.setAttribute("ui.style", "fill-color: #E982B0;");

    }

    private void ajouterDestination(String id, String type, int distance, int fiabilite, int duree) {
        modeleGraphe.ajouterSommet(id, type);
        Node n = graphStream.addNode(id);

        if (type.equals("M")) {
            n.setAttribute("ui.style", "fill-color: #E982B0;");
        } else if (type.equals("O")) {
            n.setAttribute("ui.style", "fill-color: #white;");
        } else if (type.equals("N")) {
            n.setAttribute("ui.style", "fill-color: #87F36C;");
        }

        modeleGraphe.ajouterArete(distance, fiabilite, duree, "S1", id);
        modeleGraphe.ajouterArete(distance, fiabilite, duree, id, "S1");

        org.graphstream.graph.Edge e = graphStream.addEdge("S1 " + id, "S1", id);
        e.setAttribute("duree", (double) duree);
        e.setAttribute("fiabilite", (double) fiabilite);
    }

    @Test
    public void testObtenirType() {
        try {
            ajouterDestination("S2", "O", 10, 5, 15);
            ajouterDestination("S3", "N", 12, 8, 20);

            Node nS1 = graphStream.getNode("S1");
            Node nS2 = graphStream.getNode("S2");
            Node nS3 = graphStream.getNode("S3");

            assertEquals("Le type pour #E982B0 doit être M", "M", Algo.obtenirType(nS1));
            assertEquals("Le type pour white doit être O", "O", Algo.obtenirType(nS2));
            assertEquals("Le type pour #87F36C doit être N", "N", Algo.obtenirType(nS3));
        } catch (Throwable t) {
            System.err.println("=== CRASH DANS testObtenirType ===");
            t.printStackTrace();
            fail("Le test a planté : " + t.getMessage());
        }
    }

    @Test
    public void testOptimiserTourneeUnCamion() {
        try {
            ajouterDestination("S3", "M", 10, 5, 20);
            ajouterDestination("S2", "M", 5, 5, 10);

            Algo.optimiserTournee(graphStream, modeleGraphe, 1, "TOUS");

            assertEquals("Le temps total de la tournée (10 vers S2, puis 30 pour rejoindre S3 via S1) doit être 40", 40.0, Algo.tempsC1, 0.0001);
            assertFalse("La tournée ne doit pas être vide", Algo.meilleureTourneeCamion1.isEmpty());
            assertEquals("Le premier noeud visité doit être le plus proche (S2)", "S2", Algo.meilleureTourneeCamion1.get(0));
        } catch (Throwable t) {
            System.err.println("=== CRASH DANS testOptimiserTourneeUnCamion ===");
            t.printStackTrace();
            fail("Le test a planté : " + t.getMessage());
        }
    }

    @Test
    public void testOptimiserTourneeDeuxCamions() {
        ajouterDestination("S2", "M", 10, 5, 15);
        ajouterDestination("S3", "M", 10, 5, 25);

        Algo.optimiserTournee(graphStream, modeleGraphe, 2, "TOUS");

        assertFalse("Le camion 1 doit avoir une tournée", Algo.meilleureTourneeCamion1.isEmpty());
        assertFalse("Le camion 2 doit avoir une tournée", Algo.meilleureTourneeCamion2.isEmpty());
        assertEquals("Le meilleur temps global doit être le MAX des deux camions", 25.0, Algo.meilleurTemps, 0.0001);
    }

    @Test
    public void testOptimiserTourneeFiltreType() {
        ajouterDestination("S2", "O", 5, 5, 10);
        ajouterDestination("S3", "M", 5, 5, 15);

        Algo.optimiserTournee(graphStream, modeleGraphe, 1, "M");

        assertTrue("S3 (Maternité) doit être visité", Algo.meilleureTourneeCamion1.contains("S3"));
        assertFalse("S2 (Bloc op) aurait dû être filtré et ignoré", Algo.meilleureTourneeCamion1.contains("S2"));
    }

    @Test
    public void testOptimiserTourneeFiltreInconnu() {
        ajouterDestination("S2", "M", 5, 5, 10);

        Algo.optimiserTournee(graphStream, modeleGraphe, 1, "INCONNU");

        assertTrue("La tournée doit être vide avec un filtre inexistant", Algo.meilleureTourneeCamion1.isEmpty());
        assertEquals("Le temps doit être de 0 si aucune destination n'est valide", 0.0, Algo.tempsC1, 0.0001);
    }

    @Test
    public void testOptimiserTourneeTropDeCamions() {
        ajouterDestination("S2", "M", 10, 5, 15);

        Algo.optimiserTournee(graphStream, modeleGraphe, 2, "TOUS");

        assertFalse("Le camion 1 doit faire la tournée", Algo.meilleureTourneeCamion1.isEmpty());
        assertTrue("Le camion 2 n'a nulle part où aller et doit rester vide", Algo.meilleureTourneeCamion2.isEmpty());
        assertEquals("Le temps du camion 2 doit être 0", 0.0, Algo.tempsC2, 0.0001);
    }

    @Test
    public void testOptimiserTourneeSommetInaccessible() {
        ajouterDestination("S2", "M", 5, 5, 10);

        modeleGraphe.ajouterSommet("S3", "M");
        Node n3 = graphStream.addNode("S3");
        n3.setAttribute("ui.style", "fill-color: #E982B0;");

        Algo.optimiserTournee(graphStream, modeleGraphe, 1, "TOUS");

        assertTrue("S2 doit être visité", Algo.meilleureTourneeCamion1.contains("S2"));
        assertFalse("S3 ne peut pas être visité car inaccessible", Algo.meilleureTourneeCamion1.contains("S3"));
    }

    @Test
    public void testCompteursTypes() {
        ajouterDestination("S2", "O", 10, 5, 15);
        ajouterDestination("S3", "N", 10, 5, 15);
        ajouterDestination("S4", "N", 10, 5, 15);

        assertEquals("Il doit y avoir 1 maternité (S1)", 1, modeleGraphe.cptM());
        assertEquals("Il doit y avoir 1 bloc opératoire (S2)", 1, modeleGraphe.cptO());
        assertEquals("Il doit y avoir 2 centres de nutrition (S3, S4)", 2, modeleGraphe.cptN());
    }

    @Test
    public void testTrouverEtGetArete() {
        ajouterDestination("S2", "M", 45, 7, 30);

        Arete a1 = modeleGraphe.trouver_art("S1", "S2");
        Arete a2 = modeleGraphe.trouver_art("S2", "S1");
        Arete uniqueSens = modeleGraphe.getArete("S1", "S2");

        assertNotNull("L'arête S1->S2 doit exister", a1);
        assertNotNull("L'arête S2->S1 doit exister", a2);
        assertNotNull("L'arête uniqueSens doit exister", uniqueSens);
        assertEquals(45.0, a1.getDistance(), 0.0001);
        assertEquals(7, a1.getFiabilite());
        assertEquals(30.0, a1.getDuree(), 0.0001);
    }

    @Test
    public void testRouteIniEtAreteFia() {
        ajouterDestination("S2", "M", 10, 2, 15);
        ajouterDestination("S3", "M", 10, 8, 15);

        int routesCritiques = modeleGraphe.routeIni(5, modeleGraphe);
        assertEquals("S1->S2 et S2->S1 ont une fiabilité de 2 (donc < 5)", 2, routesCritiques);

        modeleGraphe.arete_fia(5, graphStream);

        org.graphstream.graph.Edge edgeFaible = graphStream.getEdge("S1 S2");
        assertNotNull("L'arête graphique S1 S2 doit exister", edgeFaible);

        Object styleAttribut = edgeFaible.getAttribute("ui.style");
        assertNotNull("L'arête doit avoir un attribut de style assigné", styleAttribut);
        assertTrue("L'arête non fiable doit être rouge", styleAttribut.toString().contains("#ff0000"));
    }

    @Test
    public void testInfoSommetEtArete() {
        ajouterDestination("S2", "O", 10, 5, 15);

        String infoSommet = modeleGraphe.info_Sommet("S2");
        String infoArete = modeleGraphe.info_Arete("S1", "S2");

        assertTrue("Les infos du sommet doivent contenir le type O", infoSommet.contains("O"));
        assertTrue("Les infos de l'arête doivent contenir la distance 10", infoArete.contains("10"));
    }
}
