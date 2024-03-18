package me.helmify.app.config;

import lombok.RequiredArgsConstructor;
import me.helmify.domain.ui.args.HelmifySessionArgumentResolver;
import me.helmify.domain.ui.session.SessionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfiguration implements WebMvcConfigurer {

	private final SessionService sessionService;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new HelmifySessionArgumentResolver(sessionService));
	}

}
