package net.lecousin.ant.tools.registry.github;

import org.springframework.stereotype.Component;

import net.lecousin.ant.tools.registry.Registry;
import net.lecousin.ant.tools.registry.RegistryFactory;

@Component
public class GitHubRegistryFactory implements RegistryFactory {

	@Override
	public String getType() {
		return "github";
	}
	
	@Override
	public Registry build(String params) {
		int i = params.indexOf(':');
		return new GitHubRegistry(params.substring(0, i), params.substring(i + 1));
	}
	
}
