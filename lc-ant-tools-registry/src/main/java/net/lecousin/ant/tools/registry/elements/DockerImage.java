package net.lecousin.ant.tools.registry.elements;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.lecousin.ant.tools.registry.Registry;
import net.lecousin.ant.tools.registry.RegistryElement;

@Data
@AllArgsConstructor
public class DockerImage implements RegistryElement {

	public Type getType() {
		return Type.DOCKER;
	}
	
	private final Registry registry;
	private final String name;
	private final String url;
	
}
