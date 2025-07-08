package front.meetudy.user.repository.chat;

import front.meetudy.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage , Long> {

    /**
     * 채팅방 메시지 페이징 조회
     *
     * @param pageable     페이징 정보
     * @param studyGroupId 스터디 그룹 id
     * @return 채팅방 메시지 페이징 객체
     */
    Page<ChatMessage> findByStudyGroupIdOrderBySentAtDesc(Pageable pageable,
                                                          Long studyGroupId);

}
