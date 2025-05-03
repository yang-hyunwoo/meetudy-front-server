package front.meetudy.annotation;


import static front.meetudy.annotation.ValidationGroups.*;

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
