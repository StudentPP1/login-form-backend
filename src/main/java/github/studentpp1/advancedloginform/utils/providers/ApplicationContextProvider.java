package github.studentpp1.advancedloginform.utils.providers;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T> T bean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static Object bean(String name) {
        return context.getBean(name);
    }

    @Override
    public void setApplicationContext(
            // ignore warning
            @SuppressWarnings("NullableProblems") ApplicationContext applicationContext
    ) throws BeansException {
        context = applicationContext;
    }
}
