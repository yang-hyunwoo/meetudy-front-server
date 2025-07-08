package front.meetudy.user.service.chat;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

    private final Map<Long, Map<String, Long>> roomSessionMap = new ConcurrentHashMap<>();

    /**
     * 멤버 추가
     *
     * @param studyGroupId 그룹 id
     * @param sessionId 세션 id
     * @param memberId 멤버 id
     */
    public void addMember(Long studyGroupId,
                          String sessionId,
                          Long memberId
    ) {
        roomSessionMap.computeIfAbsent(studyGroupId, k -> new ConcurrentHashMap<>())
                .put(sessionId, memberId);
    }

    /**
     * 멤버 삭제
     *
     * @param sessionId 세션 id
     */
    public void removeUser(String sessionId) {
        for(Map<String , Long> sessionMap : roomSessionMap.values()) {
            sessionMap.remove(sessionId);
        }
    }

    /**
     * 접속중인 멤버 조회
     *
     * @param studyGroupId 그룹 id
     * @return
     */
    public List<Long> getOnlineUserIds(Long studyGroupId) {
        return roomSessionMap
                .getOrDefault(studyGroupId, Collections.emptyMap())
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Deprecated
    public Optional<Long> findStudyGroupIdBySessionId(String sessionId) {
        return roomSessionMap.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(sessionId))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Deprecated
    public boolean isUserCompletelyOffline(Long memberId, Long studyGroupId) {
        Map<String, Long> sessionMap = roomSessionMap.getOrDefault(studyGroupId, Collections.emptyMap());
        return sessionMap.values().stream().noneMatch(id -> id.equals(memberId));
    }

}
