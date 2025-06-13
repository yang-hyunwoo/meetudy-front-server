package front.meetudy.repository.chat;

import front.meetudy.domain.chat.ChatDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatDocumentRepository extends JpaRepository<ChatDocument, Long> {
}
