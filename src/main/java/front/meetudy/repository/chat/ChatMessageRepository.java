package front.meetudy.repository.chat;

import front.meetudy.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage , Long> {

    Page<ChatMessage> findByStudyGroupIdOrderBySentAtDesc(Pageable pageable, Long studyGroupId);

}
