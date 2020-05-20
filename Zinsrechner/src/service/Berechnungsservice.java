package service;

import daos.Datenzugriffsobjekt;

/**
 * Service, dessen Funktionalitaet isoliert vom Datenzugriffsobjekt
 * getestet werden soll.
 * 
 * @author Yannis Herbig
 */
public class Berechnungsservice {
    Datenzugriffsobjekt datenzugriffsobjekt;

    public int addiereAlleZahlen(){
        int summe = 0;
        for(int x : datenzugriffsobjekt.holeAlleZahlen()){
            summe += x;
        }
        return summe;
    }

    public int multipliziereAlleZahlen(){
        int produkt = 1;
        for(int x : datenzugriffsobjekt.holeAlleZahlen()){
            produkt *= x;
        }
        return produkt;
    }
}
