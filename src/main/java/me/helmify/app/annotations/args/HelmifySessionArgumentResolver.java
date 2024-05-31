package me.helmify.app.annotations.args;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.helmify.domain.ui.session.SessionInfo;
import me.helmify.domain.ui.session.SessionService;
import me.helmify.util.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

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

		String tmp = new String(request.getInputStream().readAllBytes());
		String json = tmp.isEmpty() ? "{}" : tmp;

		String session = request.getParameter("sessionId");
		Map<String, Object> map = JsonUtil.fromJson(json, Map.class);

		if (session == null) {
			if (map != null && map.containsKey("sessionId")) {
				session = map.get("sessionId").toString();
			}
		}

		SessionInfo sessionInfo = sessionService.getSession(session).orElseThrow();

		if (map != null) {
			if (map.containsKey("dockerImageRepositoryUrl"))
				sessionInfo.setDockerImageRepositoryUrl(map.get("dockerImageRepositoryUrl").toString());
			if (map.containsKey("dockerImageTag"))
				sessionInfo.setDockerImageTag(map.get("dockerImageTag").toString());
			if (map.containsKey("dockerImagePullSecret"))
				sessionInfo.setDockerImagePullSecret(map.get("dockerImagePullSecret").toString());
			if (map.containsKey("chartFlavor"))
				sessionInfo.setChartFlavor(map.get("chartFlavor").toString());
		}

		sessionService.addSession(sessionInfo);
		return sessionInfo;
	}

}
