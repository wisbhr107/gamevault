package cat.pratfp.gamevault.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/**
 * El magatzem de fitxers de GameVault: una carpeta base amb tres subcarpetes,
 * import, export i backup.
 */
public class Magatzem {

    private static final DateTimeFormatter MARCA = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Path base;

    /**
     * Crea un magatzem sobre una carpeta base. No toca el disc.
     *
     * @param base carpeta arrel del magatzem (per exemple, Path.of("dades"))
     */
    public Magatzem(Path base) {
        this.base = base;
    }

    /** @return la carpeta d'importació (base/import) */
    public Path importacio() {
        return base.resolve("import");
    }

    /** @return la carpeta d'exportació (base/export) */
    public Path exportacio() {
        return base.resolve("export");
    }

    /** @return la carpeta de còpies de seguretat (base/backup) */
    public Path copies() {
        return base.resolve("backup");
    }

    /**
     * Crea les carpetes import, export i backup del magatzem.
     * Si ja existeixen no falla.
     *
     * @throws IOException si no es pot crear alguna carpeta
     */
    public void preparaEstructura() throws IOException {
        Files.createDirectories(importacio());
        Files.createDirectories(exportacio());
        Files.createDirectories(copies());
    }

    /**
     * Fa una còpia del fitxer a la carpeta backup amb el nom nom_marca.extensió.
     * L'original no es toca. Si la còpia ja existeix, la substitueix.
     *
     * @param fitxer fitxer que es vol copiar
     * @return la ruta de la còpia
     * @throws IOException si no es pot fer la còpia
     */
    public Path copiaSeguretat(Path fitxer) throws IOException {
        String nom = fitxer.getFileName().toString();
        int punt = nom.lastIndexOf('.');

        String nomNou;
        if (punt == -1) {
            nomNou = nom + "_" + marcaDeTemps();
        } else {
            String sense = nom.substring(0, punt);
            String extensio = nom.substring(punt);
            nomNou = sense + "_" + marcaDeTemps() + extensio;
        }

        Files.createDirectories(copies());
        Path desti = copies().resolve(nomNou);
        Files.copy(fitxer, desti, StandardCopyOption.REPLACE_EXISTING);
        return desti;
    }

    /**
     * Suma la mida en bytes de tots els fitxers del magatzem, també els de les subcarpetes.
     * Les carpetes no compten.
     *
     * @return la mida total en bytes
     * @throws IOException si no es pot recórrer la carpeta
     */
    public long midaTotal() throws IOException {
        long total = 0;
        try (Stream<Path> camins = Files.walk(base)) {
            for (Path p : camins.toList()) {
                if (Files.isRegularFile(p)) {
                    total += Files.size(p);
                }
            }
        }
        return total;
    }

    /**
     * Llista els fitxers regulars del magatzem amb la seva mida, ordenats per ruta.
     *
     * @return una línia per fitxer, amb el format "ruta relativa (N bytes)"
     * @throws IOException si no es pot recórrer la carpeta
     */
    public List<String> inventari() throws IOException {
        try (Stream<Path> camins = Files.walk(base)) {
            return camins.filter(Files::isRegularFile)
                    .sorted()
                    .map(p -> base.relativize(p) + " (" + mida(p) + " bytes)")
                    .toList();
        }
    }

    /** Mida d'un fitxer sense excepció comprovada, per poder-la fer servir dins d'un map. */
    private static long mida(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Marca de temps per als noms de les còpies.
     *
     * @return la data i hora actuals amb el format yyyyMMdd-HHmmss
     */
    public static String marcaDeTemps() {
        return LocalDateTime.now().format(MARCA);
    }
}