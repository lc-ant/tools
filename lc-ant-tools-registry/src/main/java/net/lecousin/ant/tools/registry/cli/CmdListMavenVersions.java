package net.lecousin.ant.tools.registry.cli;

import org.springframework.boot.ApplicationArguments;

import net.lecousin.ant.tools.registry.RegistryElement.Type;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

public class CmdListMavenVersions {

	public static void execute(RegistryCli cli, ApplicationArguments args) {
		cli.getRegistries(args).stream()
		.flatMap(registry -> args.getOptionValues("maven-package").stream().map(pkgName -> Tuples.of(registry, pkgName)))
		.map(tuple -> tuple.getT1().listVersions(Type.MAVEN, tuple.getT2())).flatMap(Flux::toStream)
		.forEach(element -> System.out.println(element));
	}
	
}
