package front.meetudy.repository.chat;

import front.meetudy.domain.chat.ChatLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatLinkRepository extends JpaRepository<ChatLink, Long> {

    /**
     * 채팅방 링크 리스트 조회
     *
     * @param studyGroupId 스터디 그룹 id
     * @return 채팅방 링크 리스트 객체
     */
    @Query("SELECT cl FROM ChatLink cl " +
            "WHERE cl.studyGroupId = :studyGroupId " +
            "AND cl.deleted = false" +
            " ORDER BY cl.id DESC ")
    List<ChatLink> findChatLinkList(Long studyGroupId);

}
