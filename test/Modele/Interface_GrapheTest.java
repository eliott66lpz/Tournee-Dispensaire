package Modele;

import Vue.Interface_Graphe;
import java.awt.GraphicsEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test pour Interface_Graphe.
 * Adaptée aux concepts de BUT1 : vérification des états, des retours de méthodes 
 * et gestion basique du cycle de vie de l'IHM dans les tests.
 *
 * @author eliott
 */
public class Interface_GrapheTest {
    
    private Interface_Graphe instance;
    
    public Interface_GrapheTest() {
    }
    
    @Before
    public void setUp() {
        // Sécurité : évite de lancer l'IHM si le test tourne sur un serveur sans écran (CI/CD)
        if (!GraphicsEnvironment.isHeadless()) {
            // On initialise une fenêtre avec un graphe aléatoire de 5 sommets pour nos tests
            instance = new Interface_Graphe(false, 5, null);
        }
    }
    
    @After
    public void tearDown() {
        // Bonne pratique : on détruit la fenêtre après chaque test pour libérer la mémoire 
        // et ne pas accumuler des fenêtres ouvertes à l'écran.
        if (instance != null) {
            instance.dispose();
        }
    }

    /**
     * Test de la méthode initComposants.
     * En BUT1, on ne teste généralement pas le placement des boutons. On s'assure 
     * simplement que l'IHM se construit sans lever d'exceptions (Crash test).
     */
    @Test
    public void testInitComposants() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        System.out.println("initComposants - Crash Test");
        
        // On vérifie que l'instance a bien été créée
        assertNotNull("L'instance de l'interface graphique ne devrait pas être null", instance);
        
        // On vérifie que le constructeur a bien rendu la fenêtre visible
        assertTrue("La fenêtre principale doit être visible après son instanciation", instance.isVisible());
        assertEquals("Le titre de la fenêtre n'est pas le bon", "SAE IHM - Centre de Commandement", instance.getTitle());
    }

    /**
     * Test de la méthode getTypeClic avec un sommet qui N'EXISTE PAS.
     */
    @Test
    public void testGetTypeClicInexistant() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        System.out.println("getTypeClic - Sommet Inexistant");
        
        // On teste avec un ID qui a très peu de chances d'exister dans un graphe de 5 sommets
        String result = instance.getTypeClic("S999");
        
        // La méthode doit retourner null si le sommet n'est pas trouvé
        assertNull("Le type d'un sommet inexistant doit retourner null", result);
    }
    
    /**
     * Test de la méthode getTypeClic avec un sommet qui EXISTE.
     */
    @Test
    public void testGetTypeClicExistant() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        System.out.println("getTypeClic - Sommet Existant");
        
        // Dans votre logique (ex: colorerTournee), le sommet de départ s'appelle souvent "S1".
        // On suppose que la génération aléatoire de 5 sommets a créé au moins un sommet "S1".
        String result = instance.getTypeClic("S1");
        
        // On vérifie qu'on a bien récupéré quelque chose
        assertNotNull("Le sommet S1 devrait exister et retourner une valeur", result);
        
        // On vérifie que la valeur retournée correspond bien à l'un des types autorisés du sujet
        boolean typeValide = result.equals("M") || result.equals("O") || result.equals("N");
        assertTrue("Le type du sommet doit obligatoirement être M, O ou N", typeValide);
    }
}