package front.meetudy.repository.contact.faq;

import front.meetudy.domain.contact.faq.FaqBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaqRepository extends JpaRepository<FaqBoard ,Long> {


}
