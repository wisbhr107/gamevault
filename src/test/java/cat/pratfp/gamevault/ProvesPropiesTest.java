package cat.pratfp.gamevault;

import cat.pratfp.gamevault.io.IndexHores;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvesPropiesTest {

    @TempDir
    Path tmp;

    @Test
    @DisplayName("PR1-14 · llegeix les hores del segon registre")
    void llegeixHoresDelSegonRegistre() throws Exception {
        Path fitxer = tmp.resolve("index");

        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "rw")) {
            escriuRegistre(raf, 1, "Joc u", 2.5);
            escriuRegistre(raf, 2, "Joc dos", 7.25);
        }

        IndexHores index = new IndexHores(fitxer);

        assertEquals(7.25, index.llegeixHores(1));
    }

    @Test
    @DisplayName("PR1-14 · suma hores a dos registres seguits")
    void sumaHoresEnDosRegistresSeguits() throws Exception {
        Path fitxer = tmp.resolve("index");

        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "rw")) {
            escriuRegistre(raf, 1, "Joc u", 2.5);
            escriuRegistre(raf, 2, "Joc dos", 7.25);
        }

        long midaInicial = Files.size(fitxer);
        IndexHores index = new IndexHores(fitxer);

        assertEquals(4.0, index.sumaHores(0, 1.5));
        assertEquals(10.25, index.sumaHores(1, 3.0));
        assertEquals(4.0, index.llegeixHores(0));
        assertEquals(10.25, index.llegeixHores(1));
        assertEquals(midaInicial, Files.size(fitxer));
        assertEquals(2, index.nombreRegistres());
    }

    private void escriuRegistre(RandomAccessFile raf, int id, String titol, double hores) throws Exception {
        raf.writeInt(id);

        String titolFix = String.format("%-" + IndexHores.LONG_TITOL + "s", titol)
                .substring(0, IndexHores.LONG_TITOL);
        raf.writeChars(titolFix);
        raf.writeDouble(hores);
    }
}