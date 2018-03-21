import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class HelloWorld implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.printf("Hello World my getName is %s", project.getName());
        String displayName = project.getDisplayName();
        if (displayName == null)
            System.out.println("Hello World my displayName is null");
        else
            System.out.printf("Hello World my displayName is %s", displayName);
    }
}