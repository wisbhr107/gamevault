package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Lectura i escriptura del catàleg en JSON amb Jackson.
 */
public class JsonJocDao {

    private final ObjectMapper mapper = creaMapper();

    /**
     * Crea l'ObjectMapper configurat: entén LocalDate, escriu les dates com "2017-02-24"
     * i no falla si el JSON porta camps que Joc no té.
     *
     * @return l'ObjectMapper configurat
     */
    private static ObjectMapper creaMapper() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return m;
    }

    /**
     * Escriu la llista de jocs al fitxer en JSON indentat.
     *
     * @param p    fitxer JSON de destí
     * @param jocs jocs que s'han d'escriure
     * @throws IOException si no es pot escriure al fitxer
     */
    public void escriu(Path p, List<Joc> jocs) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(p.toFile(), jocs);
    }

    /**
     * Llegeix el fitxer JSON i retorna la llista de jocs.
     *
     * @param p fitxer JSON d'origen
     * @return la llista de jocs llegida
     * @throws IOException si el fitxer no existeix o el JSON no és vàlid
     */
    public List<Joc> llegeix(Path p) throws IOException {
        return mapper.readValue(p.toFile(), new TypeReference<List<Joc>>() {});
    }
}