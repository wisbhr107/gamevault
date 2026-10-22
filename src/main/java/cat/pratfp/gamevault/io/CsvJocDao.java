package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;
import cat.pratfp.gamevault.model.ResultatCarrega;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class CsvJocDao {

    /** Capçalera del fitxer CSV, en aquest ordre. */
    public static final String[] CAPCALERA =
            {"id", "titol", "plataforma", "estudi", "dataSortida", "horesJugades", "nota"};

    private static final char SEPARADOR = ';';

    /**

     * @param p 
     * @return 
     * @throws IOException 
     */
    public ResultatCarrega llegeix(Path p) throws IOException {
        List<Joc> jocs = new ArrayList<>();
        List<String> incidencies = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withSeparator(SEPARADOR).withQuoteChar('"').build();

        try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8);
             CSVReader csv = new CSVReaderBuilder(r).withCSVParser(parser).withSkipLines(1).build()) {

            String[] c;
            int linia = 1;                      // la línia 1 és la capçalera
            while ((c = csv.readNext()) != null) {
                linia++;

                if (c.length < 7) {
                    incidencies.add("línia " + linia + ": incompleta");
                    continue;
                }

                try {
                    int id = Integer.parseInt(c[0].trim());
                    String titol = c[1];
                    String plataforma = c[2];
                    String estudi = c[3];
                    LocalDate dataSortida = LocalDate.parse(c[4].trim());
                    double hores = Double.parseDouble(c[5].trim());
                    double nota = Double.parseDouble(c[6].trim());

                    jocs.add(new Joc(id, titol, plataforma, estudi, dataSortida, hores, nota));
                } catch (DateTimeParseException e) {
                    incidencies.add("línia " + linia + ": data mal formada");
                } catch (NumberFormatException e) {
                    incidencies.add("línia " + linia + ": número invàlid");
                } catch (IllegalArgumentException e) {
                    incidencies.add("línia " + linia + ": " + e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("CSV mal format a " + p, e);
        }
        return new ResultatCarrega(jocs, incidencies);
    }

    /**
     * @param p  
     * @param jocs 
     * @throws IOException 
     */
    public void escriu(Path p, List<Joc> jocs) throws IOException {
        try (Writer w = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
             ICSVWriter csv = new CSVWriter(w, SEPARADOR,
                     ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                     ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     ICSVWriter.DEFAULT_LINE_END)) {
            csv.writeNext(CAPCALERA, false);
            for (Joc j : jocs) {
                csv.writeNext(aCamps(j), false);
            }
        }
    }

    /**
     * @param j
     * @return 
     */
    static String[] aCamps(Joc j) {
        return new String[]{String.valueOf(j.id()), j.titol(), j.plataforma(), j.estudi(),
                j.dataSortida().toString(), String.valueOf(j.horesJugades()), String.valueOf(j.nota())};
    }
}