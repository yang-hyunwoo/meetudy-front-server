package front.meetudy.repository.chat;

import front.meetudy.domain.chat.ChatDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatDocumentRepository extends JpaRepository<ChatDocument, Long> {

    /**
     * 채팅방 자료 리스트 조회
     *
     * @param studyGroupId 스터디 그룹 id
     * @return 채팅방 자료 리스트 객체
     */
    @Query("""
                SELECT cd
                FROM ChatDocument cd
                JOIN FETCH cd.member
                JOIN FETCH cd.files f
                JOIN FETCH f.filesDetails
                WHERE cd.studyGroupId = :studyGroupId
                 AND f.deleted=false
                  ORDER BY cd.id DESC
            """)
    List<ChatDocument> findChatDocumentList(Long studyGroupId);

}
