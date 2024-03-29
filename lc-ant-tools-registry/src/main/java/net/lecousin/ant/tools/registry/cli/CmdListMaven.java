package net.lecousin.ant.tools.registry.cli;

import org.springframework.boot.ApplicationArguments;

import net.lecousin.ant.tools.registry.RegistryElement.Type;
import reactor.core.publisher.Flux;

public class CmdListMaven {

	public static void execute(RegistryCli cli, ApplicationArguments args) {
		cli.getRegistries(args).stream().map(registry -> registry.list(Type.MAVEN)).flatMap(Flux::toStream)
		.forEach(element -> System.out.println(element));
	}
}
