package daos;

/**
 * Data-Layer-Abhaengigkeit, die gemockt werden soll.
 * Dies koennte auch zum Beispiel ein Datenbank-Abfrage-Objekt sein.
 * 
 * @author Yannis Herbig
 */
public class Datenzugriffsobjekt {

    public int[] holeAlleZahlen(){
        return new int[] {1, 2, 3, 4, 5};
    }
}
