package nl.esciencecenter.e3dchem.swagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import org.yaml.snakeyaml.Yaml;

public class Rewriter {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new Exception("Require 3 arguments: <swagger input location> <rewrite yaml> <swagger output filename>");
        }
        SwaggerParser parser = new SwaggerParser();
        String input_spec = args[0];
        Swagger spec = parser.read(input_spec);
        String rewrites_fn = args[1];
        Yaml yaml = new Yaml();
        FileInputStream rewrites_stream = new FileInputStream(new File(rewrites_fn));
        Map<String, List<String>> rewrites = (Map<String, List<String>>) yaml.load(rewrites_stream);
        // rewrites is yaml file with rewrites
        // responses2array:
        // - /services/proteinfamily/
        // Will change
        // "type": "ProteinFamilySerializer"
        // into
        // "type": "array", "items": {"type": "ProteinFamilySerializer"}

        rewrite_response2array(spec, rewrites);

        try (PrintWriter out = new PrintWriter(args[2])) {
            out.print(Json.pretty(spec));
        }
    }

    private static void rewrite_response2array(Swagger spec, Map<String, List<String>> rewrites) throws Exception {
        List<String> responses2array = rewrites.get("responses2array");
        for (String path_name : responses2array) {
            System.out.println(path_name);
            Path path = spec.getPath(path_name);
            if (path == null) {
                throw new Exception("Path " + path_name + " not found");
            }
            Operation operation = path.getGet();
            Response response = operation.getResponses().get("default");
            Property orig_schema = response.getSchema();
            Property array_prop = new ArrayProperty(orig_schema);
            response.setSchema(array_prop);
        }
    }
}
