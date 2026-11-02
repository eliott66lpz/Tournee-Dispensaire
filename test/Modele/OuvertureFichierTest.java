/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package Modele;

import Modele.Graphe;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Classe de test unitaire pour la classe OuvertureFichier sous JUnit 4.
 * @author eliott
 */
public class OuvertureFichierTest {
    
    private File fichierTemporaire;
    
    public OuvertureFichierTest() {
    }
        
    @After
    public void tearDown() {
        if (fichierTemporaire != null && fichierTemporaire.exists()) {
            fichierTemporaire.delete();
        }
    }

    /**
     * Test of chargerAttributs method, of class OuvertureFichier.
     * Crée un faux fichier CSV à la volée, le charge, et vérifie les données lues.
     */
    @Test
    public void testChargerAttributs() {
        System.out.println("chargerAttributs");
        
        String filePath = "test_graphe_temp.csv";
        fichierTemporaire = new File(filePath);
        
        try (FileWriter fw = new FileWriter(fichierTemporaire)) {
            fw.write("// Fichier CSV de test pour JUnit\n");
            fw.write("S1;M;0\n");
            fw.write("S2;O;10,2,5\n");
            fw.flush();
        } catch (IOException e) {
            fail("Impossible de créer le fichier CSV temporaire pour le test : " + e.getMessage());
        }

        Graphe gr = new Graphe();
        OuvertureFichier instance = new OuvertureFichier();
        
        instance.chargerAttributs(filePath, gr);
        
        assertNotNull("La liste des sommets ne doit pas être nulle.", gr.getListe_sommets());
        assertTrue("Le sommet S1 aurait dû être chargé.", gr.getListe_sommets().containsKey("S1"));
        assertTrue("Le sommet S2 aurait dû être chargé.", gr.getListe_sommets().containsKey("S2"));
        
        assertEquals("Le type de S1 doit être M.", "M", gr.getListe_sommets().get("S1").getType());
        assertEquals("Le type de S2 doit être O.", "O", gr.getListe_sommets().get("S2").getType());

        assertNotNull("La liste des arêtes ne doit pas être nulle.", gr.getListe_arete());
        assertFalse("Des arêtes auraient dû être créées dans le graphe.", gr.getListe_arete().isEmpty());
    }
    
}
