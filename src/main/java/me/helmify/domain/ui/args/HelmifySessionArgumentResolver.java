package me.helmify.domain.ui.args;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.helmify.domain.ui.model.SessionInfo;
import me.helmify.domain.ui.session.SessionService;
import me.helmify.util.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HelmifySessionArgumentResolver implements HandlerMethodArgumentResolver {

	private final SessionService sessionService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(HelmifySession.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Map<String, Object> map = JsonUtil.fromJson(new String(request.getInputStream().readAllBytes()), Map.class);
		String sessionId = Optional.ofNullable(map.get("sessionId"))
			.orElseThrow(() -> new IllegalArgumentException("SessionId key not found"))
			.toString();
		SessionInfo sessionInfo = sessionService.getSession(sessionId)
			.orElseThrow(() -> new IllegalArgumentException("Session " + sessionId + " not found"));

		if (map.containsKey("dockerImageRepositoryUrl"))
			sessionInfo.setDockerImageRepositoryUrl(map.get("dockerImageRepositoryUrl").toString());
		if (map.containsKey("dockerImageTag"))
			sessionInfo.setDockerImageTag(map.get("dockerImageTag").toString());
		if (map.containsKey("dockerImagePullSecret"))
			sessionInfo.setDockerImagePullSecret(map.get("dockerImagePullSecret").toString());

		sessionService.addSession(sessionInfo);

		return sessionInfo;
	}

}
