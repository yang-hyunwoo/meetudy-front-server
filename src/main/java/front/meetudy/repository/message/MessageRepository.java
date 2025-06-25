package front.meetudy.repository.message;

import front.meetudy.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message , Long> {

    /**
     * 받는 쪽지함 페이징 조회
     *
     * @param pageable   페이징 정보
     * @param receiverId 받는 멤버 id
     * @param deleted    삭제 여부
     * @return 받는 쪽지함 페이징 객체
     */
    Page<Message> findByReceiverIdAndDeletedOrderByCreatedAtDesc(Pageable pageable,
                                                                 Long receiverId,
                                                                 boolean deleted);

    /**
     * 보낸 쪽지함 페이징 조회
     *
     * @param pageable 페이징 정보
     * @param senderId 보내는 멤버 id
     * @param deleted  삭제 여부
     * @return 보낸 쪽지함 페이징 객체
     */
    Page<Message> findBySenderIdAndDeletedOrderByCreatedAtDesc(Pageable pageable,
                                                               Long senderId,
                                                               boolean deleted);

    /**
     * 받는 쪽지 상세 조회
     *
     * @param messageId  쪽지 id
     * @param receiverId 받는 멤버 id
     * @return 받는 쪽지 상세 객체
     */
    Optional<Message> findByIdAndReceiverId(Long messageId,
                                            Long receiverId);

}
