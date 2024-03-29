package net.lecousin.ant.tools.registry;

public interface RegistryFactory {

	String getType();
	
	Registry build(String params);
	
}
