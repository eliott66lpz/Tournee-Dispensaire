/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package Modele;

import Vue.FirstPage;
import java.awt.GraphicsEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Classe de test unitaire pour la fenêtre d'accueil FirstPage sous JUnit 4.
 * @author eliott
 */
public class FirstPageTest {

    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of initComposant method, of class FirstPage.
     * Vérifie que les composants graphiques essentiels sont bien instanciés
     * et configurés après l'appel à initComposant().
     */
    @Test
    public void testInitComposant() {
        System.out.println("initComposant");
        
        FirstPage instance = new FirstPage();
        instance.initComposant();
        assertEquals("Le titre de la JFrame est incorrect.", "Choisir le Graphe", instance.getTitle());
        assertNotNull("Le conteneur principal (ContentPane) ne devrait pas être null.", instance.getContentPane());
        instance.dispose();
    }
    
}
