package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Índex d'hores jugades en un fitxer binari d'accés aleatori.
 * <p>
 * Cada registre fa sempre {@value #MIDA_REGISTRE} bytes:
 * <pre>
 *   id (int, 4 bytes) | títol (40 caràcters, 80 bytes) | hores (double, 8 bytes)
 * </pre>
 * El registre de la posició n comença al byte n × MIDA_REGISTRE.
 */
public class IndexHores {

    /** Caràcters reservats per al títol (s'omple amb espais o es talla). */
    public static final int LONG_TITOL = 40;
    /** Mida fixa de cada registre en bytes: 4 + 40 × 2 + 8 = 92. */
    public static final int MIDA_REGISTRE = Integer.BYTES + LONG_TITOL * Character.BYTES + Double.BYTES;

    private final Path fitxer;

    /**
     * Crea un índex d'hores associat al fitxer indicat.
     *
     * @param fitxer ruta del fitxer binari de l'índex
     */
    public IndexHores(Path fitxer) {
        this.fitxer = fitxer;
    }

    /**
     * Escriu el registre d'un joc a la posició indicada.
     *
     * @param posicio número de registre
     * @param j joc del qual es desen l'identificador, el títol i les hores
     * @throws IOException si no es pot escriure al fitxer
     */
    public void escriu(int posicio, Joc j) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "rw")) {
            raf.seek((long) posicio * MIDA_REGISTRE);
            raf.writeInt(j.id());
            String fix = String.format("%-" + LONG_TITOL + "s", j.titol()).substring(0, LONG_TITOL);
            raf.writeChars(fix);
            raf.writeDouble(j.horesJugades());
        }
    }

    /**
     * Compta els registres complets que conté el fitxer.
     *
     * @return nombre de registres, o zero si el fitxer no existeix
     * @throws IOException si no es pot consultar la mida del fitxer
     */
    public long nombreRegistres() throws IOException {
        return Files.exists(fitxer) ? Files.size(fitxer) / MIDA_REGISTRE : 0;
    }

    /**
     * Llegeix les hores del registre indicat.
     *
     * @param posicio número del registre que es vol llegir
     * @return hores jugades del registre
     * @throws IOException si no es pot llegir el fitxer o el registre no és complet
     */
    public double llegeixHores(int posicio) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "r")) {
            long posicioHores = (long) posicio * MIDA_REGISTRE
                    + Integer.BYTES + LONG_TITOL * Character.BYTES;
            raf.seek(posicioHores);
            return raf.readDouble();
        }
    }

    /**
     * Suma hores al registre indicat i actualitza només el camp de les hores.
     *
     * @param posicio número del registre que es vol actualitzar
     * @param extra hores que cal sumar
     * @return total d'hores després de la suma
     * @throws IOException si no es pot llegir o escriure el registre
     */
    public double sumaHores(int posicio, double extra) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "rw")) {
            long posicioHores = (long) posicio * MIDA_REGISTRE
                    + Integer.BYTES + LONG_TITOL * Character.BYTES;
            raf.seek(posicioHores);
            double horesNoves = raf.readDouble() + extra;
            raf.seek(posicioHores);
            raf.writeDouble(horesNoves);
            return horesNoves;
        }
    }
}