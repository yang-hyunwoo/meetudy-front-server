package front.meetudy.repository.contact.qna;

import front.meetudy.domain.contact.Qna.QnaBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<QnaBoard, Long> {

}
