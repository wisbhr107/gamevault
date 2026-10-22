package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cau binària del catàleg: desa la llista sencera amb serialització Java per recuperar-la ràpidament.
 * Només la pot llegir Java: no és un format d'intercanvi.
 */
public class CauCataleg {

    /**
     * Desa la llista de jocs al fitxer amb serialització Java.
     * Es desa sempre un ArrayList perquè el tipus sigui el mateix.
     *
     * @param p    fitxer .ser de destí
     * @param jocs jocs que s'han de desar
     * @throws IOException si no es pot escriure al fitxer
     */
    public void desa(Path p, List<Joc> jocs) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(p))) {
            out.writeObject(new ArrayList<>(jocs));
        }
    }

    /**
     * Recupera el catàleg desat amb {@link #desa(Path, List)}.
     *
     * @param p fitxer .ser
     * @return la llista de jocs recuperada
     * @throws IOException si el fitxer no existeix, està corromput o la classe no es troba
     */
    @SuppressWarnings("unchecked")
    public List<Joc> carrega(Path p) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(p))) {
            return (List<Joc>) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("No s'ha trobat la classe a " + p.getFileName(), e);
        }
    }
}