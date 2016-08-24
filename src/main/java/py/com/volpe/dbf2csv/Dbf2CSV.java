package py.com.volpe.dbf2csv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

public class Dbf2CSV {

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			printUsage();
			return;
		}

		new Dbf2CSV(args[0]).doIt();
	}

	String path;

	public Dbf2CSV(String string) {
		this.path = string;
	}

	private static void printUsage() {

		System.out.println("-------------------------------------------");
		System.out.println("[ERROR] Usage: mvn exec:java -Dexec.args=SOURCE");
		System.out.println("[ERROR] This will create a file with ");
		System.out.println("[ERROR] the same name but with the ");
		System.out.println("[ERROR] extension CSV in the source folder.");
		System.out.println("-------------------------------------------");

	}

	public void doIt() throws Exception {

		Files.walk(Paths.get(path))
				.filter(Files::isRegularFile)
				.filter(f -> f.toString().endsWith("dbf"))
				.map(file -> new Result(file.getFileName(), toCSV(file)))
				.forEach(this::write);
	}

	protected List<String> toCSV(Path file) {
		System.out.println("Processing: " + file.toString());
		try (InputStream fis = Files.newInputStream(file)) {
			DbfRecord rec;
			try (DbfReader reader = new DbfReader(fis)) {

				DbfMetadata meta = reader.getMetadata();
				List<String> toRet = new ArrayList<>();
				List<String> fields = meta.getFields().stream().map(f -> f.getName()).collect(Collectors.toList());

				toRet.add(fields.stream().collect(Collectors.joining(",")));

				while ((rec = reader.read()) != null) {
					rec.setStringCharset(StandardCharsets.ISO_8859_1);

					StringJoiner sj = new StringJoiner(",");
					for (String s : fields)
						sj.add(rec.getString(s));

					toRet.add(sj.toString());
				}

				return toRet;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void write(Result result) {
		String filename = path + "/" + result.name;
		System.out.println("Writing " + filename);
		try (FileWriter fw = new FileWriter(filename)) {
			BufferedWriter bw = new BufferedWriter(fw);
			for (String s : result.lines) {
				bw.write(s);
				bw.newLine();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Result {
		String name;
		List<String> lines;

		public Result(Path name, List<String> lines) {
			this.name = name.toString().replace("dbf", "csv");
			this.lines = lines;
		}

	}
}
