package accelerate.spring;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;
import static accelerate.util.AppUtil.isEmpty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import accelerate.cache.AccelerateCache;
import accelerate.exception.AccelerateException;
import accelerate.exception.AccelerateRuntimeException;
import accelerate.util.AppUtil;
import accelerate.util.ReflectionUtil;
import accelerate.util.StringUtil;

/**
 * Utility class that handles Spring context load event.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Dec 28, 2009
 */
@Component
@Profile("accelerate")
public class StaticListenerHelper implements ApplicationListener<ApplicationReadyEvent>, Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of classes annotated with @AccelerateContextListener. Static
	 * reference is stores to avoid scanning classpath multiple times as it is
	 * an expensive operation
	 */
	private static Set<BeanDefinition> contextListeners = null;

	/**
	 * List of classes annotated with @StaticCacheListener. Static reference is
	 * stores to avoid scanning classpath multiple times as it is an expensive
	 * operation
	 */
	private static Set<BeanDefinition> cacheListeners = null;

	/**
	 * static {@link ApplicationContext} instance, to provide spring beans
	 * access to all classes.
	 */
	@Autowired
	private transient ApplicationContext applicationContext = null;

	/**
	 * {@link AccelerateProperties} instance
	 */
	@Autowired
	private AccelerateProperties accelerateProperties = null;

	/**
	 * This method is called to notify that the application is shutting down or
	 * the context has been destroyed.
	 */
	@PreDestroy
	public void destroy() {
		try {
			notifyContextListener("onContextClosed", this.applicationContext);
		} catch (AccelerateException error) {
			throw new AccelerateRuntimeException(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	/**
	 * This method initializes Accelerate components to register that a new
	 * {@link BeanFactory} has been initialized. It notifies any custom
	 * implementation of {@link StaticContextListener} provided by the
	 * application. It then notifies any static classes that maybe registered as
	 * a listener for spring context initialization or {@link AccelerateCache}
	 * reload event.
	 * 
	 * @param aEvent
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent aEvent) {
		try {
			notifyContextListener("onContextStarted", this.applicationContext);
		} catch (AccelerateException error) {
			throw new AccelerateRuntimeException(error);
		}
	}

	/**
	 * @param aHandlerKey
	 * @param aHandlerArg
	 * @throws AccelerateException
	 */
	private void notifyContextListener(String aHandlerKey, Object aHandlerArg) throws AccelerateException {
		try {
			if (contextListeners == null) {
				synchronized (this) {
					if (contextListeners == null) {
						ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
								false);
						provider.addIncludeFilter(new AnnotationTypeFilter(StaticContextListener.class));

						contextListeners = new HashSet<>(provider.findCandidateComponents("accelerate.*"));
						String[] packages = StringUtil.split(this.accelerateProperties.getAppBasePackage(), COMMA_CHAR);
						for (String packageStr : packages) {
							if (!AppUtil.compare(packageStr, "accelerate")) {
								contextListeners.addAll(provider
										.findCandidateComponents(this.accelerateProperties.getAppBasePackage()));
							}
						}
					}
				}
			}

			for (BeanDefinition beanDefinition : contextListeners) {
				Object metadata = ReflectionUtil.getFieldValue(beanDefinition.getClass(), beanDefinition, "metadata");
				@SuppressWarnings("unchecked")
				Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtil
						.getFieldValue(metadata.getClass(), metadata, "attributesMap");
				LinkedList<AnnotationAttributes> annotationAttributeList = attributesMap
						.get(StaticContextListener.class.getName());

				Class<?> targetClass = Class.forName(beanDefinition.getBeanClassName());
				String handlerName = annotationAttributeList.get(0).getString(aHandlerKey);

				ReflectionUtil.invokeMethod(targetClass, null, handlerName, new Class<?>[] { aHandlerArg.getClass() },
						new Object[] { aHandlerArg });
			}
		} catch (ClassNotFoundException error) {
			throw new AccelerateException(error);
		}
	}

	/**
	 * @param aCache
	 * @throws AccelerateException
	 */
	public void notifyCacheLoad(AccelerateCache<?, ?> aCache) throws AccelerateException {
		try {
			if (cacheListeners == null) {
				synchronized (this) {
					if (cacheListeners == null) {
						ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
								false);
						provider.addIncludeFilter(new AnnotationTypeFilter(StaticCacheListener.class));

						cacheListeners = new HashSet<>(provider.findCandidateComponents("accelerate.*"));
						if (!isEmpty(this.accelerateProperties.getAppBasePackage())) {
							cacheListeners.addAll(
									provider.findCandidateComponents(this.accelerateProperties.getAppBasePackage()));
						}
					}
				}
			}

			for (BeanDefinition beanDefinition : cacheListeners) {
				Object metadata = ReflectionUtil.getFieldValue(beanDefinition.getClass(), beanDefinition, "metadata");
				@SuppressWarnings("unchecked")
				Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtil
						.getFieldValue(metadata.getClass(), metadata, "attributesMap");
				LinkedList<AnnotationAttributes> annotationAttributeList = attributesMap
						.get(StaticCacheListener.class.getName());
				AnnotationAttributes annotationAttributes = annotationAttributeList.get(0);
				String cacheName = annotationAttributes.getString("name");
				String handlerName = annotationAttributes.getString("handler");
				Class<?> targetClass = Class.forName(beanDefinition.getBeanClassName());

				if (AppUtil.compare(cacheName, aCache.name())) {
					ReflectionUtil.invokeMethod(targetClass, null, handlerName, new Class<?>[] { aCache.getClass() },
							new Object[] { aCache });
				}
			}
		} catch (ClassNotFoundException error) {
			throw new AccelerateException(error);
		}
	}
}