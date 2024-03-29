package net.lecousin.ant.tools.registry;

import reactor.core.publisher.Flux;

public interface Registry {
	
	Flux<RegistryElement> list(RegistryElement.Type elementType);
	
	Flux<RegistryElementVersion> listVersions(RegistryElement element);
	
	Flux<RegistryElementVersion> listVersions(RegistryElement.Type elementType, String elementName);
	
}
