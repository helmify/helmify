package me.helmify.domain.ui.session;

import lombok.extern.slf4j.Slf4j;
import me.helmify.domain.ui.model.SessionInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SessionService {

	private static final List<SessionInfo> sessions = new ArrayList<>();

	public String addSession(SessionInfo session) {
		sessions.remove(session);
		sessions.add(session);
		return session.getId();
	}

	public List<SessionInfo> getSessions() {
		return sessions;
	}

	public Optional<SessionInfo> getSession(String id) {
		return sessions.stream().filter(s -> s.getId().equals(id)).findFirst();
	}

	public Optional<SessionInfo> get(SessionInfo sessionInfo) {
		return sessions.stream().filter(s -> s.getId().equals(sessionInfo.getId())).findFirst();
	}

}
