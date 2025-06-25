package front.meetudy.repository.chat;

import front.meetudy.domain.chat.ChatNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatNoticeRepository extends JpaRepository<ChatNotice, Long> {

    /**
     * 채팅방 공지 사항 리스트 조회
     * @param studyGroupId 스터디 그룹 id
     * @return 채팅방 공지 사항 리스트 객체
     */
    @Query("SELECT cn FROM ChatNotice cn " +
            "WHERE cn.studyGroupId = :studyGroupId " +
            "AND cn.deleted = false" +
            " ORDER BY cn.id DESC ")
    List<ChatNotice> findChatNoticeList(Long studyGroupId);

}
