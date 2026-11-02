/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Modele;

import Modele.Sommet;
import Modele.Graphe;
import Modele.Dijkstra;
import Modele.Arete;
import java.awt.Color;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eliott
 */
public class DijkstraTest {
    
    private Graphe gr;
    
    public DijkstraTest() {
    }   
    
    @Before
    public void setUp() {
        gr = new Graphe();

        Sommet sA = new Sommet("A", "M");
        Sommet sB = new Sommet("B", "O");
        Sommet sC = new Sommet("C", "N");
        Sommet sD = new Sommet("D", "M");

        gr.getListe_sommets().put("A", sA);
        gr.getListe_sommets().put("B", sB);
        gr.getListe_sommets().put("C", sC);
        gr.getListe_sommets().put("D", sD);

        Arete artAB = new Arete(10, 1, 5, sA, sB); 
        
        Arete artAC = new Arete(30, 15, 10, sA, sC); 
        Arete artCB = new Arete(15, 5, 12, sC, sB); 

        gr.getListe_arete().put("AB", artAB);
        gr.getListe_arete().put("AC", artAC);
        gr.getListe_arete().put("CB", artCB);
    }

    /**
     * Test of dijkstraGraphe method, of class Dijkstra.
     */
    @Test
    public void testDijkstraGraphe() {
        System.out.println("dijkstraGraphe");
        
        String depart = "A";
        String arrivee = "B";
        boolean useDuree = false;
        double expResult = 10.0;
        double result = Dijkstra.dijkstraGraphe(depart, arrivee, gr, useDuree);
        assertEquals(expResult, result, 0.001);

        useDuree = true;
        expResult = 5.0;
        result = Dijkstra.dijkstraGraphe(depart, arrivee, gr, useDuree);
        assertEquals(expResult, result, 0.001);
        
        assertEquals(Double.MAX_VALUE, Dijkstra.dijkstraGraphe("A", "D", gr, useDuree), 0.001);
    }

    /**
     * Test of dijkstraGrapheEstimee method, of class Dijkstra.
     */
    @Test
    public void testDijkstraGrapheEstimee() {
        System.out.println("dijkstraGrapheEstimee");
        
        String depart = "A";
        String arrivee = "B";
        double expResult = 23.0;
        double result = Dijkstra.dijkstraGrapheEstimee(depart, arrivee, gr);
        assertEquals(expResult, result, 0.001);
        
        assertEquals(Double.MAX_VALUE, Dijkstra.dijkstraGrapheEstimee("A", "D", gr), 0.001);
    }

    /**
     * Test of dijkstraGrapheEstimeeColor method, of class Dijkstra.
     */
    @Test
    public void testDijkstraGrapheEstimeeColor() {
        System.out.println("dijkstraGrapheEstimeeColor");
        
        String depart = "A";
        String arrivee = "B";
        double expResult = 22.0;
        double result = Dijkstra.dijkstraGrapheEstimeeColor(depart, arrivee, gr);
        assertEquals(expResult, result, 0.001);

        Arete ac = gr.getListe_arete().get("AC");
        Arete cb = gr.getListe_arete().get("CB");
        Color jauneAttendu = new Color(255, 236, 0);
        
        assertNotNull(ac);
        assertNotNull(cb);
        assertEquals(jauneAttendu, ac.getCouleur());
        assertEquals(jauneAttendu, cb.getCouleur());
        
        assertEquals(Double.MAX_VALUE, Dijkstra.dijkstraGrapheEstimeeColor("A", "D", gr), 0.001);
    }
    
}