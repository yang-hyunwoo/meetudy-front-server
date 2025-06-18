package front.meetudy.repository.contact.faq;

import front.meetudy.domain.contact.faq.FaqBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqBoard ,Long> {


}
