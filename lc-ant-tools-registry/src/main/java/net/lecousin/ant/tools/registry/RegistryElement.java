package net.lecousin.ant.tools.registry;

public interface RegistryElement {

	Type getType();
	Registry getRegistry();
	String getName();
	String getUrl();
	
	enum Type {
		MAVEN,
		DOCKER;
	}
	
}
