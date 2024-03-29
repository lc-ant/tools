package net.lecousin.ant.tools.registry.github;

import java.util.List;
import java.util.Optional;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.tools.registry.Registry;
import net.lecousin.ant.tools.registry.RegistryElement;
import net.lecousin.ant.tools.registry.RegistryElementVersion;
import net.lecousin.ant.tools.registry.RegistryElement.Type;
import net.lecousin.ant.tools.registry.elements.DockerImage;
import net.lecousin.ant.tools.registry.elements.MavenPackage;
import net.lecousin.ant.tools.registry.elements.MavenPackageVersion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@RequiredArgsConstructor
public class GitHubRegistry implements Registry {

	private final String organization;
	private final String token;
	
	@Override
	public Flux<RegistryElement> list(Type elementType) {
		return getPackageType(elementType)
			.map(packageType ->
				requestPages(buildClient(), "/packages?package_type=" + packageType + "&", GitHubPackageDescriptor.class)
				.map(this::toElement)
			).orElse(Flux.empty());
	}
	
	@Override
	public Flux<RegistryElementVersion> listVersions(RegistryElement element) {
		return listVersions(element.getType(), element.getName());
	}
	
	@Override
	public Flux<RegistryElementVersion> listVersions(Type elementType, String elementName) {
		return getPackageType(elementType)
			.map(packageType ->
				requestPages(buildClient(), "/packages/" + packageType + "/" + elementName + "/versions?", GitHubPackageVersionDescriptor.class)
				.map(v -> toElementVersion(v, elementType))
			).orElse(Flux.empty());
	}
	
	private WebClient buildClient() {
		return WebClient.builder()
			.baseUrl("https://api.github.com/orgs/" + organization)
			.defaultHeader("Authorization", "Bearer " + token)
			.defaultHeader("X-GitHub-Api-Version", "2022-11-28")
			.defaultHeader("Accept", "application/vnd.github+json")
			.build();
	}
	
	private Optional<String> getPackageType(Type elementType) {
		switch (elementType) {
		case MAVEN: return Optional.of("maven");
		case DOCKER: return Optional.of("container");
		default: return Optional.empty();
		}
	}
	
	private <T> Flux<T> requestPages(WebClient client, String url, Class<T> elementType) {
		return requestPage(client, url, 1, elementType)
			.expand(pageResult -> {
				if (pageResult.getT2().size() < 100) return Mono.empty();
				return requestPage(client, url, pageResult.getT1() + 1, elementType);
			})
			.flatMapIterable(pageResult -> pageResult.getT2());
	}
	
	private <T> Mono<Tuple2<Integer, List<T>>> requestPage(WebClient client, String url, int page, Class<T> elementType) {
		return client.get().uri(url + "page=" + page + "&per_page=100")
		.exchangeToMono(response -> {
			if (response.statusCode().is2xxSuccessful())
				return response.bodyToFlux(elementType).collectList();
			response.headers().asHttpHeaders().forEach((k,v) -> System.err.println(k + ": " + v));
			return response.bodyToMono(String.class)
				.doOnSuccess(s -> System.err.println(s))
				.then(response.createError());
		})
		.map(list -> Tuples.of(page, list));
	}
	
	private RegistryElement toElement(GitHubPackageDescriptor descriptor) {
		if ("maven".equals(descriptor.getPackageType()))
			return new MavenPackage(this, descriptor.getName(), descriptor.getUrl());
		if ("container".equals(descriptor.getPackageType()))
			return new DockerImage(this, descriptor.getName(), descriptor.getUrl());
		throw new RuntimeException("Unexpected package type: " + descriptor);
	}
	
	private RegistryElementVersion toElementVersion(GitHubPackageVersionDescriptor descriptor, Type elementType) {
		if (Type.MAVEN.equals(elementType))
			return new MavenPackageVersion(descriptor.getName());
		throw new RuntimeException("Unexpected package type: " + elementType);
	}
	
}
