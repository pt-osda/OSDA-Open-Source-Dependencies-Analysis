import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class HelloWorld implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Logger logger = project.getLogger();
        logger.info(String.format("Hello World my getName is %s", project.getName()));

        try {
            PrintWriter writer = new PrintWriter("Report.txt");
            BufferedReader reader = new BufferedReader(new FileReader("Prototipo.txt"));

            String input = reader.readLine();
            StringBuilder result = new StringBuilder();

            while(input != null){
                result.append(input);
                result.append(System.lineSeparator());
                input = reader.readLine();
            }
            logger.info("Finish reading file");
            writer.print(result.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("$local");
        Set<Project> subProjects = project.getSubprojects();

        ConfigurationContainer configurationContainer = project.getConfigurations();

        for (Configuration configuration : configurationContainer) {
            logger.info(String.format("Configuration is %s", configuration.getName()));
        }

        Configuration configuration = configurationContainer.getByName("compile");

        DependencySet dependencySet = configuration.getDependencies();

        for (Dependency dependency : dependencySet) {
            logger.info(String.format("Compile Configuration Dependency %s", dependency.getName()));
        }


        int count = 1;
        for (Project subProject : subProjects) {
            logger.info(String.format("Sub Project Nr. %s is named %s", count++, subProject.getName()));
        }

        logger.info(String.format("This Project contains %s subproject", count));
    }
}