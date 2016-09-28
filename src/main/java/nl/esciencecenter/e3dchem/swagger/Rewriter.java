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
        Map<String, Object> rewrites = (Map<String, Object>) yaml.load(rewrites_stream);

        if (rewrites.containsKey("response2array")) {
            List<String> responses2array = (List<String>) rewrites.get("response2array");
            rewrite_response2array(spec, responses2array);
        }

        // TODO set response type
        // TODO set type of field
        // TODO add definition

        try (PrintWriter out = new PrintWriter(args[2])) {
            out.print(Json.pretty(spec));
        }
    }

    /**
     * Wrap response type with array
     *
     * responses2array:
     * - /services/proteinfamily/
     * Will change
     * "type": "ProteinFamilySerializer"
     * into
     * "type": "array", "items": {"type": "ProteinFamilySerializer"}
     *
     * @param spec
     * @param rewrites
     * @throws Exception
     */
    private static void rewrite_response2array(Swagger spec, List<String> rewrites) throws Exception {
        for (String path_name : rewrites) {
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
