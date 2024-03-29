package net.lecousin.ant.tools.registry.elements;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.lecousin.ant.tools.registry.RegistryElementVersion;

@Data
@AllArgsConstructor
public class MavenPackageVersion implements RegistryElementVersion {

	private String version;
	
}
