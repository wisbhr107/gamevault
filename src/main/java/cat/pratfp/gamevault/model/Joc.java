package cat.pratfp.gamevault.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Un joc del catàleg GameVault.
 * <p>
 * És un record: immutable, amb constructor, accessors, equals, hashCode i toString generats.
 * El constructor compacte valida les dades abans de crear l'objecte.
 * Implementa {@link Serializable} perquè es pugui desar a la cau binària (CauCataleg).
 *
 * @param id            identificador del joc
 * @param titol         títol del joc (no pot ser null ni estar en blanc)
 * @param plataforma    plataforma on es juga
 * @param estudi        estudi desenvolupador
 * @param dataSortida   data de llançament
 * @param horesJugades  hores jugades (no pot ser negativa)
 * @param nota          nota del joc, de 0 a 10 (ambdós valors inclosos)
 * @throws IllegalArgumentException si el títol, les hores o la nota no són vàlids
 */
public record Joc(int id, String titol, String plataforma, String estudi,
                  LocalDate dataSortida, double horesJugades, double nota) implements Serializable {

    public Joc {
        if (titol == null || titol.isBlank()) {
            throw new IllegalArgumentException("el títol no pot estar buit");
        }
        if (horesJugades < 0) {
            throw new IllegalArgumentException("les hores no poden ser negatives (" + horesJugades + ")");
        }
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("la nota ha d'anar de 0 a 10 (" + nota + ")");
        }
    }
}