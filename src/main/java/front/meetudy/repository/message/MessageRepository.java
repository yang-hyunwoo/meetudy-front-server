package front.meetudy.repository.message;

import front.meetudy.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message , Long> {

    Page<Message> findByReceiverIdAndDeletedOrderByCreatedAtDesc(Pageable pageable, Long receiverId, boolean deleted);

    Page<Message> findBySenderIdAndDeletedOrderByCreatedAtDesc(Pageable pageable, Long senderId, boolean deleted);

    Optional<Message> findByIdAndReceiverId(Long messageId, Long receiverId);

}
