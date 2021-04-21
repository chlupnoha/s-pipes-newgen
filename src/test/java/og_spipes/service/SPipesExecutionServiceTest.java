package og_spipes.service;

import og_spipes.model.spipes.ExecutionDTO;
import og_spipes.model.spipes.TransformationDTO;
import og_spipes.persistence.dao.ScriptDAO;
import og_spipes.persistence.dao.TransformationDAO;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SPipesExecutionServiceTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    private final ScriptDAO scriptDAO = Mockito.mock(ScriptDAO.class);

    private final TransformationDAO transformationDAO = Mockito.mock(TransformationDAO.class);

    private final SPipesExecutionService service = new SPipesExecutionService("http://localhost:1111", restTemplate, transformationDAO, scriptDAO);

    @Test
    public void serviceExecution(){
        when(restTemplate.getForEntity(
                "http://localhost:1111/service?id=execute-greeding",
                String.class,
                new HashMap<String, String>() {{
                    put("firstName","karel");
                    put("repositoryName","http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1");
                }})
        ).thenReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED));

        ResponseEntity<String> entity = service.serviceExecution(
                "execute-greeding",
                "http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1",
                new HashMap<String, String>() {{
                    put("firstName","karel");
                }}
        );

        assertEquals(new ResponseEntity<>("body", HttpStatus.ACCEPTED), entity);
    }


    @Test
    public void getAllExecution() throws IOException {
        Map<String, Set<Object>> properties = new HashMap<>();
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-name", Collections.singleton("http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.3"));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-start-date", Collections.singleton(new Date(1619039405731L)));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-finish-date", Collections.singleton(new Date(1619039432986L)));
        properties.put("http://onto.fel.cvut.cz/ontologies/s-pipes/has-pipeline-execution-duration", Collections.singleton(642));
        when(transformationDAO.getAllExecutionTransformation()).thenReturn(Stream.of(
                new TransformationDTO("http://onto.fel.cvut.cz/ontologies/dataset-descriptor/transformation/1618874296751000", properties)
        ).collect(Collectors.toList()));
        File file = new File(getClass().getClassLoader().getResource("scripts_test/sample/hello-world/hello-world2.sms.ttl").getFile());
        when(scriptDAO.findScriptByOntologyName(any())).thenReturn(file);

        List<ExecutionDTO> allExecution = service.getAllExecution();

        assertEquals(1, allExecution.size());
    }

}