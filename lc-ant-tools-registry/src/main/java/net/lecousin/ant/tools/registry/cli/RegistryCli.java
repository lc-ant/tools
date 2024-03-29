package net.lecousin.ant.tools.registry.cli;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

import lombok.Setter;
import net.lecousin.ant.tools.registry.Registry;
import net.lecousin.ant.tools.registry.RegistryFactory;

@SpringBootApplication
@ComponentScan(basePackages = "net.lecousin.ant.tools")
public class RegistryCli implements ApplicationRunner, ApplicationContextAware {

	public static void main(String[] args) {
		SpringApplication.run(RegistryCli.class, args);
	}
	
	@Setter
	private ApplicationContext applicationContext;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		String command = args.getSourceArgs()[0];
		switch (command) {
		case "list-maven": CmdListMaven.execute(this, args); break;
		case "list-maven-versions": CmdListMavenVersions.execute(this, args); break;
		default: throw new RuntimeException("Unknown command " + command);
		}
	}
	
	public List<Registry> getRegistries(ApplicationArguments args) {
		return args.getOptionValues("registry").stream()
			.map(this::getRegistry)
			.toList();
	}
	
	public Registry getRegistry(String params) {
		int i = params.indexOf(':');
		String type = params.substring(0, i);
		params = params.substring(i + 1);
		for (RegistryFactory factory : applicationContext.getBeansOfType(RegistryFactory.class).values())
			if (factory.getType().equals(type))
				return factory.build(params);
		throw new RuntimeException("Unknown registry type " + type);
	}
	
	
	
}
