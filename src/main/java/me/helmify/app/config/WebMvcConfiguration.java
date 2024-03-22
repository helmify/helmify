package me.helmify.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.helmify.domain.helm.resolvers.DependencyResolver;
import me.helmify.domain.ui.counter.ChartDownloadedEvent;
import me.helmify.app.annotations.args.HelmifySessionArgumentResolver;
import me.helmify.domain.ui.session.SessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

	private final SessionService sessionService;

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
				if (response.getStatus() == 200 && response.containsHeader("Content-Disposition"))
					eventPublisher.publishEvent(new ChartDownloadedEvent());
			}
		}).addPathPatterns("/**/*");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new HelmifySessionArgumentResolver(sessionService));
	}

	@Bean
	@Qualifier("supportedDependencies")
	public String supportedDependencies(List<DependencyResolver> resolvers) {
		Set<String> supportedDependenciesList = resolvers.stream()
			.map(DependencyResolver::dependencyName)
			.filter(s -> !s.equals("actuator"))
			.filter(s -> !s.equals("web"))
			.map(StringUtils::capitalize)
			.collect(Collectors.toSet());
		return String.join(", ", supportedDependenciesList);
	}

}
