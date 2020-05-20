package darlehen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Klasse stellt Methoden zum Vergleich von verschiedenen Tilgungsdarlehen bereit
 * 
 * @author Yannis Herbig
 */
public class DarlehenAnalytics {

    private List<Tilgungsdarlehen> tilgungsdarlehenList;

    public DarlehenAnalytics(){
        this.tilgungsdarlehenList = new ArrayList<>();
    }

    public List<Tilgungsdarlehen> getTilgungsdarlehenList() {
        return tilgungsdarlehenList;
    }

    public void sortiereTilgungsdarlehenNachGesamtschuld(){
        this.tilgungsdarlehenList =
            tilgungsdarlehenList.parallelStream()
            .sorted(Comparator.comparingDouble(Tilgungsdarlehen::getGesamtschuld))
            .collect(Collectors.toList());
    }

    public OptionalDouble getGesamtschuldenMin(){
        return tilgungsdarlehenList.stream().mapToDouble(Tilgungsdarlehen::getGesamtschuld).min();
    }

    public OptionalDouble getGesamtschuldenMax(){
        return tilgungsdarlehenList.stream().mapToDouble(Tilgungsdarlehen::getGesamtschuld).max();
    }

    public double getGesamtschuldenTotal(){
        return tilgungsdarlehenList.stream().mapToDouble(Tilgungsdarlehen::getGesamtschuld).sum();
    }

    public OptionalDouble getGesamtschuldenAverage(){
        return tilgungsdarlehenList.stream()
            .mapToDouble(Tilgungsdarlehen::getGesamtschuld).average();
    }
}
