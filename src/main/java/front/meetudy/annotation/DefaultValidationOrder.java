package front.meetudy.annotation;


import static front.meetudy.annotation.ValidationGroups.*;

/**
 * dto에서 validtaion 순서를 위한 정렬 클래스
 * 추가 하기 위해선 ValidationGroups.java에 추가 후 추가하여야 함
 */
public final class DefaultValidationOrder {
    public static final Class<?>[] ORDER = {
            Step1.class,
            Step2.class,
            Step3.class,
            Step4.class,
            Step5.class,
            Step6.class,
            Step7.class,
            Step8.class,
            Step9.class,
            Step10.class,
    };
}
