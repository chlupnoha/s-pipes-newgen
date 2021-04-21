package og_spipes.rest;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application.properties")
public class FormControllerTest {

    //TODO resolver blicking test

    @Value("${repositoryUrl}")
    private String repositoryUrl;

    @Autowired
    private MockMvc mockMvc;

    /**
     * This test requires whole sample folder because it contains necessary import dependencies.
     * @throws Exception
     */
    @BeforeEach
    public void init() throws Exception {
        File scriptsHomeTmp = new File(repositoryUrl);
        if(scriptsHomeTmp.exists()){
            FileSystemUtils.deleteRecursively(scriptsHomeTmp);
            Files.createDirectory(Paths.get(scriptsHomeTmp.toURI()));
        }
        FileUtils.copyDirectory(new File("src/test/resources/scripts_test/sample/"), scriptsHomeTmp);
    }

    @Test
    @DisplayName("Get parsed s-form")
    public void testGetScriptModuleTypes() throws Exception {
        String tmpScripts = repositoryUrl + "/hello-world/hello-world.sms.ttl";

        this.mockMvc.perform(post("/scripts/forms")
                .content(
                        "{" +
                            "\"@type\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/question-dto\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-type-uri\": \"http://topbraid.org/sparqlmotionlib#ApplyConstruct\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-module-uri\": \"http://onto.fel.cvut.cz/ontologies/s-pipes/hello-world-example-0.1/construct-greeding\"," +
                            "\"http://onto.fel.cvut.cz/ontologies/s-pipes/has-script-path\": \"" + tmpScripts +"\"" +
                        "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        //TODO better comparsion
    }

    @Test
    @DisplayName("Update hello-world.sms.ttl :bind-firstname label to 'Bind person name karel'.")
    public void testEditForm() throws Exception {
        String tmpScripts = repositoryUrl + "/hello-world/hello-world.sms.ttl";
        String jsonSForms = readFileToString(new File("src/test/resources/sforms/sforms_update.json"), "UTF-8");
        jsonSForms = jsonSForms.replace("SCRIPT_PATH", tmpScripts);

        this.mockMvc.perform(post("/scripts/forms/answers")
                .content(jsonSForms)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Model expectedModel = ModelFactory.createDefaultModel().read("src/test/resources/sforms/hello-world.sms.ttl");
        Model resModel = ModelFactory.createDefaultModel().read(tmpScripts);

        //form2Script delete imports
        //viz. https://email.seznam.cz/?hp#search/blasko%20prefix%3Aform/89014 - konversation
        Assert.assertTrue(expectedModel.isIsomorphicWith(resModel));
    }

    @AfterEach
    public void after() {
        FileSystemUtils.deleteRecursively(new File(repositoryUrl));
    }

}